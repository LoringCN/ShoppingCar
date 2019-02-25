package cn.huace.ads.constant;

import org.apache.xmlbeans.impl.xb.xmlconfig.Extensionconfig;

/**
 * 常量
 */
public final class AdsConstant {
    /**
     * 广告
     */
    public static final class  ADS{
        /**
         * 校验码
         * 1 - 查询所有广告、根据状态查询广告、根据类型查询广告；
         * 2 - 根据广告上架状态查询广告列表
         * 3 - 根据广告类型查询广告列表
         * 4 - 按名称查询
         */
        public interface CHECK_CODE{
            /**
             * 1 - 查询所有广告、根据状态查询广告、根据类型查询广告；
             */
            public static final String ALL_ = "1";
            /**
             *  2 - 根据广告上架状态查询广告列表
             */
            public static final String SHELF_ = "2";
            /**
             * 3 - 根据广告类型查询广告列表
             */
            public static final String TYPE_ = "3";
            /**
             * 4 - 按名称查询
             */
            public static final String NAME_ = "4";

        }

        /**
         * 审核状态
         * -1：初始态 , 0:待审核,1:审核中,2:审核通过,3:下发修改，4-提交上级 ,9:审核不通过。
         */
        public interface AUDIT_STATUS{
            /**
             * -1: 初始态
             */
            public static final int INITIAL_ = -1;
            /**
             * 0:待审核
             */
            public static final int WAITING_ = 0;
            /**
             * 1:审核中
             */
            public static final int DEALING_ = 1;
            /**
             * 2:审核通过
             */
            public static final int PASS_ = 2;
            /**
             * 3:下发修改
             */
            public static final int BACK_ = 3;
            /**
             * 4:提交上级
             */
            public static final int UPER_ = 4;
            /**
             * 5:审核不通过
             */
            public static final int REFUSE_ = 9;

        }

        /**
         * 是否上架
         */
        public interface IS_SHELF{
            /**
             * 下架
             */
            public static final int INVAILD_ = 0;
            /**
             * 上架
             */
            public static final int VAILD_ = 1;
        }

        /**
         * 投放类型：-1 ：默认投放；1：指定投放
         */
        public interface DeliverScope {
            /**
             * -1 ：默认投放
             */
            public static final int DEFAULT_SCOPE = -1;
            /**
             * 1：指定投放
             */
            public static final int ONLY_SCOPE  = 1;
        }


        /**
         * 下架
         */
        public static final String UN_SHELF = "unshelf";
        /**
         * 上架
         */
        public static final String ON_SHELF = "onshelf";

        /**
         * 广告类型：
         * 1-开机视频广告
         * 2-首页轮播广告
         * 3-特价封面广告
         * 4-新品轮播广告
         * 5-LBS广告
         */
        public interface AD_TYPE {
            /**
             * 1-开机视频广告
             */
            public static final int BOOT_VIDEO = 1;
            /**
             * 2-首页轮播广告
             */
            public static final int HOME_PAGE  = 2 ;
            /**
             * 3-特价封面广告
             */
            public static final int SPECIAL_COVER  = 3 ;
            /**
             * 4-新品轮播广告
             */
            public static final int NEW_PAGE  = 4 ;
            /**
             * 5-LBS广告
             */
            public static final int LBS  = 5 ;

        }

        /**
         * 关联关系
         * 1-无关联
         * 2-商品
         * 3-超链接
         * 4-商品和货架
         */
        public interface RELATION_CODE {
            /**
             * 无关联
             */
            public static final int NULL_ = 1;
            /**
             * 商品
             */
            public static final int GOODS_ = 2;
            /**
             * 超链接
             */
            public static final int URL_ = 3;
            /**
             * 商品和货架
             */
            public static final int LBS_ = 4;
        }
    }

    /**
     * 广告审核
     */
    public static final class ADS_AUDIT{

        /**
         * 校验码
         * 1-查询所有任务
         * 2-查询初始态、下发修改
         * 3-待审核、审核中
         * 9-拒绝
         */
        public interface CHECK_CODE{
            /**
             * 1-查询所有任务
             */
            public static final String ALL_ = "1";
            /**
             * 2-查询初始态、下发修改
             */
            public static final String INITIAL_BACK_ = "2";
            /**
             * 3-待审核、审核中
             */
            public static final String WAITING_DEALING_ = "3";
            /**
             * 9-拒绝
             */
            public static final String REFUSE_9 = "9";
        }

        /**
         * 提交审核
         * 1-提交审核；2-审核通过；3-驳回；4-拒绝；5-提交上级审核。
         */
        public interface AUDIT_ACTION{
            /**
             * 提交审核
             */
            public static final int TO_AUDIT = 1;
            /**
             * 通过
             */
            public static final int TO_PASS = 2;
            /**
             * 驳回
             */
            public static final int TO_BACK = 3;
            /**
             * 拒绝
             */
            public static final int TO_REFUSE = 4;
            /**
             * 提交上级审核
             */
            public static final int TO_UPPER = 5;
        }



    }

}
