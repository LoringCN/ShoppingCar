package cn.huace.controller.admin.shop;

import cn.huace.common.bean.HttpResult;
import cn.huace.common.utils.FileUtil;
import cn.huace.common.utils.excel.ExcelUtil;
import cn.huace.controller.admin.base.AdminBasicController;
import cn.huace.shop.bluetooth.entity.BlueTooth;
import cn.huace.shop.bluetooth.service.BlueToothService;
import cn.huace.sys.bean.ShiroUser;
import cn.huace.sys.entity.SystemUser;
import cn.huace.sys.service.SystemUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RestController
@Api(value = "/admin/bluetooth", description = "蓝牙管理")
@RequestMapping(value = "/admin/bluetooth")
public class BlueToothController extends AdminBasicController {

    @Autowired
    private BlueToothService blueToothService;

    @Autowired
    private SystemUserService systemUserService;

    private static final String BLUETOOTH_TEMPLATE_FILE_NAME = "bluetooth.xls";
    private static final String BLUETOOTH_TEMPLATE_FILE_LOCATION = "static/bt_template.xls";

    @ApiOperation(value = "", notes = "查询蓝牙列表接口")
    @RequestMapping(method = RequestMethod.GET)
    public HttpResult list(Integer shopId, @RequestParam(value = "keyword", required = false) String blueToothId, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer rows) {
        Map<String, Object> searchMap = new HashMap<>();
        if (shopId != null) {
            searchMap.put("EQ_shopId", shopId);
        } else {
            ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
            SystemUser systemUser = systemUserService.findOne(user.getId());
            if (systemUser.getType() != 0) {
                String shopIds = systemUser.getShopIds();
                if (shopIds.split(",").length == 1) {
                    searchMap.put("EQ_shopId", Integer.parseInt(shopIds.trim()));
                } else {
//                    取列表的第一个的商店的数据
                    searchMap.put("EQ_shopId", Integer.parseInt(shopIds.split(",")[0]));
                }
            }
        }

        if (blueToothId != null) {
            searchMap.put("LIKE_blueToothId", blueToothId);
        }
        Page<BlueTooth> result = blueToothService.findAll(searchMap, page, rows, Sort.Direction.DESC, "id");
        return HttpResult.createSuccess("success", result);
    }

    @ApiOperation(value = "/save", notes = "添加、更新蓝牙接口")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public HttpResult save(BlueTooth blueTooth) {
        if (blueTooth.isNew()) {
            blueToothService.save(blueTooth);
        } else {
            BlueTooth old = blueToothService.findOne(blueTooth.getId());
            old.setFloorNo(blueTooth.getFloorNo());
            old.setFlag(blueTooth.getFlag());
            old.setBlueToothId(blueTooth.getBlueToothId());
            old.setShopId(blueTooth.getShopId());
            old.setAlDis(blueTooth.getAlDis());
            blueToothService.save(old);
        }
        return HttpResult.createSuccess("保存成功！");
    }

    @ApiOperation(value = "/find", notes = "根据ID查看蓝牙信息")
    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public HttpResult find(Integer id) {
        BlueTooth blueTooth = blueToothService.findOne(id);
        return HttpResult.createSuccess(blueTooth);
    }

    @ApiOperation(value = "/del", notes = "删除蓝牙信息")
    @RequestMapping(value = "/del", method = RequestMethod.GET)
    public HttpResult delete(String ids) {
        String[] idList = ids.split(",");
        for (int i = 0; i < idList.length; i++) {
            blueToothService.delete(Integer.valueOf(idList[i]));
        }
        return HttpResult.createSuccess("删除成功！");
    }


