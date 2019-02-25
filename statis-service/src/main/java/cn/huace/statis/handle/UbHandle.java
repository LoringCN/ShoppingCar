package cn.huace.statis.handle;

import cn.huace.ad.entity.AdV2;
import cn.huace.ad.service.AdV2Service;
import cn.huace.goods.entity.Goods;
import cn.huace.goods.service.GoodsService;
import cn.huace.statis.ub.entity.StatisUb;
import cn.huace.statis.ub.entity.StatisUbNavi;
import cn.huace.statis.ub.service.StatisUbNaviService;
import cn.huace.statis.ub.service.StatisUbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by huangdan on 2017/7/13.
 */
@Component
@Slf4j
public class UbHandle implements BasicHandle {

    @Autowired
    private StatisUbNaviService statisUbNaviService;

    @Autowired
    private StatisUbService statisUbService;

    @Autowired
    private AdV2Service adService;

    @Autowired
    private GoodsService goodsService;

    public synchronized void readLog(Map<String, String> str, String timeStr) {

        String typeStr = str.get("type");
        String key;
        log.info("typeStr:" + typeStr);
        int type = Integer.parseInt(typeStr);
        int shopId = Integer.parseInt(str.get("shopId"));
        switch (type) {
            case 1:
                countUb(shopId, type, Integer.parseInt(str.get("adId")));
                break;
            case 2:
                countUb(shopId, type, Integer.parseInt(str.get("adId")));
                break;
            case 3:
                countUbNavi(shopId, type, str.get("startLocation"), str.get("endLocation"));
                break;
            case 4:
                countUbNavi(shopId, type, null, str.get("endLocation"));
                break;
            case 5:
            case 6:
                countUb(shopId, type, null);
                break;
            case 7:
                countUb(shopId, type, Integer.parseInt(str.get("productId")));
                break;
            case 8:
                countUb(shopId, type, Integer.parseInt(str.get("adId")));
                break;
            case 9:
                countUb(shopId, type, null);
                break;
            case 10:
                countUb(shopId,type,Integer.parseInt(str.get("adId")));
                break;
            case 11:
                countUb(shopId,type,Integer.parseInt(str.get("adId")));
                break;
            case 12:
                countUb(shopId,type,Integer.parseInt(str.get("adId")));
                break;
            case 13:
                countUb(shopId,type,Integer.parseInt(str.get("adId")));
                break;
            case 14:
                countUb(shopId,type,Integer.parseInt(str.get("adId")));
                break;

        }
    }

    private void countUb(int shopId, int type, Integer otherId) {
        String key = shopId + "_" + type + "_" + otherId;
        StatisUb ub = HandleData.statisUbMap.get(key);
        if (ub == null) {
            ub = new StatisUb();
            ub.setType(type);
            ub.setShopId(shopId);
            ub.setOtherId(otherId);
            ub.setTotalCount(1);
            // 将otherId转换为对应广告名或商品名
            convertOtherId2GoodsNameOrAdName(type,otherId,ub);
            HandleData.statisUbMap.put(key, ub);
        } else {
            ub.setTotalCount(ub.getTotalCount() + 1);
        }
    }

    private void convertOtherId2GoodsNameOrAdName(int type,Integer otherId,StatisUb ub){
        if (StringUtils.isEmpty(otherId)) {
            return;
        }
        AdV2 ad = adService.findOne(otherId);
        Goods goods = goodsService.findOne(otherId);
        String adName = ad == null?"":ad.getName();
        switch (type) {
            case 1:
                ub.setOtherName(adName);
                break;
            case 2:
                ub.setOtherName(adName);
                break;
            case 7:
                ub.setOtherName(goods == null?"":goods.getTitle());
                break;
            case 8:
                ub.setOtherName(adName);
                break;
            case 10:
                ub.setOtherName(adName);
                break;
            case 11:
                ub.setOtherName(adName);
                break;
            default:
                break;
        }
    }
    private void countUbNavi(int shopId, int type, String startLocation, String endLocation) {
        String key = shopId + "_" + type + "_" + startLocation + "_" + endLocation;
        StatisUbNavi ub = HandleData.statisUbNaviMap.get(key);
        if (ub == null) {
            ub = new StatisUbNavi();
            ub.setType(type);
            ub.setShopId(shopId);
            ub.setStartLocation(startLocation);
            ub.setEndLocation(endLocation);
            ub.setTotalCount(1);
            HandleData.statisUbNaviMap.put(key, ub);
        } else {
            ub.setTotalCount(ub.getTotalCount() + 1);
        }
    }

    public void handleData(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(dateStr);
            Map<String, StatisUb> statisUbMap = HandleData.statisUbMap;
            Set<String> keySet = statisUbMap.keySet();
            for (String key : keySet) {
                StatisUb statisUb = statisUbMap.get(key);
                if (statisUb != null) {
                    statisUb.setStatisDate(date);
                    statisUbService.save(statisUb);
                }
            }
            Map<String, StatisUbNavi> statisUbNaviMap = HandleData.statisUbNaviMap;
            keySet = statisUbNaviMap.keySet();
            for (String key : keySet) {
                StatisUbNavi statisUbNavi = statisUbNaviMap.get(key);
                if (statisUbNavi != null) {
                    statisUbNavi.setStatisDate(date);
                    statisUbNaviService.save(statisUbNavi);
                }
            }


        } catch (Exception e) {

        }


//        Map<Integer, LiveChannel> channelKeyMap=HandleData.channelKeyMap;
//        Set<Integer> channelIdSet=channelKeyMap.keySet();
//        StatLiveChannelIfc ejb=(StatLiveChannelIfc)Utils.lookupEJB(StatLiveChannelIfc.jndi);
//        for(Integer channelId:channelIdSet){
//            LiveChannel channel= channelKeyMap.get(channelId);
//            if(channel!=null){
//                ejb.saveLiveChannelFromLog(channel);
//            }
//        }
    }
}
