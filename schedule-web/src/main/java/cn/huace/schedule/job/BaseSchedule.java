package cn.huace.schedule.job;

import cn.huace.common.utils.redis.RedisService;
import cn.huace.goods.entity.Goods;
import cn.huace.goods.entity.GoodsCategory;
import cn.huace.goods.entity.SyncGoods;
import cn.huace.goods.enums.GoodsType;
import cn.huace.goods.service.GoodsCategoryService;
import cn.huace.goods.service.GoodsService;
import cn.huace.goods.service.SyncGoodsService;
import cn.huace.goods.util.RequestUtil;
import cn.huace.schedule.constant.ThirdPartyConstant;
import cn.huace.shop.shop.entity.Shop;
import cn.huace.shop.shop.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *  所有定时任务基类，此类主要任务：
 *   1.将所有商店信息存入redis,
 *      redisKey: shop:info
 *   2.将所有商店对应的所有商品分类存入redis
 *     redisKey：category:info:shopId,shopId不同商店不同
 *
 * Created by yld on 2017/9/21.
 */
@Slf4j
public class BaseSchedule implements InitializingBean{

    @Autowired
    private ShopService shopService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private GoodsCategoryService categoryService;

    /**
     * 发送分页同步数据请求，封装结果数据
     * @param url 请求URL
     * @param lastSyncTime 上一次同步时间
     * @param pageSize 每次同步取回商品数据
     * @param nextSyncTime 记录下一次同步时间
     * @return 请求结果
     * @throws IOException
     */
    protected List<JSONObject>  pageRequest(String url,Integer pageSize,Date lastSyncTime,Date nextSyncTime) throws IOException {
        //请求返回商品数
        int goodsNum = 0;
        int pageNo = 1;
        //发送同步请求次数
        int requestCount = 0;
        //封装所有同步结果
        List<JSONObject> resultList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        do{
            Map<String,Object> params = new HashMap<>();
            params.put("timestamp",sdf.format(lastSyncTime));
            params.put("pageNo",pageNo);
            params.put("pageSize",pageSize);
            //添加随机字符串

            //计算签名参数sign

            //发送请求，获取响应数据
            JSONObject result = RequestUtil.sendHttpPost(url,params);
            if(result != null && !result.isEmpty()){
                nextSyncTime = new Date();
                JSONArray content = result.getJSONArray("content");
                resultList.add(result);
                goodsNum = content.size();
                pageNo += 1;
            }else {
                nextSyncTime = new Date();
            }
            requestCount ++;
        } while (goodsNum >= pageSize);

        log.info(" ******* 同步新增商品请求次数，requestCount = "+requestCount);
        return resultList;
    }

