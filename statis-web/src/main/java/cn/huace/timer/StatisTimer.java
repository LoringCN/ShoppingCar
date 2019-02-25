package cn.huace.timer;

import cn.huace.common.utils.DateUtils;
import cn.huace.config.Config;
import cn.huace.goods.entity.Goods;
import cn.huace.goods.service.GoodsService;
import cn.huace.shop.shop.entity.Shop;
import cn.huace.shop.shop.service.ShopService;
import cn.huace.statis.core.StatisEntrance;
import cn.huace.statis.handle.HandleData;
import cn.huace.statis.mongo.entity.HeatMap;
import cn.huace.statis.mongo.entity.StatisticMongoCategory;
import cn.huace.statis.mongo.entity.StatisticMongoEntity;
import cn.huace.statis.mongo.service.StatisticMongoService;
import cn.huace.statis.search.entity.StatisSearch;
import cn.huace.statis.search.service.StatisSearchService;
import cn.huace.statis.ub.entity.StatisUb;
import cn.huace.statis.ub.service.StatisUbService;
import cn.huace.statis.utils.StatContants;
import cn.huace.statis.utils.StatisticEnum;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by huangdan on 2017/1/18.
 */
@Component
@EnableScheduling
@Slf4j
public class StatisTimer {

    @Autowired
    private StatisticMongoService statisticMongoService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private StatisUbService statisUbService;

    @Autowired
    private StatisSearchService statisSearchService;

    @Autowired
    StatisEntrance statisEntrance;

    @Autowired
    Config config;

    @Autowired
    ShopService shopService;

    //定时刷新叫号&取餐惊喜号
    @Scheduled(cron = "0 10 1 ? * *")
    public void statis() throws Exception {
        HandleData.freshXAndYMap();
        log.info("定时任务开始！！！");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String yesterday = sdf.format(DateUtils.getNextDayTime(-1));
//                yesterday="2017-12-21";
        String path = config.getStatFilePath();
        statisEntrance.doStatis(yesterday, path);

//        首页轮播
        getPHShow();
        log.info("首页轮播完成！！！");

//        首页点击
        getPHClick();
        log.info("首页点击完成！！！");

//        定位广告点击
        getLocClick();
        log.info("定位点击完成！！！");

//        定位广告展示
        getLocShow();
        log.info("点位展示完成！！！");

        getYYADShow();
        log.info("运营广告展示完成！！！");

//        写入搜索
        setSearchCategory();
        log.info("搜索统计完成！！！");

//        把track信息存在mongodb
        setTrack();
        log.info("定时任务结束！！！");

    }

    public void getPHShow() {
        getAdMap(StatContants.UB_TYPE_ST.UB_AD_PIC_SHOW, StatisticEnum.HPADSHOW.getType());
    }

    public void getPHClick() {
        getAdMap(StatContants.UB_TYPE_ST.UB_CLICK_AD, StatisticEnum.HPADCLICK.getType());
    }

    public void getLocClick() {
        getAdMap(StatContants.UB_TYPE_ST.UB_CLICK_LBS_AD, StatisticEnum.LOCADCLICK.getType());
    }

    public void getLocShow() {
        getAdMap(StatContants.UB_TYPE_ST.UB_SHOW_LBS_AD, StatisticEnum.LOCADSHOW.getType());
    }

    public void getYYADShow() {
        getAdMap(StatContants.UB_TYPE_ST.UB_SHOW_YY_AD, StatisticEnum.YYADSHOW.getType());
    }


