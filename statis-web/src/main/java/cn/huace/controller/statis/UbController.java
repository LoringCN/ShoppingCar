package cn.huace.controller.statis;

import cn.huace.common.bean.HttpResult;
import cn.huace.common.utils.DateUtils;
import cn.huace.common.utils.redis.RedisService;
import cn.huace.controller.base.AdminBasicController;
import cn.huace.controller.bean.UbType;
import cn.huace.shop.shop.entity.Shop;
import cn.huace.shop.shop.service.ShopService;
import cn.huace.statis.ub.entity.StatisUb;
import cn.huace.statis.ub.entity.StatisUbNavi;
import cn.huace.statis.ub.service.StatisUbNaviService;
import cn.huace.statis.ub.service.StatisUbService;
import cn.huace.statis.utils.StatContants;
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
@Api(value = "/admin/ub", description = "")
@RequestMapping(value = "/admin/ub")
public class UbController extends AdminBasicController {

    @Autowired
    private StatisUbService statisUbService;

    @Autowired
    private StatisUbNaviService statisUbNaviService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private RedisService redisService;

    @RequestMapping(method = RequestMethod.GET)
    public HttpResult list(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer rows,
                           Integer shopId, Integer type, Date start, Date end) {
        Map<String, Object> searchMap = new HashMap<String, Object>();
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
            }
        } else {
            searchMap.put("EQ_shopId", shopId);
        }
        if (type != null) {
            searchMap.put("EQ_type", type);
        }
        if (start != null) {
            searchMap.put("GTE_statisDate", DateUtils.getStartTime(start));
        }
        if (end != null) {
            searchMap.put("LTE_statisDate", DateUtils.getEndTime(end));
        }

        Page<StatisUb> pageResult = statisUbService.listUb(searchMap, page, rows);
        for (StatisUb statisUb : pageResult) {
            if (statisUb.getShopId() != null) {
                Shop shop = shopService.findOne(statisUb.getShopId());
                statisUb.setShop(shop);
            }
        }
        return HttpResult.createSuccess(pageResult);
    }

    @RequestMapping(value = "/statisUbNavi", method = RequestMethod.GET)
    public HttpResult statisUbNavi(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer rows,
                                   Integer shopId, Integer type, Date start, Date end) {

        Map<String, Object> searchMap = new HashMap<String, Object>();
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
            }
        } else {
            searchMap.put("EQ_shopId", shopId);
        }
        if (type != null) {
            searchMap.put("EQ_type", type);
        }
        if (start != null) {
            searchMap.put("GTE_statisDate", DateUtils.getStartTime(start));
        }
        if (end != null) {
            searchMap.put("LTE_statisDate", DateUtils.getEndTime(end));
        }
        Page<StatisUbNavi> pageResult = statisUbNaviService.listUbNavi(searchMap, page, rows);
        for (StatisUbNavi statisUb : pageResult) {
            if (statisUb.getShopId() != null) {
                Shop shop = shopService.findOne(statisUb.getShopId());
                statisUb.setShop(shop);
            }
        }
        return HttpResult.createSuccess(pageResult);
    }

    @RequestMapping(value = "/listUbType", method = RequestMethod.GET)
    public HttpResult listUbType() {
        List<UbType> list = new ArrayList<UbType>();
        int size = StatContants.UB_TYPE_ST.UB_TYPE_MAP.size();
        for (int i = 1; i <= size; i++) {
            if (excludeUbType(i)) {
                list.add(new UbType(i, StatContants.UB_TYPE_ST.UB_TYPE_MAP.get(i)));
            }
        }
        return HttpResult.createSuccess(list);
    }

    @RequestMapping(value = "/listUbNaviType", method = RequestMethod.GET)
    public HttpResult listUbNaviType() {
        List<UbType> list = new ArrayList<UbType>();
        list.add(new UbType(StatContants.UB_TYPE_ST.UB_NAVI, StatContants.UB_TYPE_ST.UB_TYPE_MAP.get(StatContants.UB_TYPE_ST.UB_NAVI)));
        list.add(new UbType(StatContants.UB_TYPE_ST.UB_NAVI_ARRIVED, StatContants.UB_TYPE_ST.UB_TYPE_MAP.get(StatContants.UB_TYPE_ST.UB_NAVI_ARRIVED)));
        return HttpResult.createSuccess(list);
    }

    private boolean excludeUbType(int type) {
        switch (type) {
            case StatContants.UB_TYPE_ST.UB_NAVI:
                return false;
            case StatContants.UB_TYPE_ST.UB_NAVI_ARRIVED:
                return false;
            case StatContants.UB_TYPE_ST.UB_CLICK_APP_CHECK:
                return false;
            default:
                return true;
        }
    }
}
