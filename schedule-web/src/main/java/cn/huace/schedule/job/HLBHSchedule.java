package cn.huace.schedule.job;

import cn.huace.common.utils.redis.RedisService;
import cn.huace.schedule.constant.ThirdPartyConstant;
import cn.huace.goods.entity.Goods;
import cn.huace.goods.entity.GoodsCategory;
import cn.huace.goods.entity.SyncGoods;
import cn.huace.goods.entity.SyncRecord;
import cn.huace.goods.service.GoodsService;
import cn.huace.goods.service.SyncGoodsService;
import cn.huace.goods.service.SyncRecoredService;
import cn.huace.shop.shop.entity.Shop;
import cn.huace.shop.shop.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 汇隆百货商品对接接口
 * Seconds Minutes Hours DayofMonth Month DayofWeek Year或
  Seconds Minutes Hours DayofMonth Month DayofWeek
 * Created by yld on 2017/8/21.
 */
@Slf4j
@Configuration
@EnableScheduling
public class HLBHSchedule extends BaseSchedule{

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private SyncRecoredService recoredService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private SyncGoodsService syncGoodsService;

    //每次同步请求商品数量
    private static final int PAGESIZE = 5;
    //超市PID前缀
    private static final String SHOP_PREFIX = "HLBH_";

    @Scheduled(cron = "0 0/2 * * * ?")
    public void syncNewAddGoods(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("****　执行同步新增商品任务！时间："+sdf.format(new Date()));
        String url = "http://localhost:8084/api/product/test/add";
        SyncRecord record = new SyncRecord();
        StringBuilder oIdBuilder = new StringBuilder();
        try {
            //查询商店信息
            Shop shop = shopService.findShopByShopName(ThirdPartyConstant.HLBH.SYNC_SHOP_NAME);
            //根据shopId获取所有分类信息
            Map<String,GoodsCategory> categoryMap = getCategoryCacheByShopId(shop);
            //查询上次成功同步时间
            Date syncTime = recoredService.findRecentSyncTime(
                    ThirdPartyConstant.SYNC_GOODS_TYPE_ADD,
                    ThirdPartyConstant.HLBH.SYNC_SHOP_NAME,
                    ThirdPartyConstant.SYNC_REQUEST_OK_STATE);

            if(syncTime == null){
                //不存在同步记录时间时，以1970初始化时间为准,即初始化
                String defaultTime = "1970-01-01 00:00:00";
                syncTime = sdf.parse(defaultTime);
            }
            //下一次同步时间
            Date nextSyncTime = new Date();
            //封装所有同步结果
            List<JSONObject> resultList = pageRequest(url,PAGESIZE,syncTime,nextSyncTime);

            if(!CollectionUtils.isEmpty(resultList)){
                int state = resultList.get(0).getInt("state");
                String msg = resultList.get(0).getString("msg");

                //同步回来修改商品总数
                int syncNum = 0;
                List<Goods> goodsList = new ArrayList<>();
                List<SyncGoods> syncGoodsList = new ArrayList<>();

                for(JSONObject result:resultList){
                    JSONArray content = result.getJSONArray("content");
                    syncNum += content.size();
                    for(int i = 0;i<content.size();i++){
                        Goods goods = new Goods();
                        SyncGoods syncGoods = new SyncGoods();

                        JSONObject obj = content.getJSONObject(i);

                        //记录原始商品属性至syncGoods,商品修改需要
                        String originId = obj.getString("id");
                        oIdBuilder.append(originId).append(",");

                        parseJSONObject2Goods(obj,goods,shop,categoryMap);
                        addSyncGoods(obj,syncGoods,SHOP_PREFIX,originId);

                        goodsList.add(goods);
                        syncGoodsList.add(syncGoods);
                    }
                }

                if(!CollectionUtils.isEmpty(goodsList)){
                    //批量保存新增商品
                    List<Goods> goods = goodsService.batchInsertGoods(goodsList);
                    if(!CollectionUtils.isEmpty(goods)){
                        for(int i = 0;i<goods.size();i++){
                            Integer goodsId = goods.get(i).getId();
                            //给syncGoods设置新增商品ID
                            syncGoodsList.get(i).setGId(goodsId);
                        }
                    }
                    //批量新增syncGoods
                    syncGoodsService.batchInsertSyncGoods(syncGoodsList);
                }
                //记录同步信息
                record.setSyncState(state);
                record.setDescr(msg);
                record.setShopName(ThirdPartyConstant.HLBH.SYNC_SHOP_NAME);
                record.setType(ThirdPartyConstant.SYNC_GOODS_TYPE_ADD);
                record.setSyncNum(syncNum);
                record.setSyncTime(nextSyncTime);
                String oIds = oIdBuilder.toString();
                if(!StringUtils.isEmpty(oIds)){
                    record.setOriginIds(oIds.substring(0,oIds.length()-1));
                }
                recoredService.save(record);
            }

        } catch (Exception e) {
            log.error("时间："+sdf.format(new Date())+",同步《新增商品》出错！error："+ e.getMessage(),e);
        }
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void syncUpdateGoods(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("****　开始执行同步修改商品任务！时间："+sdf.format(new Date()));
        String url = "http://localhost:8084/api/product/test/update";
        SyncRecord record = new SyncRecord();
        StringBuilder oIdBuilder = new StringBuilder();
        try {
            //查询商店信息
            Shop shop = shopService.findShopByShopName(ThirdPartyConstant.HLBH.SYNC_SHOP_NAME);
            //根据shopId获取所有分类信息
            Map<String,GoodsCategory> categoryMap = getCategoryCacheByShopId(shop);
            //查询上次成功同步时间
            Date syncTime = recoredService.findRecentSyncTime(
                    ThirdPartyConstant.SYNC_GOODS_TYPE_UPDATE,
                    ThirdPartyConstant.HLBH.SYNC_SHOP_NAME,
                    ThirdPartyConstant.SYNC_REQUEST_OK_STATE);

            if(syncTime == null){
                //不存在修改记录时，以最新一次同步新增时间为准
                syncTime = recoredService.findFirstSyncTime(
                        ThirdPartyConstant.SYNC_GOODS_TYPE_ADD,
                        ThirdPartyConstant.HLBH.SYNC_SHOP_NAME,
                        ThirdPartyConstant.SYNC_REQUEST_OK_STATE);
            }
            if(syncTime == null){//保证第一次同步新增数据在第一次同步修改数据之前
                return;
            }
            //下一次同步时间
            Date nextSyncTime = new Date();
            //封装所有同步结果
            List<JSONObject> resultList = pageRequest(url,PAGESIZE,syncTime,nextSyncTime);

            if(!CollectionUtils.isEmpty(resultList)){
                int state = resultList.get(0).getInt("state");
                String msg = resultList.get(0).getString("msg");

                //同步回来修改商品总数
                int syncNum = 0;
                //所有修改商品信息
                List<Goods> goodsList = new ArrayList<>();
                //所有需要更新SyncGoods信息
                List<SyncGoods> syncGoodsList = new ArrayList<>();

                for(JSONObject result:resultList){
                    JSONArray content = result.getJSONArray("content");
                    syncNum += content.size();
                    for(int i = 0;i<content.size();i++){
                        JSONObject obj = content.getJSONObject(i);
                        String id =  obj.getString("id");
                        oIdBuilder.append(id).append(",");
                        //根据合作方原始id唯一查询商品
                        SyncGoods syncGoods = syncGoodsService.findByOid(SHOP_PREFIX + id);
                        log.error("***** syncGoods={},id = {}",syncGoods,id);
                        if(syncGoods != null){
                            syncGoods = updateSyncGoods(obj,syncGoods);
                            syncGoodsList.add(syncGoods);

                            Goods goods = goodsService.findOne(syncGoods.getGId());
                            if(goods != null){
                                goods = parseJSONObject2Goods(obj,goods,shop,categoryMap);
                                //将同步回来商品存入集合
                                goodsList.add(goods);
                                syncGoods.setGId(goods.getId());
                            }
                        }
                    }
                }

                if(!CollectionUtils.isEmpty(goodsList)){
                    //批量更新修改商品
                    goodsService.batchUpdateGoods(goodsList);
//                    log.info("***** 同步修改商品数据 -->"+goodsList);

                    syncGoodsService.batchUpdateSyncGoods(syncGoodsList);
                }
                //记录同步信息
                record.setSyncState(state);
                record.setDescr(msg);
                record.setShopName(ThirdPartyConstant.HLBH.SYNC_SHOP_NAME);
                record.setType(ThirdPartyConstant.SYNC_GOODS_TYPE_UPDATE);
                record.setSyncNum(syncNum);
                record.setSyncTime(nextSyncTime);
                String oIds = oIdBuilder.toString();
                if(!StringUtils.isEmpty(oIds)){
                    record.setOriginIds(oIds.substring(0,oIds.length()-1));
                }
                recoredService.save(record);

            }

        } catch (Exception e) {
            log.error("时间："+sdf.format(new Date())+",同步《修改商品》出错！error："+ e.toString(),e);
        }
    }

    /**
     * 根据shopId获取商品分类
     */
    private Map<String,GoodsCategory> getCategoryCacheByShopId(Shop shop){
        String categoryRedisKey = ThirdPartyConstant.CATEGORY_REDIS_KEY_PREFIX + shop.getId();
        //取出所有分类信息
        @SuppressWarnings("unchecked")
        Map<String,GoodsCategory> categoryMap
                = (Map<String,GoodsCategory>)redisService.hgetAllCacheValue(categoryRedisKey,ThirdPartyConstant.DB_INDEX);

        return categoryMap;
    }
}