    //    第一个参数是广告的type，第二个是在mongodb中的type
    public void getAdMap(int type, String mdType) {
        Date startDate = DateUtils.getNextStartTime(new Date(), -1);
        Date endDate = DateUtils.getEndTime(startDate);
        List<Integer> shopIds = statisUbService.findShopIdByTimeAndType(startDate, endDate, type);
        if (shopIds.size() != 0) {
            for (Integer shopId : shopIds) {
                Long sum = 0L;
                List<StatisticMongoCategory> list = new ArrayList<>();
                JSONObject jsonObject = new JSONObject();
                List<StatisUb> statisUbs = statisUbService.findByTimeAndTypeAndShopId(startDate, endDate, type, shopId);
                if (statisUbs.size() != 0) {
                    for (StatisUb statisUb : statisUbs) {
//                        在这不存在重名的情况，所以不再判断
                        jsonObject.put(statisUb.getOtherName(), new JSONObject());
                        jsonObject.getJSONObject(statisUb.getOtherName()).put("count", statisUb.getTotalCount());
                        sum += statisUb.getTotalCount();
                    }
                    for (StatisUb statisUb : statisUbs) {
                        long i = statisUb.getTotalCount();
                        String s = Math.round(((i * 1.0 / sum)) * 1000) / 10.0 + "%";
                        StatisticMongoCategory statisticMongoCategory = new StatisticMongoCategory();
                        statisticMongoCategory.setName(statisUb.getOtherName());
                        statisticMongoCategory.setCount(i);
                        statisticMongoCategory.setRatio(s);
                        list.add(statisticMongoCategory);
                    }
                }
                StatisticMongoEntity statisticMongoEntity = new StatisticMongoEntity();
                statisticMongoEntity.setShopId(shopId);
                statisticMongoEntity.setData(list);
                statisticMongoEntity.setDate(DateUtils.getYesterday(DateUtils.DATE_STRING_FORMAT));
                statisticMongoEntity.setType(mdType);
                statisticMongoService.save(statisticMongoEntity);
            }
        }
    }

    public void setSearchCategory() {
        Date startDate = DateUtils.getNextStartTime(new Date(), -1);
        Date endDate = DateUtils.getEndTime(startDate);
        List<Integer> shopIds = statisSearchService.findShopIdByTime(startDate, endDate);
        if (shopIds.size() != 0) {
            for (Integer shopId : shopIds) {
                Long count;
                String saveCategory;
                Long countOfSearch = 0L;
                List<StatisticMongoCategory> list = new ArrayList<>();
                JSONObject jsonObject = new JSONObject();
                List<StatisSearch> statisSearchList = statisSearchService.findByTimeAndShopId(startDate, endDate, shopId);
                if (statisSearchList.size() != 0) {
                    for (StatisSearch statisSearch : statisSearchList) {
                        String relGoods = statisSearch.getRelGoods();
                        if (!StringUtils.isEmpty(relGoods)) {
                            String[] goodsIdArr = relGoods.split(",");
                            List<Integer> ids = new ArrayList<>();
                            for (String goodsId : goodsIdArr) {
                                if (!StringUtils.isEmpty(goodsId)) {
                                    ids.add(Integer.parseInt(goodsId));
                                }
                            }
//                    只取前面5个来判断类型
                            if (ids.size() > 5) {
                                ids = ids.subList(0, 5);
                            }
                            List<Goods> goods = goodsService.findAll(ids);

                            Map<String, Integer> catMap = categoryCountMap(goods);
                            Collection<Integer> countCollection = catMap.values();
                            List<Integer> countList = Arrays.asList(countCollection.toArray(new Integer[countCollection.size()]));
                            Collections.sort(countList);
                            if (countList.size() != 0) {
                                Integer maxCount = countList.get(countList.size() - 1);
                                StringBuilder builder = new StringBuilder();
                                for (Map.Entry<String, Integer> entry : catMap.entrySet()) {
                                    Integer value = entry.getValue();
                                    if (value.equals(maxCount)) {
                                        builder.append(entry.getKey());
                                        break;
                                    }
                                }
                                saveCategory = builder.toString();
                                if (StringUtils.isEmpty(jsonObject.getJSONObject(saveCategory))) {
                                    jsonObject.put(saveCategory, new JSONObject());
                                }
                                count = (Long) jsonObject.getJSONObject(saveCategory).get("count");
                                if (StringUtils.isEmpty(count)) {
                                    jsonObject.getJSONObject(saveCategory).put("count", 1L);
                                } else {
                                    jsonObject.getJSONObject(saveCategory).put("count", ++count);
                                }
                                ++countOfSearch;
                            }
                        }
                    }
                    for (Map.Entry<String, Object> categoryMap : jsonObject.entrySet()) {
                        Long i = (Long) ((JSONObject) categoryMap.getValue()).get("count");
                        String s = Math.round(((i * 1.0 / countOfSearch)) * 1000) / 10.0 + "%";
                        ((JSONObject) categoryMap.getValue()).put("ratio", s);

                        StatisticMongoCategory statisticMongoCategory = new StatisticMongoCategory();
                        statisticMongoCategory.setName(categoryMap.getKey());
                        statisticMongoCategory.setCount(i);
                        statisticMongoCategory.setRatio(s);
                        list.add(statisticMongoCategory);
                    }
                    StatisticMongoEntity statisticMongoEntity = new StatisticMongoEntity();
                    statisticMongoEntity.setData(list);
                    statisticMongoEntity.setShopId(shopId);
                    statisticMongoEntity.setDate(DateUtils.getYesterday(DateUtils.DATE_STRING_FORMAT));
                    statisticMongoEntity.setType(StatisticEnum.SEARCH.getType());
                    statisticMongoService.save(statisticMongoEntity);
                }
            }
        }

    }

