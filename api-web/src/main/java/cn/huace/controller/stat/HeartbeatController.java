package cn.huace.controller.stat;

import cn.huace.common.bean.HttpResult;
import cn.huace.common.utils.EStatus;
import cn.huace.controller.base.BaseFrontController;
import cn.huace.shop.device.service.DeviceService;
import cn.huace.shop.shop.service.ShopService;
import cn.huace.shop.stat.entity.DeviceStatus;
import cn.huace.shop.stat.service.DeviceStatusService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;


@Slf4j
@RestController
@Api(value = "/stat/heartbeat", description = "平板心跳")
@RequestMapping("/stat/heartbeat")
public class HeartbeatController extends BaseFrontController {
    @Autowired
    DeviceStatusService deviceStatusService;

    @Autowired
    ShopService shopService;

    @Autowired
    DeviceService deviceService;

    @RequestMapping
    public HttpResult report(HttpServletRequest request, Float x, Float y,
                             String location, Integer battery, @RequestParam(required = false) String compass) {
        Integer shopId = findShopId(request);
        if (shopId == null) {
            return HttpResult.createFAIL("不存在的用户!");
        }
        String devId = findDevId(request);
        if (devId == null) {
            return HttpResult.createFAIL("不存在的设备！");
        }
        log.info("devId : " + devId + ",shopId : " + shopId + ",x : " + x + ",y : " + y
                + ",location : " + location + ",battery : " + battery + ",compass : " + compass);
        DeviceStatus entity = deviceStatusService.findOne(shopId, devId);
        if (entity == null) {
            entity = new DeviceStatus();
            entity.setShop(shopService.findOne(shopId));
            entity.setStatus(EStatus.NORMAL);
        }
        entity.setDevId(devId);
        entity.setLocation(location);
        entity.setBattery(battery);
        entity.setX(new BigDecimal(x));
        entity.setY(new BigDecimal(y));
        entity.setModifiedTime(new Date());
        deviceStatusService.save(entity);
        deviceService.updateCompass(devId, compass);
        return HttpResult.createSuccess("success");
    }
}
