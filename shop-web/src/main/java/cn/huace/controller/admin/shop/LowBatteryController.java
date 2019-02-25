package cn.huace.controller.admin.shop;

import cn.huace.common.bean.HttpResult;
import cn.huace.common.utils.DateUtils;
import cn.huace.controller.admin.base.AdminBasicController;
import cn.huace.shop.stat.entity.LowBattery;
import cn.huace.shop.stat.service.LowBatteryService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

/**
 * Created by xiaoling on 2017/5/27.
 */
@Slf4j
@RestController
@Api(value = "/admin/lowbattery", description = "低电管理")
@RequestMapping(value = "/admin/lowbattery")
public class LowBatteryController extends AdminBasicController {

    @Autowired
    private LowBatteryService lowBatteryService;

    // get all devices,, or get one shop devices, or query by device-id/isProcess..
    @RequestMapping(method = RequestMethod.GET)
    public HttpResult list(Integer shopId, String devId, @RequestParam(name = "isProcess", required = false) Boolean isProcess,
                           @RequestParam Integer page, @RequestParam(defaultValue = "10") Integer rows, Date start, Date end) {


        log.info("shopId : " + shopId + ",page : " + page + ",rows : " + rows);

        Map<String, Object> searchMap = new HashedMap();
        if (!StringUtils.isEmpty(shopId)) {
            searchMap.put("EQ_shop.id", shopId);
        }

        if (!StringUtils.isEmpty(devId)) {
            searchMap.put("LIKE_devId", devId);
        }

        if (!StringUtils.isEmpty(isProcess)) {
            searchMap.put("EQ_isProcess", isProcess);
        }

        if (start != null) {
            searchMap.put("GTE_modifiedTime", DateUtils.getStartTime(start));
        }
        if (end != null) {
            searchMap.put("LTE_modifiedTime", DateUtils.getEndTime(end));
        }

        Page<LowBattery> pageResult = lowBatteryService.findAll(searchMap, page, rows, Sort.Direction.ASC, "modifiedTime");

        return HttpResult.createSuccess("get success", pageResult);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public HttpResult getOneRecordById(Integer id) {
        if (id == null) {
            return HttpResult.createFAIL("参数：id不能为空");
        }
        log.info("LowBatteryController::getOneRecordById() table lowbattery Id=" + id);

        LowBattery lowBattery = lowBatteryService.findOne(id);

        System.out.println(lowBattery.toString());

        return HttpResult.createSuccess("get success", lowBattery);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public HttpResult update(Integer id, boolean isProcess) {
        if (id == null) {
            return HttpResult.createFAIL("参数：id不能为空");
        }
        log.info("LowBatteryController::update() table lowbattery Id=" + id);

        LowBattery entity = lowBatteryService.findOne(id);
        if (entity == null) {
            return HttpResult.createFAIL("记录不存在，id:" + id);
        }
//        if (isProcess){
//            entity.setCount(0);
//        }
        entity.setIsProcess(isProcess);
        lowBatteryService.save(entity);

        return HttpResult.createSuccess("保存成功");
    }

    @RequestMapping(value = "/del", method = RequestMethod.POST)
    public HttpResult del(Integer id) {
        lowBatteryService.delete(id);
        return HttpResult.createSuccess("删除成功！");
    }
}