    /**
     * 封装同步回来的<em><b color="blue">新增商品</b></em>数据
     * @param resultList 同步请求返回修改商品json数据集合
     * @param shopPrefix 超市名简写
     * @param shop  超市
     * @param categoryMap 商品分类信息
     * @return
     */
    protected Map<String,Object> handleAddResult(
            List<JSONObject> resultList,String shopPrefix,
            Shop shop,Map<String,GoodsCategory> categoryMap){

        //同步回来修改商品总数
        int syncNum = 0;
        //存放处理后数据
        Map<String,Object> resultMap = new HashMap<>();
        //所有修改原始商品ID
        StringBuilder oIdBuilder = new StringBuilder();
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
                oIdBuilder.append(originId+",");

                parseJSONObject2Goods(obj,goods,shop,categoryMap);
                addSyncGoods(obj,syncGoods,shopPrefix,originId);

                goodsList.add(goods);
                syncGoodsList.add(syncGoods);
            }
        }
        resultMap.put("syncNum",syncNum);
        resultMap.put("oIdBuilder",oIdBuilder.toString());
        resultMap.put("goodsList",goodsList);
        resultMap.put("syncGoodsList",syncGoodsList);

        return resultMap;
    }
    /**
     * 封装同步回来的<em><b color="blue">修改商品</b></em>数据
     * @param resultList 同步请求返回修改商品json数据集合
     * @param syncGoodsService 我方与超市商品数据映射关系服务
     * @param goodsService 我方商品服务
     * @param shopPrefix 超市名简写
     * @param shop  超市
     * @param categoryMap 商品分类信息
     * @return
     */
    protected Map<String,Object> handleUpdateResult(
            List<JSONObject> resultList, SyncGoodsService syncGoodsService, GoodsService goodsService,
            String shopPrefix,Shop shop,Map<String,GoodsCategory> categoryMap){
        //同步回来修改商品总数
        int syncNum = 0;
        //存放处理后数据
        Map<String,Object> resultMap = new HashMap<>();
        //所有修改原始商品ID
        StringBuilder oIdBuilder = new StringBuilder();
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
                oIdBuilder.append(id+",");
                //根据合作方原始id唯一查询商品
                SyncGoods syncGoods = syncGoodsService.findByOid(shopPrefix + id);
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

        resultMap.put("syncNum",syncNum);
        resultMap.put("oIdBuilder",oIdBuilder.toString());
        resultMap.put("goodsList",goodsList);
        resultMap.put("syncGoodsList",syncGoodsList);

        return resultMap;
    }

    /**
     *  解析返回json商品数据，封装成Goods返回
     * @param goods 原始商品数据
     * @param obj 返回结果商品数据
     * @param shop 超市
     * @param categoryMap 超市商品分类
     * @return
     */
    protected Goods parseJSONObject2Goods(JSONObject obj,Goods goods,Shop shop,Map<String,GoodsCategory> categoryMap){
        if(obj == null || goods == null || shop == null || categoryMap == null){
            return null;
        }

        goods.setTitle(obj.getString("goodsName"));
        Object normalPrice = obj.get("normalPrice");
        if(!StringUtils.isEmpty(normalPrice)&& !"null".equalsIgnoreCase(String.valueOf(normalPrice))){
            goods.setNormalPrice(Integer.parseInt(String.valueOf(normalPrice)));
        }
        Object promotionPrice = obj.get("promotionPrice");
        if(!StringUtils.isEmpty(promotionPrice) && !"null".equalsIgnoreCase(String.valueOf(promotionPrice))){
            goods.setPromotionPrice(Integer.parseInt(String.valueOf(promotionPrice)));
        }
        goods.setRemark(obj.getString("remark"));
        String barCode = obj.getString("barcode");
        if(!StringUtils.isEmpty(barCode) && !"null".equalsIgnoreCase(String.valueOf(barCode))){
            goods.setBarcode(barCode);
        }else{
            goods.setBarcode(null);
        }
        goods.setDescr(obj.getString("goodsFeature"));

        goods.setShop(shop);
        goods.setNewRecommend("0");
        goods.setType(GoodsType.NORMAL.getValue());
        goods.setSortNo(5);
        //商品是否上架
        Boolean isShelf = obj.getBoolean("isShelf");
        String flag = isShelf?"1":"-1";
        goods.setFlag(flag);

        //商品图片
        String imgUrl = obj.getString("imgUrl");
        if(imgUrl != null && !"null".equalsIgnoreCase(imgUrl)){
            goods.setCoverImgUrl(imgUrl);
        }
//                            if(!StringUtils.isEmpty(syncGoods) && !syncImgUrl.equalsIgnoreCase(originImgUrl)){
//                                //更换了商品图片
//                                String srcImgUrl = SystemConfig.getInstance().getFilePre() + syncImgUrl;
//                                String imgName = RequestUtil.saveImg(srcImgUrl, Contants.GOODS_IMG_TEMP_FOLDER);
//                                goods.setCoverImgUrl(imgName);
//                                syncGoods.setOriginImgUrl(syncImgUrl);
        //删除旧图片
//                                boolean isdeleted = OssDeletionUtils.delete(goods.getCoverImgUrl());
//                                log.info("****** 删除图片："+isdeleted);
//                            }

        //匹配商品分类
        String categoryName = obj.getString("categoryName");
        GoodsCategory category = categoryMap.get(categoryName);
        goods.setCategory(category);
        return goods;
    }
    /**
     *  新增sync_goods表记录
     * @param obj 返回结果商品数据
     * @param syncGoods 我方与超市商品数据映射关系实体
     * @param shopPrefix 超市名简写
     * @param originId 商品原始ID
     * @return
     */
    protected SyncGoods addSyncGoods(JSONObject obj,SyncGoods syncGoods,String shopPrefix,String originId){
        if(obj == null || syncGoods == null){
            return null;
        }
        syncGoods.setOriginId(shopPrefix + originId);
        updateSyncGoods(obj,syncGoods);
        return syncGoods;
    }
    /**
     *  更新sync_goods表记录
     * @param obj 返回结果商品数据
     * @param syncGoods 我方与超市商品数据映射关系实体
     * @return
     */
    protected SyncGoods updateSyncGoods(JSONObject obj,SyncGoods syncGoods){
        if(obj == null || syncGoods == null){
            return null;
        }
        //商品是否上架
        Boolean isShelf = obj.getBoolean("isShelf");
        String flag = isShelf?"1":"-1";
        syncGoods.setFlag(flag);
        //商品图片
        String imgUrl = obj.getString("imgUrl");
        if(imgUrl != null && !"null".equalsIgnoreCase(imgUrl)){
            syncGoods.setOriginImgUrl(imgUrl);
        }
        return syncGoods;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("********** 启动应用，开始缓存shop、category信息");
        try{
            /*
                如果已缓存，则此处不在重新缓存，交由定时任务去更新缓存，可以避免多个超市
                继承此基类时，重复刷新缓存。
                判断依据：
                    检查redis中是否有shop和category缓存操作时间，
                    若shop或category其中任意一个缓存时间不存在，则执行后续所有操作，否则终止后续操作。
             */
            String lastShopCacheTimeStr = redisService.getCacheValue(ThirdPartyConstant.SHOP_SYNC_TIME,ThirdPartyConstant.DB_INDEX);
            String lastCategoryCacheTimeStr = redisService.getCacheValue(ThirdPartyConstant.CATEGORY_SYNC_TIME,ThirdPartyConstant.DB_INDEX);
            if(!StringUtils.isEmpty(lastShopCacheTimeStr) && !StringUtils.isEmpty(lastCategoryCacheTimeStr)){
                log.info("*********** shop、category数据已缓存！！！");
                return;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //1.查询所有正常开业商店
            List<Shop> shopList = shopService.findAllAviableShop();
            //记录shop数据缓存时间
            redisService.setCacheValue(
                    ThirdPartyConstant.SHOP_SYNC_TIME,
                    sdf.format(new Date()),
                    ThirdPartyConstant.DB_INDEX);

            //2.查询所有商品分类
            List<GoodsCategory> categoryList = categoryService.findAllCategoryAvailable();
            //记录category数据缓存时间
            redisService.setCacheValue(
                    ThirdPartyConstant.CATEGORY_SYNC_TIME,
                    sdf.format(new Date()),
                    ThirdPartyConstant.DB_INDEX);

            //3.封装商店和分类数据
            Map<String,Object> shopMap = new HashMap<>();
            Map<String,Object> categoryMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(shopList)){
                for(Shop shop:shopList){
                    shopMap.put(String.valueOf(shop.getId()),shop);
                }
            }
            if(!CollectionUtils.isEmpty(categoryList)){
                for(Shop shop:shopList){
                    Map<String,Object> shopCategoryMap = new HashMap<>();
                    Integer shopId = shop.getId();
                    for(GoodsCategory category:categoryList){
                        if(shopId == category.getShop().getId()){
                            shopCategoryMap.put(String.valueOf(category.getCatName()),category);
                        }
                    }
                    categoryMap.put(String.valueOf(shop.getId()),shopCategoryMap);
                }
            }
            //存入rediss
            if(!shopMap.isEmpty()){
                redisService.hmsetCacheValue(ThirdPartyConstant.SHOP_REDIS_KEY,shopMap,ThirdPartyConstant.DB_INDEX,null);
                log.info("************ 缓存shop信息成功！");
            }
            if(!categoryMap.isEmpty()){
                for (Map.Entry<String,Object> entry:categoryMap.entrySet()){
                    @SuppressWarnings("unchecked")
                    Map<String,Object> categoryCache = (Map<String,Object>)entry.getValue();
                    if(!categoryCache.isEmpty()){
                        redisService.hmsetCacheValue(
                                ThirdPartyConstant.CATEGORY_REDIS_KEY_PREFIX + entry.getKey(),
                                categoryCache,ThirdPartyConstant.DB_INDEX,null);
                    }
                }
                log.info("************ 缓存category信息成功！");
            }
        }catch (Exception e){
            log.error("************** 缓存shop、category信息失败！",e);
        }

    }
}
