package cn.huace.statis.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/14.
 */
public class StatContants {
    //运动轨迹
    public static final String STAT_TYPE_TRACK="track";
    //用户行为
    public static final String STAT_TYPE_UB="ub";

    public static final String UB_TYPE="type";

    public static final String STAT_SHOP_ID="shopId";
    //连接符
    public static final String STAT_COLON=":";

    public static final String DEV_ID = "devID";

    //用户搜索商品关键字
    public static final String STAT_TYPE_SEARCH = "search";

    //大于等于此版本允许导航位置上传
    public static final Integer NAVICAT_TRACK_VERSION = 10043;

    public static final class SEARCH_KEY{
        public static final String KEYWORD = "keyword";
        public static final String RESULT = "result";
        public static final String SHOP_ID = "shopId";
        public static final String REL_GOODS = "relGoods";
    }
    public static final class TRACK_KEY{
        public static final String Y = "y";
        public static final String DEV_Id = "devId";
        public static final String X = "x";
        public static final String TS = "ts";
        public static final String LOCATION = "location";
        public static final String SHOP_ID = "shopId";
    }
    //用户行为统计
    public static final class UB_TYPE_ST{
        public static final int UB_AD_PIC_SHOW = 1;
        public static final int UB_AD_VIDEO_PLAY = 2;
        public static final int UB_NAVI = 3;
        public static final int UB_NAVI_ARRIVED = 4;
        public static final int UB_BROWSER_PRODUCT_LIST = 5;
        public static final int UB_BROWSER_PROMOTION_PRODUCT_LIST = 6;
        public static final int UB_BROWSER_PRODUCT_DETAIL = 7;
        public static final int UB_CLICK_AD = 8;
        public static final int UB_CLICK_APP_CHECK = 9;
        public static final int UB_CLICK_LBS_AD = 10;
        public static final int UB_SHOW_LBS_AD = 11;
        public static final int UB_NEW_AD_SHOW = 12;
        public static final int UB_NEW_AD_CLICK = 13;
        public static final int UB_SHOW_YY_AD = 14;

        public static Map<Integer, String> UB_TYPE_MAP = new HashMap<Integer, String>() {
            {
                put(UB_AD_PIC_SHOW, "图片广告展示");
                put(UB_AD_VIDEO_PLAY, "视频广告播放");
                put(UB_NAVI, "导航起止地");
                put(UB_NAVI_ARRIVED, "导航目的地");
                put(UB_BROWSER_PRODUCT_LIST, "商品列表查看");
                put(UB_BROWSER_PROMOTION_PRODUCT_LIST, "特价商品列表查看");
                put(UB_BROWSER_PRODUCT_DETAIL, "查看商品详情");
                put(UB_CLICK_AD, "广告点击");
                put(UB_CLICK_APP_CHECK, "版本检查");
                put(UB_CLICK_LBS_AD,"定位广告点击");
                put(UB_SHOW_LBS_AD,"定位广告展示");
                put(UB_NEW_AD_SHOW,"新品轮播广告展示");
                put(UB_NEW_AD_CLICK,"新品轮播广告点击");
                put(UB_SHOW_YY_AD,"运营广告展示");
            }
        };



    }

}