    public void setTrack() {
        for (Map.Entry<Integer, Map<String, Integer>> map : HandleData.xAndYMap.entrySet()) {
            List<HeatMap> heatMapList = new ArrayList<>();
            Integer shopId = map.getKey();
            Long count = 0L;
            JSONObject jsonObject = new JSONObject();
            List<StatisticMongoCategory> list = new ArrayList<>();
            Long sum = 0L;
            Shop shop = shopService.findOne(shopId);
            for (Map.Entry<String, Integer> map2 : map.getValue().entrySet()) {
                String[] xY = map2.getKey().split(",");
                heatMapList.add(convert(xY[0], xY[1], map2.getValue()));

                String location = setLocation(xY[0], xY[1], shop);
                if (!"无效区域".equals(location) && location != null) {
                    if (StringUtils.isEmpty(jsonObject.getJSONObject(location))) {
                        jsonObject.put(location, new JSONObject());
                    }
                    count = (Long) jsonObject.getJSONObject(location).get("count");
                    if (StringUtils.isEmpty(count)) {
                        jsonObject.getJSONObject(location).put("count", 1L);

                    } else {
                        jsonObject.getJSONObject(location).put("count", ++count);
                    }
                    ++sum;
                }
            }
            for (Map.Entry<String, Object> m1 : jsonObject.entrySet()) {
                long i = (long) ((JSONObject) m1.getValue()).get("count");
                String s = Math.round(((i * 1.0 / sum)) * 1000) / 10.0 + "%";
                StatisticMongoCategory statisticMongoCategory = new StatisticMongoCategory();
                statisticMongoCategory.setName(m1.getKey());
                statisticMongoCategory.setCount(i);
                statisticMongoCategory.setRatio(s);
                list.add(statisticMongoCategory);
            }
//            把所有的track记录写入到mongodb
            StatisticMongoEntity statisticMongoEntity = new StatisticMongoEntity();
            if (!StringUtils.isEmpty(shopId)) {
                statisticMongoEntity.setShopId(shopId);
                statisticMongoEntity.setData(heatMapList);
                statisticMongoEntity.setType(StatisticEnum.TRACK.getType());
                statisticMongoEntity.setDate(DateUtils.getYesterday(DateUtils.DATE_STRING_FORMAT));
            }
            statisticMongoService.save(statisticMongoEntity);
//                把区域内的购物车次数写入到mongodb
            StatisticMongoEntity location = new StatisticMongoEntity();
            if (!StringUtils.isEmpty(shopId)) {
                location.setShopId(shopId);
                location.setData(list);
                location.setType(StatisticEnum.TRACKLOC.getType());
                location.setDate(DateUtils.getYesterday(DateUtils.DATE_STRING_FORMAT));
            }
            statisticMongoService.save(location);
        }
        HandleData.freshXAndYMap();
    }

