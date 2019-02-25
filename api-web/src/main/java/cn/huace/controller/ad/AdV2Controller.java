package cn.huace.controller.ad;

import cn.huace.ad.entity.AdOnlineV2;
import cn.huace.ad.entity.AdRelationV2;
import cn.huace.ad.entity.AdTypeV2;
import cn.huace.ad.entity.AdV2;
import cn.huace.ad.service.AdOnlineV2Service;
import cn.huace.ad.service.AdTypeV2Service;
import cn.huace.ad.service.AdV2Service;
import cn.huace.ad.util.AdCodeConstants;
import cn.huace.common.bean.HttpResult;
import cn.huace.controller.base.BaseFrontController;
import cn.huace.entity.AdV2OV;
import cn.huace.entity.WrapAdV2Item;
import cn.huace.goods.entity.Goods;
import cn.huace.goods.service.GoodsService;
import cn.huace.shop.device.entity.Device;
import cn.huace.shop.device.service.DeviceService;
import cn.huace.shop.map.entity.NaviMap;
import cn.huace.shop.map.service.NaviMapService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * V2版本广告
 * Date:2018/3/5
 */
@Slf4j
@RestController
@Api(value = "/v2/ad",description = "V2版本广告")
@RequestMapping(value = "/v2/ad")
public class AdV2Controller extends BaseFrontController{

    @Autowired
    private DeviceService deviceService;
    @Autowired
    private AdOnlineV2Service adOnlineV2Service;
    @Autowired
    private AdV2Service adV2Service;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private NaviMapService naviMapService;
    @Autowired
    private AdTypeV2Service adTypeV2Service;

    @ApiOperation(value = "获取所有广告信息")
    @RequestMapping(value = "/homelist",method = RequestMethod.POST)
    public HttpResult listAds(HttpServletRequest request){
        Integer shopId = findShopId(request);
        String devId = findDevId(request);
        log.info("******* 获取新版广告数据,shopId = {},devId = {}",shopId,devId);
        if (shopId == null || devId == null) {
            return HttpResult.createFAIL("非法接口访问！");
        }
        Device device = deviceService.findDevice(devId);
        if (device == null) {
            return HttpResult.createFAIL("设备不存在！");
        }
        // 所有广告
        List<AdV2> allAdV2s = new ArrayList<>();
        // 所有上线广告类型ID
        Set<Integer> adTypeIdSet = new HashSet<>();
        // 线上广告Id
        List<Integer> adIds = new ArrayList<>();

        // 获取该设备指定投放广告
        List<AdOnlineV2> adOnlineV2List = adOnlineV2Service.findAdOnlineV2ByDevId(device.getId(),shopId);
        // 获取默认投放广告
        int defaultDevId = -1;
        List<AdOnlineV2> defaultAdOnlineV2List = adOnlineV2Service.findAdOnlineV2ByDevId(defaultDevId,shopId);

        // 排除已有指定广告的广告位上的默认广告
        List<AdOnlineV2> allAdOnlineV2List = excludeDefaultAdOnlineV2(adOnlineV2List,defaultAdOnlineV2List);
        if (allAdOnlineV2List != null && allAdOnlineV2List.size() != 0) {
            getAdIdsAndAdTypeIds(allAdOnlineV2List,adIds,adTypeIdSet);
            log.info("******* 已上线广告Ids = {}",adIds);
            // 根据广告ID集合查询广告
            List<AdV2> adV2List = adV2Service.findAll(adIds);
            setRankAndOnlineTime(adV2List,allAdOnlineV2List);
            allAdV2s.addAll(adV2List);
        }
        // 查询所有广告类型
        List<Integer> allAdTypeIdList = adTypeV2Service.findAllAdTypeV2Ids();
        // 找出没有投放广告的广告位
        allAdTypeIdList.removeAll(adTypeIdSet);

        log.info("******** 没有投放的广告类型：{}",allAdTypeIdList);

        // 如果所有广告位都有广告投放，那么不需要再查询默认广告
        if (!CollectionUtils.isEmpty(allAdTypeIdList)) {
            // 查询对应广告位内部默认广告
            List<AdV2> defaultInternalAdV2s = adV2Service.findInternalAdV2ByAdTypeId(allAdTypeIdList,shopId);
            if (defaultInternalAdV2s != null && defaultInternalAdV2s.size() != 0) {
                for (AdV2 adV2 : defaultInternalAdV2s) {
                    adV2.setRank(Integer.MAX_VALUE);
                }
                allAdV2s.addAll(defaultInternalAdV2s);
            }
        }

        // 封装返回数据
        List<AdV2OV> adV2OVList = new ArrayList<>();

        NaviMap naviMap = naviMapService.findNewestNavimapByShop(shopId);

        if (allAdV2s.size() == 0) {
            log.info("******* 没有广告数据~~~~,shopId = {},devId = {}",shopId,devId);
            return HttpResult.createSuccess("查询成功！",new WrapAdV2Item(adV2OVList,naviMap == null ? null:naviMap.toResp()));
        }
        // 广告排序
        List<AdV2> sortAdV2s = sortAdV2s(allAdV2s);
        convertAdV2ToAdV2OV(sortAdV2s,adV2OVList);

        WrapAdV2Item item = new WrapAdV2Item(adV2OVList,naviMap == null ? null:naviMap.toResp());
        return HttpResult.createSuccess("查询成功！",item);
    }

