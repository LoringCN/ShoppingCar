package cn.huace.schedule.job;

import cn.huace.common.utils.redis.RedisService;
import cn.huace.goods.entity.GoodsCategory;
import cn.huace.goods.service.GoodsCategoryService;
import cn.huace.schedule.constant.ThirdPartyConstant;
import cn.huace.shop.shop.entity.Shop;
import cn.huace.shop.shop.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于定时更新商店和对应分类信息，并更新缓存
 * 更新策略：
 *  上一次更新时间 <= modifiedTime
 * Created by yld on 2017/9/21.
 */
@Slf4j
@Configuration
@EnableScheduling
public class ShopCategorySchedule {
    @Autowired
    private RedisService redisService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private GoodsCategoryService categoryService;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void updateShopAndCategoryCache(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("*************** 开始更新shop及category缓存，时间："+sdf.format(new Date()));
        try {
            String lastShopCacheTimeStr = redisService.getCacheValue(ThirdPartyConstant.SHOP_SYNC_TIME,ThirdPartyConstant.DB_INDEX);
            String lastCategoryCacheTimeStr = redisService.getCacheValue(ThirdPartyConstant.CATEGORY_SYNC_TIME,ThirdPartyConstant.DB_INDEX);
            //取出上一次缓存时间
            Date lastShopCacheTime =  sdf.parse(lastShopCacheTimeStr);
            Date lastCategoryCacheTime = sdf.parse(lastCategoryCacheTimeStr);
            log.info("*********** 上次缓存时间：shop = {},category = {}",lastShopCacheTimeStr,lastCategoryCacheTimeStr);
            //根据时间查询新增或者修改的数据
            List<Shop> shopList = shopService.findModifyAndNewShops(lastShopCacheTime);
            //记录shop数据缓存时间
            redisService.setCacheValue(
                    ThirdPartyConstant.SHOP_SYNC_TIME,
                    sdf.format(new Date()),
                    ThirdPartyConstant.DB_INDEX);
            List<GoodsCategory> categoryList = categoryService.findModifyAndNewCategories(lastCategoryCacheTime);
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
                        if(shopId.equals(category.getShop().getId())){
                            shopCategoryMap.put(String.valueOf(category.getCatName()),category);
                        }
                    }
                    categoryMap.put(String.valueOf(shop.getId()),shopCategoryMap);
                }
            }
            //存入redis
            if(!shopMap.isEmpty()){
                redisService.hmsetCacheValue(ThirdPartyConstant.SHOP_REDIS_KEY,shopMap,ThirdPartyConstant.DB_INDEX,null);
                log.info("********** 定时更新shop缓存数据成功！");
            }
            if(!categoryMap.isEmpty()){
                for (Map.Entry<String,Object> entry:categoryMap.entrySet()){
                    @SuppressWarnings("unchecked")
                    Map<String,Object> categoryCache = (Map<String,Object>)entry.getValue();
                    if(!categoryCache.isEmpty()){
                        redisService.hmsetCacheValue(
                                ThirdPartyConstant.CATEGORY_REDIS_KEY_PREFIX + entry.getKey(),
                                categoryCache,ThirdPartyConstant.DB_INDEX,null);
                        log.info("********** 定时更新category缓存数据成功！");
                    }
                }
            }
        } catch (ParseException e) {
            log.error("********* 缓存时间解析失败！",e);
        }catch (Exception e){
            log.error("*********　定时更新shop或category信息失败！",e);
        }

    }
}