    @ApiOperation(value = "/batch/add", notes = "批量导入蓝牙信息")
    @RequestMapping(value = "/batch/add", method = RequestMethod.POST)
    public HttpResult batchAddBluetooths(@RequestParam(name = "excel") MultipartFile file, Integer shopId) {
        List<BlueTooth> blueToothList = new ArrayList<>();
        String excelFilePath = FileUtil.saveFile(file);
        ExcelUtil excelUtil = new ExcelUtil();
        try {
            List<Row> rowList = excelUtil.readExcel(excelFilePath);
            if (rowList.size() <= 1) {//只有标题栏或者什么都没有
                return HttpResult.createFAIL("excel文件没有数据！");
            }
            //删除上传的excel文件
            FileUtil.delFile(excelFilePath);
            for (int i = 1; i < rowList.size(); i++) {
                BlueTooth blueTooth = convertRow2BlueTooth(rowList.get(i), excelUtil, shopId);
                if (blueTooth != null) {
                    if (blueTooth.getFloorNo() == null) {
                        return HttpResult.createFAIL("楼层数据格式不正确，请修改后重试！");
                    }
                    if (blueTooth.getAlDis() == null) {
                        return HttpResult.createFAIL("告警距离数据格式不正确，请修改后重试！");
                    }
                    blueToothList.add(blueTooth);
                }
            }
            int result = blueToothService.batchInsert(blueToothList);
            if (result == blueToothList.size()) {
                return HttpResult.createSuccess("批量添加蓝牙数据成功！");
            }
        } catch (IOException e) {
            log.error("********* 读取excel出错！！！，error = {}", e);
        }
        return HttpResult.createFAIL("批量添加蓝牙数据失败！");
    }

    @ApiOperation(value = "/download/bluetooth/template", notes = "下载蓝牙excel模板文件")
    @RequestMapping(value = "/download/bluetooth/template", method = RequestMethod.GET)
    public void downloadBlueToothTemplate(HttpServletResponse response) {
        BufferedOutputStream out = null;
        InputStream input = null;
        try {
            out = new BufferedOutputStream(response.getOutputStream());

            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment; filename=" + BLUETOOTH_TEMPLATE_FILE_NAME);

            input = this.getClass().getClassLoader().getResourceAsStream(BLUETOOTH_TEMPLATE_FILE_LOCATION);

            if (input == null) {
                out.write("{\"state:\"1,\"msg:下载失败！找不到文件。\"}".getBytes("UTF-8"));
                out.flush();
                return;
            }

            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int size = 0;
            while ((size = input.read(buffer)) != -1) {
                out.write(buffer, 0, size);
            }

            out.flush();
            log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 成功下载文件: " + BLUETOOTH_TEMPLATE_FILE_NAME);
        } catch (Exception e) {
            log.error("**** 下载失败，error:" + e.toString(), e);
            try {
                out.write("{\"state:\"1,\"msg:下载失败！\"}".getBytes("UTF-8"));
                out.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private BlueTooth convertRow2BlueTooth(Row row, ExcelUtil excelUtil, Integer shopId) {
        if (row == null) {
            return null;
        }
        BlueTooth blueTooth = new BlueTooth();
        blueTooth.setShopId(shopId);
        //蓝牙ID
        blueTooth.setBlueToothId(excelUtil.getCellValue(row.getCell(0)));
        //蓝牙所在楼层
        String floorNo = excelUtil.getCellValue(row.getCell(1));
        if (!StringUtils.isEmpty(floorNo)) {
            try {
                Integer floor = Integer.parseInt(floorNo);
                blueTooth.setFloorNo(floor);
            } catch (Exception e) {
                log.error("******* 蓝牙所在楼层数据格式不正确！");
            }
        }
        //告警距离
        String alarmDistance = excelUtil.getCellValue(row.getCell(2));
        if (!StringUtils.isEmpty(alarmDistance)) {
            try {
                Integer alDis = Integer.parseInt(alarmDistance);
                blueTooth.setAlDis(alDis);
            } catch (Exception e) {
                log.error("******* 告警距离数据格式不正确！");
            }
        }
        blueTooth.setFlag(true);
        return blueTooth;
    }
}