    /**
     * 移除不需要默认广告
     * @param adOnlineV2List
     * @param defaultAdOnlineV2List
     * @return 返回合并后广告
     */
    private List<AdOnlineV2> excludeDefaultAdOnlineV2(List<AdOnlineV2> adOnlineV2List, List<AdOnlineV2> defaultAdOnlineV2List) {
        List<AdOnlineV2> allAdOnlineV2s = new ArrayList<>();
        List<AdOnlineV2> excludeDefaultAdOnlineV2s = new ArrayList<>();
        if (CollectionUtils.isEmpty(adOnlineV2List) && CollectionUtils.isEmpty(defaultAdOnlineV2List)) {
            return null;
        }
        if (CollectionUtils.isEmpty(adOnlineV2List)) {
            return defaultAdOnlineV2List;
        }
        if (CollectionUtils.isEmpty(defaultAdOnlineV2List)) {
            return adOnlineV2List;
        }
        for (AdOnlineV2 deviceAd : adOnlineV2List) {
            for (AdOnlineV2 defaultAd : defaultAdOnlineV2List) {
                if (deviceAd.getTypeId().intValue() == defaultAd.getTypeId()) {
                    // 轮播广告位可以有多个默认投放方式广告
                    AdTypeV2 adTypeV2 = adTypeV2Service.findOne(deviceAd.getTypeId());
                    int typeCode = adTypeV2.getCode();
                    if (AdCodeConstants.AdV2Type.TYPE_CODE_INDEX_CAROUSEL != typeCode
                        && AdCodeConstants.AdV2Type.TYPE_CODE_NEW_RECOMMEND_CAROUSEL != typeCode
                        && AdCodeConstants.AdV2Type.TYPE_CODE_LBS != typeCode) {
                        excludeDefaultAdOnlineV2s.add(defaultAd);
                    }
                }
            }
        }
        // 移除不需要默认广告
        defaultAdOnlineV2List.removeAll(excludeDefaultAdOnlineV2s);
        // 合并所有广告
        allAdOnlineV2s.addAll(adOnlineV2List);
        allAdOnlineV2s.addAll(defaultAdOnlineV2List);

        return allAdOnlineV2s;
    }

    private void convertAdV2ToAdV2OV(List<AdV2> sortAdV2s, List<AdV2OV> adV2OVList) {
        for (AdV2 adV2 : sortAdV2s) {
            AdTypeV2 adType = adV2.getType();
            AdRelationV2 adRelation = adV2.getRelation();

            AdV2OV adOV = new AdV2OV();
            adOV.setId(adV2.getId());
            adOV.setType(adType.getCode());
            adOV.setUrl(adV2.getUrl());
            adOV.setMd5(adV2.getMd5());
            adOV.setRelationType(adRelation.getCode());
//            adOV.setExtra(adV2.getExtra());
            adOV.setRank(adV2.getRank()==Integer.MAX_VALUE ?0:adV2.getRank());
            // 获取关联商品信息
            if (adRelation.getCode() == AdCodeConstants.AdV2Relation.GOODS_RELATION) {
                // 非定位广告且关联关系为商品
                int goodsId = Integer.parseInt(adV2.getExtra());
                Goods goods = goodsService.findOne(goodsId);
                // 设置关联商品信息
                adOV = setGoodsInfo(goods,adOV);
                adOV.setExtra(adV2.getExtra());
            }else if (adRelation.getCode() == AdCodeConstants.AdV2Relation.GOODS_AND_SHELF) {
                String extra = adV2.getExtra();
                if (StringUtils.isNotBlank(extra)) {
                    String[] arr = StringUtils.split(extra,",");
                    Goods goods = goodsService.findOne(Integer.parseInt(arr[0]));
                    // 设置关联商品信息
                    adOV = setGoodsInfo(goods,adOV);
                    // 移除商品id,只保留货架id
                    adOV.setExtra(extra.substring(extra.indexOf(",")+1));
                }
            } else {
                adOV.setExtra(adV2.getExtra());
            }
            adV2OVList.add(adOV);
        }
    }
    private AdV2OV setGoodsInfo(Goods goods,AdV2OV adOV){
        if (goods != null) {
            adOV.setProductId(goods.getId());
            adOV.setTitle(goods.getTitle());
            adOV.setNormalPrice(goods.getNormalPrice());
            adOV.setPromotionPrice(goods.getPromotionPrice());
        }
        return adOV;
    }
    /**
     * 设置广告位和广告上线时间
     */
    private void setRankAndOnlineTime(List<AdV2> adV2List, List<AdOnlineV2> adOnlineV2List) {
        for (AdV2 adV2 : adV2List) {
            for (AdOnlineV2 adOnlineV2 : adOnlineV2List) {
                if (adV2.getId().intValue() == adOnlineV2.getAdId().intValue()) {
                    adV2.setRank(adOnlineV2.getRank());
                    adV2.setOnlineTime(adOnlineV2.getModifiedTime());
                }
            }
        }
    }

