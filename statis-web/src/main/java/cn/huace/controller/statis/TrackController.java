package cn.huace.controller.statis;

import cn.huace.common.bean.HttpResult;
import cn.huace.common.utils.DateUtils;
import cn.huace.common.utils.redis.RedisService;
import cn.huace.controller.base.AdminBasicController;
import cn.huace.shop.shop.entity.Shop;
import cn.huace.shop.shop.service.ShopService;
import cn.huace.statis.track.entity.StatisTrack;
import cn.huace.statis.track.service.StatisTrackService;
import cn.huace.sys.bean.ShiroUser;
import cn.huace.sys.entity.SystemUser;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author zhouyanbin
 * @version 1.0
 * @date 2017年5月3日 下午12:23:56
 */

@Slf4j
@RestController
@Api(value = "/admin/track", description = "购物车轨迹")
@RequestMapping(value = "/admin/track")
public class TrackController extends AdminBasicController {
    @Autowired
    private StatisTrackService statisTrackService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private RedisService redisService;

    @RequestMapping(method = RequestMethod.GET)
    public HttpResult list(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer rows,
                           Integer shopId, String devId, Date start, Date end) {
        Map<String, Object> searchMap = new HashMap<String, Object>();
        long deviceCount = 0;
        if (StringUtils.isEmpty(shopId)) {
            ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
            SystemUser systemUser = (SystemUser) redisService.getObjectCacheValue("cn.huace.sys.systemUser:" + user.getAccount(), 3);
            if (systemUser.getType() != 0) {
                String shopIds = systemUser.getShopIds();
                if (shopIds.split(",").length == 1) {
                    searchMap.put("EQ_shopId", Integer.parseInt(shopIds.trim()));
                } else {
                    searchMap.put("EQ_shopId", Integer.parseInt(shopIds.split(",")[0]));
                }
                deviceCount = statisTrackService.countDeviceNum(Integer.parseInt(shopIds.split(",")[0]), devId, start, end);
            }
        } else {
            searchMap.put("EQ_shopId", shopId);
            deviceCount = statisTrackService.countDeviceNum(shopId, devId, start, end);
        }
        if (!org.apache.commons.lang.StringUtils.isEmpty(devId)) {
            searchMap.put("LIKE_devId", "%" + devId + "%");
        }
        if (start != null) {
            searchMap.put("GTE_atTime", DateUtils.getStartTime(start));
        }
        if (end != null) {
            searchMap.put("LTE_atTime", DateUtils.getEndTime(end));
        }
        Page<StatisTrack> pageResult = statisTrackService.listTrack(searchMap, page, rows);
        for (StatisTrack statisTrack : pageResult) {
            if (statisTrack.getShopId() != null) {
                Shop shop = shopService.findOne(statisTrack.getShopId());
                statisTrack.setShop(shop);
            }
        }
//        long deviceCount = statisTrackService.countDeviceNum(shopId, devId, start, end);
        return HttpResult.createSuccess(pageResult, deviceCount);
    }
}
