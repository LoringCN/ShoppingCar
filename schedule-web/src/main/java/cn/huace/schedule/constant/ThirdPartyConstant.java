package cn.huace.schedule.constant;


/**
 * 合作超市常量定义
 * Created by yld on 2017/8/24.
 */
public class ThirdPartyConstant {
    //商店信息redisKey
    public static final String SHOP_REDIS_KEY = "shop:info";
    //商店信息同步时间redisKey
    public static final String SHOP_SYNC_TIME = "shop:sync:time";
    //商品分类信息redisKey
    public static final String CATEGORY_REDIS_KEY_PREFIX = "category:info:";
    //商品分类信息同步时间redisKey
    public static final String CATEGORY_SYNC_TIME = "category:sync:time";
    //存储数据redis库
    public static final Integer DB_INDEX = 1;

    //同步新增商品
    public static final String SYNC_GOODS_TYPE_ADD = "add";
    //同步修改或上架商品
    public static final String SYNC_GOODS_TYPE_UPDATE = "update";
    //同步删除或下架商品
    public static final String SYNC_GOODS_TYPE_DELETE = "del";
    //请求返回状态
    public static final int SYNC_REQUEST_OK_STATE = 0;

    //汇隆百货
    public static class HLBH{
        public static final String SYNC_SHOP_NAME = "测试超市";
    }
}
