package cn.huace.controller.stat;

import cn.huace.common.utils.DateUtils;
import cn.huace.controller.base.BaseFrontController;
import cn.huace.shop.shop.entity.Shop;
import cn.huace.shop.shop.service.ShopService;
import io.swagger.annotations.Api;
import org.apache.solr.common.util.DateUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import cn.huace.shop.stat.entity.LowBattery;
import cn.huace.shop.stat.service.LowBatteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import cn.huace.common.bean.HttpResult;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xiaoling on 2017/5/27.
 */

@Slf4j
@RestController
@Api(value = "/stat", description = "低电告警")
@RequestMapping("/stat")
public class LowBatteryController extends BaseFrontController {

    @Autowired
    private LowBatteryService lowBatteryService;

    @Autowired
    private ShopService shopService;

    @RequestMapping(value = "/lowbattery", method = {RequestMethod.POST})

    public HttpResult saveLowBattery(HttpServletRequest request, String ledId, String location,
                                     @RequestParam(required = false,defaultValue = "0") Float x, @RequestParam(required = false,defaultValue = "0") Float y) {

        Integer count = 1;

        Integer shopId = findShopId(request);
        if (shopId == null) {
            return HttpResult.createFAIL("shopId can't be null");
        }

        String devId = findDevId(request);
        if (devId == null) {
            return HttpResult.createFAIL("devId can't be null");
        }

        LowBattery entity = lowBatteryService.findOneRecordNoProcess(shopId, devId);
        //都满足条件就有，否则插入新的
        if (entity == null) {
            entity = new LowBattery();
            Shop shop = shopService.findOne(shopId);
            if (shop == null) {
                return HttpResult.createFAIL("shop不存在");
            }
            log.info("saveLowBattery(),new entity");
            entity.setShop(shop);
            entity.setDevId(devId);
        } else {
            if (DateUtils.getFormatTime(entity.getModifiedTime()).equals(DateUtils.getFormatTime(new Date()))) {
                count = entity.getCount() + 1;
            } else {
                entity = new LowBattery();
                Shop shop = shopService.findOne(shopId);
                if (shop == null) {
                    return HttpResult.createFAIL("shop不存在");
                }
                entity.setShop(shop);
                entity.setDevId(devId);
            }
        }
        entity.setLedId(ledId);
        entity.setLoc(location);
        entity.setIsProcess(false);
        entity.setCount(count);
        entity.setX(x);
        entity.setY(y);
        lowBatteryService.save(entity);

        return HttpResult.createSuccess("保存成功");
    }
}