    public Map<String, Integer> categoryCountMap(List<Goods> goodsList) {
        Map<String, Integer> catMap = new HashMap<>();
        for (Goods goods : goodsList) {
//            如果这个商品没有分类，是没用的
            if (goods.getCategory() == null) {
                continue;
            }
            Integer count = catMap.get(goods.getCategory().getCatName());
            if (count != null) {
                ++count;
                catMap.put(goods.getCategory().getCatName(), count);
            } else {
                catMap.put(goods.getCategory().getCatName(), 1);
            }
        }
        return catMap;
    }

    public HeatMap convert(String x, String y, int value) {
        HeatMap heatMap = new HeatMap();
        heatMap.setValue(value);
        heatMap.setX(Float.parseFloat(x));
        heatMap.setY(Float.parseFloat(y));
        return heatMap;
    }

    public String setLocation(String locationX, String locationY, Shop shop) {
        double x, y;
        x = Double.valueOf(locationX) % 1000;
        y = Double.valueOf(locationY) % 1000;
        int flag;
        Integer keyword = 0;
        String name = null;
        String shopName = shop.getName();
        if ("天虹-深圳-沙井店".equals(shopName)) {
            if (x > 840 && y > 554 && x < 940 && y < 618) {

                Map<Integer, Map> map = new HashMap<>();
                map.put(1, getMap(840, 597, 924, 618));
                map.put(2, getMap(840, 554, 867, 597));
                map.put(3, getMap(867, 554, 904, 570));
                map.put(4, getMap(924, 564, 940, 618));
                map.put(5, getMap(904, 576, 924, 597));

                for (Integer key : map.keySet()) {
                    //得到每个区域的左下和右上的坐标集合
                    Map<Double, Double> map1 = map.get(key);
                    flag = 0;
                    for (Map.Entry<Double, Double> entry : map1.entrySet()) {
                        if (flag == 0) {
                            if (x > entry.getKey() && y > entry.getValue()) {
                                flag = 1;
                                continue;
                            }
                        } else {
                            if (x < entry.getKey() && y < entry.getValue()) {
                                keyword = key;
                            }
                        }
                    }
                }
                switch (keyword) {
                    case 0:
                        name = "超市入口";
                        break;
                    case 1:
                        name = "食物饮料";
                        break;
//                    因为这块区域是购物车在没有定位成功时的默认区域，所以要排除
                    case 2:
                        if (x > 840 && x < 848 && y > 554 && y < 564) {
                            name = "无效区域";
                            break;
                        }
                        name = "婴宠家纺";
                        break;
                    case 3:
                        name = "日常护理";
                        break;
                    case 4:
                        name = "果蔬肉类";
                        break;
                    case 5:
                        name = "粮油";
                        break;
                }
            } else {
                name = "无效区域";
            }
        }
        if ("天虹北京新奥".equals(shopName)) {

            Map<Integer, Map> map = new HashMap<>();
            map.put(1, getMap(293, 864, 314, 891));
            map.put(2, getMap(314, 864, 347, 891));
            map.put(3, getMap(302, 843, 373, 864));
            map.put(4, getMap(302, 822, 373, 843));

            for (Integer key : map.keySet()) {
                //得到每个区域的左下和右上的坐标集合
                Map<Double, Double> map1 = map.get(key);
                flag = 0;
                for (Map.Entry<Double, Double> entry : map1.entrySet()) {
                    if (flag == 0) {
                        if (x > entry.getKey() && y > entry.getValue()) {
                            flag = 1;
                            continue;
                        }
                    } else {
                        if (x < entry.getKey() && y < entry.getValue()) {
                            keyword = key;
                        }
                    }
                }
            }
            switch (keyword) {
                case 0:
                    name = "无效区域";
                    break;
                case 1:
                    name = "奶制食物";
                    break;
                case 2:
                    name = "日用护理";
                    break;
                case 3:
                    name = "粮油生鲜";
                    break;
                case 4:
                    name = "休闲食品";
                    break;
            }

        }
        return name;
    }

    //存储一个区域的左下和右上坐标
    private static Map<Double, Double> getMap(double i1, double i2, double i3, double i4) {
        Map<Double, Double> map = new HashMap<>();
        map.put(i1, i2);
        map.put(i3, i4);
        return map;
    }

}