    /**
     * 获取已投放线上广告Id和广告类型Id
     */
    private void getAdIdsAndAdTypeIds(List<AdOnlineV2> adOnlineV2List, List<Integer> adIds, Set<Integer> adTypeIdSet) {
        for (AdOnlineV2 adOnlineV2:adOnlineV2List) {
            adIds.add(adOnlineV2.getAdId());
            adTypeIdSet.add(adOnlineV2.getTypeId());
        }
    }

    /**
     * 返回结果排序
     * @param allAdV2s
     * @return
     */
    private List<AdV2> sortAdV2s(List<AdV2> allAdV2s) {
        allAdV2s.sort((AdV2 o1,AdV2 o2) -> {
            // typeCode升序
            int o1TypeCode = o1.getType().getCode();
            int o2TypeCode = o2.getType().getCode();
            if (o1TypeCode > o2TypeCode) {
                return 1;
            }
            if (o1TypeCode < o2TypeCode){
                return -1;
            }
            // 广告位rank升序
            int o1Rank = o1.getRank();
            int o2Rank = o2.getRank();
            if (o1Rank > o2Rank) {
                return 1;
            }
            if (o1Rank < o2Rank) {
                return -1;
            }
            // 外部广告在前，内部广告在后
            boolean o1Flag = o1.getFlag();
            boolean o2Flag = o2.getFlag();
            if (o1Flag && !o2Flag) {
                return 1;
            }
            if (!o1Flag && o2Flag) {
                return -1;
            }
            // 指定投放在前，默认投放在后
            Byte o1DeliverMethod = o1.getDeliverMethod();
            Byte o2DeliverMethod = o2.getDeliverMethod();
            if (o1DeliverMethod == null && o2DeliverMethod != null) {
                return 1;
            }
            if (o1DeliverMethod != null && o2DeliverMethod == null) {
                return -1;
            }
            if (o1DeliverMethod != null) {
                if (o1DeliverMethod.intValue() == 1 && o2DeliverMethod.intValue() == -1) {
                    return -1;
                }
                if (o1DeliverMethod.intValue() == -1 && o2DeliverMethod.intValue() == 1) {
                    return 1;
                }
            }
            if (o2DeliverMethod != null) {
                if (o1DeliverMethod.intValue() == 1 && o2DeliverMethod.intValue() == -1) {
                    return -1;
                }
                if (o1DeliverMethod.intValue() == -1 && o2DeliverMethod.intValue() == 1) {
                    return 1;
                }
            }
            // 最新上线在前
            Date o1OlineTime = o1.getOnlineTime();
            Date o2OnlineTime = o2.getOnlineTime();

            if (o1OlineTime != null && o2OnlineTime == null) {
                return -1;
            }
            if (o1OlineTime == null && o2OnlineTime != null) {
                return 1;
            }
            if (o1OlineTime != null) {
                if (o1OlineTime.getTime() > o2OnlineTime.getTime()) {
                    return -1;
                }
                if (o1OlineTime.getTime() < o2OnlineTime.getTime()) {
                    return 1;
                }
            }
            if (o2OnlineTime != null) {
                if (o1OlineTime.getTime() > o2OnlineTime.getTime()) {
                    return -1;
                }
                if (o1OlineTime.getTime() < o2OnlineTime.getTime()) {
                    return 1;
                }
            }
            return 0;

        });
        return allAdV2s;
    }
}
