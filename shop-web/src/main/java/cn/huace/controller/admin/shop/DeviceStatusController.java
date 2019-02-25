package cn.huace.controller.admin.shop;

import cn.huace.common.bean.HttpResult;
import cn.huace.common.utils.EStatus;
import cn.huace.controller.admin.base.AdminBasicController;
import cn.huace.shop.stat.entity.DeviceStatus;
import cn.huace.shop.stat.service.DeviceStatusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@Api(value = "/admin/heartbeat", description = "设备状态管理")
@RequestMapping(value = "/admin/heartbeat")
public class DeviceStatusController extends AdminBasicController {
    private static final long ABNORMAL_TIME_INTERVAL = 5 * 60 * 1000L;//5minutes

    @Autowired
    DeviceStatusService deviceStatusService;

    @ApiOperation(value = "/list",notes = "查询所有状态(normal、abnormal、abandon)设备接口")
    @RequestMapping(value = "/list",method = {RequestMethod.GET,RequestMethod.POST})
    public HttpResult listAllStatusDevice(
            Integer shopId,@RequestParam(required = false) String carState,
            @RequestParam Integer page, @RequestParam(defaultValue = "50") Integer rows
        ){
        log.info("*********** 调用方法：listAllStatusDevice，参数：carState = {}",carState);
        Map<String, Object> searchMap = new HashMap<>();
        if(StringUtils.isEmpty(shopId)){
           return HttpResult.createFAIL("请选择商店！！");
        }
        if(!StringUtils.isEmpty(carState)){
            searchMap = getSearchConditionByCarState(carState,searchMap);
        }
        searchMap.put("EQ_shop.id", shopId);
        Page<DeviceStatus> deviceStatusPage = deviceStatusService.findAll(searchMap,page,rows);
        return HttpResult.createSuccess("查询成功！",deviceStatusPage);
    }
    @RequestMapping(value = "/normal", method = RequestMethod.GET)
    public HttpResult normal(Integer shopId,String devId, @RequestParam Integer page, @RequestParam(defaultValue = "300") Integer rows) {
        Map<String, Object> searchMap = new HashMap<>();
        searchMap.put("NE_status", EStatus.ABANDON);
        searchMap.put("GT_modifiedTime", new Date(System.currentTimeMillis() - ABNORMAL_TIME_INTERVAL));
        searchMap.put("EQ_shop.id", shopId);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(devId)){
            searchMap.put("LIKE_devId",devId);
        }
        Page<DeviceStatus> ret = deviceStatusService.findAll(searchMap, page, rows);
        return HttpResult.createSuccess("success", ret);
    }

    @RequestMapping(value = "/abnormal", method = RequestMethod.GET)
    public HttpResult abnormal(Integer shopId,String devId, @RequestParam Integer page, @RequestParam(defaultValue = "300") Integer rows) {
        Map<String, Object> searchMap = new HashMap<>();
        searchMap.put("NE_status", EStatus.ABANDON);
        searchMap.put("LT_modifiedTime", new Date(System.currentTimeMillis() - ABNORMAL_TIME_INTERVAL));
        searchMap.put("EQ_shop.id", shopId);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(devId)){
            searchMap.put("LIKE_devId",devId);
        }
        Page<DeviceStatus> ret = deviceStatusService.findAll(searchMap, page, rows);
        return HttpResult.createSuccess("success", ret);
    }

    @RequestMapping(value = "/abandon", method = RequestMethod.GET)
    public HttpResult abandon(Integer shopId,String devId, @RequestParam Integer page, @RequestParam(defaultValue = "300") Integer rows) {
        Map<String, Object> searchMap = new HashMap<>();
        searchMap.put("EQ_status", EStatus.ABANDON);
        searchMap.put("EQ_shop.id", shopId);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(devId)){
            searchMap.put("LIKE_devId",devId);
        }
        Page<DeviceStatus> ret = deviceStatusService.findAll(searchMap, page, rows);
        return HttpResult.createSuccess("success", ret);
    }

    @RequestMapping(value = "/handle", method = RequestMethod.GET)
    public HttpResult handle(@RequestParam Integer id) {
        DeviceStatus entity = deviceStatusService.findOne(id);
        entity.setAlarmCount(0);
        entity.setStatus(EStatus.NORMAL);
        deviceStatusService.save(entity);
        return HttpResult.createSuccess("success");
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    public HttpResult reset(@RequestParam Integer id) {
        DeviceStatus entity = deviceStatusService.findOne(id);
        entity.setStatus(EStatus.ABANDON);
        deviceStatusService.save(entity);
        return HttpResult.createSuccess("success");
    }

    @RequestMapping(value = "/recover", method = RequestMethod.GET)
    public HttpResult recover(@RequestParam Integer id) {
        DeviceStatus entity = deviceStatusService.findOne(id);
        entity.setStatus(EStatus.NORMAL);
        deviceStatusService.save(entity);
        return HttpResult.createSuccess("success");
    }
    private Map<String,Object> getSearchConditionByCarState(String carState,Map<String,Object> searchMap){
        switch (carState){
            case "normal":
                searchMap.put("EQ_status", EStatus.NORMAL);
                searchMap.put("GT_modifiedTime", new Date(System.currentTimeMillis() - ABNORMAL_TIME_INTERVAL));
                return searchMap;
            case "abnormal":
                searchMap.put("EQ_status", EStatus.NORMAL);
                searchMap.put("LT_modifiedTime", new Date(System.currentTimeMillis() - ABNORMAL_TIME_INTERVAL));
                return searchMap;
            case "abandon":
                searchMap.put("EQ_status", EStatus.ABANDON);
                return searchMap;
            default:
                return null;
        }
    }
}
