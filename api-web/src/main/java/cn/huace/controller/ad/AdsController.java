package cn.huace.controller.ad;

import cn.huace.ads.constant.AdsConstant;
import cn.huace.ads.entity.Ads;
import cn.huace.ads.entity.AdsOnline;
import cn.huace.ads.service.AdsOnlineService;
import cn.huace.ads.service.AdsService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * V3版本广告
 * Date:2018/6/6
 */
@Slf4j
@RestController
@Api(value = "/v3/ad",description = "V3版本广告")
@RequestMapping(value = "/v3/ad")
public class AdsController extends BaseFrontController {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private AdsOnlineService adsOnlineService;
    @Autowired
    private AdsService adsService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private NaviMapService naviMapService;

    @ApiOperation(value = "获取所有广告信息")
    @RequestMapping(value = "/homelist",method = RequestMethod.POST)
    public HttpResult listAds(HttpServletRequest request){
        Integer shopId = findShopId(request);
        String devId = findDevId(request);
        log.info("******* 获取V3广告数据开始,shopId = {},devId = {}",shopId,devId);
        if (shopId == null || devId == null) {
            return HttpResult.createFAIL("参数异常：非法接口访问！");
        }
        Device device = deviceService.findDevice(devId);
        if (device == null) {
            return HttpResult.createFAIL("参数异常：设备不存在！");
        }
        /*广告信息*/
        List<AdV2OV> retList_ = new ArrayList<>();
        /**
         * 获取设备广告：
         * 1、获取开机视频广告
         *     先取指定设备投放，无则取默认投放，无则取默认广告 ，只取一条。
         * 2、首页轮播广告
         *     先取指定设备投放 和 默认投放 ，无则取默认广告
         * 3、特价封面广告
         *     先取指定设备投放，无则取默认投放，无则取默认广告，只取一条。
         * 4、新品轮播广告
         *     先取指定设备投放 和 默认投放 ，无则取默认广告
         * 5、LBS广告
         *     先取指定设备投放 和 默认投放 ，无则取默认广告
         */
        AdsOnline obj_1 = adsOnlineService.findNewByDevId(shopId,AdsConstant.ADS.AD_TYPE.BOOT_VIDEO,devId);
        Ads ads_1;
        if(obj_1 == null){
            List<Ads> adsList = adsService.findByDefalut(shopId,AdsConstant.ADS.AD_TYPE.BOOT_VIDEO);
            ads_1 = adsList != null && adsList.size()>0 ? adsList.get(0):null;
            if(adsList == null || adsList.size()== 0){
                return HttpResult.createFAIL("数据异常：开机视频广告位默认广告不存在！");
            }else {
                retList_.add(AdsToAdV2OV(ads_1));
            }
        }else {
            retList_.add(AdsToAdV2OV(obj_1.getAds()));
        }

        List<AdsOnline> list_2 = adsOnlineService.findAllByDevId(shopId,AdsConstant.ADS.AD_TYPE.HOME_PAGE,devId);
        List<Ads> adsList_2;
        if(list_2 ==null || list_2.size()==0){
            adsList_2 = adsService.findByDefalut(shopId,AdsConstant.ADS.AD_TYPE.HOME_PAGE);
            if(adsList_2 == null || adsList_2.size()== 0){
                return HttpResult.createFAIL("数据异常：首页轮播广告位默认广告不存在！");
            }else {
                retList_.addAll(adsList_2.stream().map(ads -> AdsToAdV2OV(ads)).collect(Collectors.toList()));
            }

        }else {
            retList_.addAll(list_2.stream().map(adsOnline -> AdsToAdV2OV(adsOnline.getAds())).collect(Collectors.toList()));
        }

        AdsOnline obj_3 = adsOnlineService.findNewByDevId(shopId,AdsConstant.ADS.AD_TYPE.SPECIAL_COVER,devId);
        Ads ads_3;
        if(obj_3 == null){
            List<Ads> adsList = adsService.findByDefalut(shopId,AdsConstant.ADS.AD_TYPE.SPECIAL_COVER);
            ads_3 = adsList != null && adsList.size()>0 ? adsList.get(0):null;
            if(adsList == null || adsList.size()== 0){
                return HttpResult.createFAIL("数据异常：特价封面广告位默认广告不存在！");
            }else {
                retList_.add(AdsToAdV2OV(ads_3));
            }
        }else {
            retList_.add(AdsToAdV2OV(obj_3.getAds()));
        }

        List<AdsOnline> list_4 = adsOnlineService.findAllByDevId(shopId,AdsConstant.ADS.AD_TYPE.NEW_PAGE,devId);
        List<Ads> adsList_4;
        if(list_4 ==null || list_4.size()==0){
            adsList_4 = adsService.findByDefalut(shopId,AdsConstant.ADS.AD_TYPE.NEW_PAGE);
            if(adsList_4 == null || adsList_4.size()== 0){
                return HttpResult.createFAIL("数据异常：新品轮播广告位默认广告不存在！");
            }else {
                retList_.addAll(adsList_4.stream().map(ads -> AdsToAdV2OV(ads)).collect(Collectors.toList()));
            }
        }else {
            retList_.addAll(list_4.stream().map(adsOnline -> AdsToAdV2OV(adsOnline.getAds())).collect(Collectors.toList()));
        }

        List<AdsOnline> list_5 = adsOnlineService.findAllByDevId(shopId,AdsConstant.ADS.AD_TYPE.LBS,devId);
        List<Ads> adsList_5;
        if(list_5 ==null || list_5.size()==0){
            adsList_5 = adsService.findByDefalut(shopId,AdsConstant.ADS.AD_TYPE.LBS);
            if(adsList_5 == null || adsList_5.size()== 0){
                return HttpResult.createFAIL("数据异常：LBS广告位默认广告不存在！");
            }else {
                retList_.addAll(adsList_5.stream().map(ads -> AdsToAdV2OV(ads)).collect(Collectors.toList()));
            }
        }else {
            retList_.addAll(list_5.stream().map(adsOnline -> AdsToAdV2OV(adsOnline.getAds())).collect(Collectors.toList()));
        }

        /*地图信息*/
        NaviMap naviMap = naviMapService.findNewestNavimapByShop(shopId);
        /*返回对象*/
        WrapAdV2Item item = new WrapAdV2Item(retList_,naviMap == null ? null:naviMap.toResp());
        return HttpResult.createSuccess("查询成功！",item);
    }

    private AdV2OV AdsToAdV2OV(Ads ads){
        AdV2OV adV2OV = new AdV2OV();
        adV2OV.setId(ads.getId());
        adV2OV.setType(ads.getType());
        adV2OV.setRelationType(ads.getRelationCode());
        adV2OV.setExtra(ads.getRelationExtra());
        adV2OV.setUrl(ads.getUrl());
        adV2OV.setMd5(ads.getMd5());
        if(ads.getRelationCode().equals(AdsConstant.ADS.RELATION_CODE.GOODS_)||ads.getRelationCode().equals(AdsConstant.ADS.RELATION_CODE.LBS_)){
            String a[] = ads.getRelationExtra().split(",");
            Goods goods = goodsService.findOne(Integer.parseInt(a[0]));
            adV2OV.setProductId(goods.getId());//商品ID
            adV2OV.setTitle(goods.getTitle());//商品名称
            adV2OV.setNormalPrice(goods.getNormalPrice());//商品原价
            adV2OV.setPromotionPrice(goods.getPromotionPrice());//商品折扣价
        }
        adV2OV.setRank(ads.getRank());
        return adV2OV;
    }

}
