package cn.huace.ad.util;

import org.omg.CORBA.PUBLIC_MEMBER;

/**
 * @author zhouyanbin 
 * @date Apr 26, 2017 9:19:53 AM 
 * @version 1.0 
*/
public class AdCodeConstants {
	/** 是 */
	public static final String YES = "1";
	/** 否 */
	public static final String NO = "0";
	
	/** 有效 */
	public static final String VALID = "1";
	/** 无效 */
	public static final String INVALID = "0";
	/**
	 * 线上广告
	 */
	public static final boolean ONLINE_AD = true;
	/**
	 * 线下广告
	 */
	public static final boolean OFFLINE_AD = false;
	/**
	 * 内部广告
	 */
	public static final boolean INNER_AD = true;
	/**
	 * 外部广告
	 */
	public static final boolean OUTER_AD = false;

	/**
	 * 广告类型
	 * @author zhouyanbin
	 */
	public static final class AdType{
		/** 图片广告 */
		public static final String AD_PICTURE = "1";
		/** 视频广告 */
		public static final String AD_VEDIO = "2";
		/** 促销广告 */
		public static final String AD_SELL = "3";
		/** 促销广告 */
		public static final String AD_THEME = "4";
	}

	/**
	 * 广告状态
	 */
	public static final class AdStatus{
		/**
		 * 正常状态
		 */
		public static final int NORMAL = 1;
		/**
		 * 下架状态
		 */
		public static final int UNSHELF = 2;
		/**
		 * 删除状态
		 */
		public static final int DELETED = 3;
	}
	/**
	 * 审核状态
	 */
	public static final class AuditStatus{
		/**
		 * 待审核
		 */
		public static final int TO_AUDIT = 0;
		/**
		 * 审核通过
		 */
		public static final int SUCCESS_AUDIT = 1;
		/**
		 * 审核失败
		 */
		public static final int FAILURE_AUDIT  = 2;
		/**
		 * 重新审核：审核失败修正后，进入该状态，确认后进入待审核状态
		 */
		public static final int RE_AUDIT = 3;
	}
	/**
	 * 广告投放方式
	 */
	public static final class DeliverMethod{
		/**
		 * 默认投放
		 */
		public static final int DELIVER_DEFAULT = -1;
		/**
		 * 指定设备投放
		 */
		public static final int DELIVER_DEVICE = 1;
	}
	/**
	 * 广告类型
	 */
	public static final class AdV2Type {
		// 开机视频广告
		public static final int TYPE_CODE_VIDEO = 2;
		// 首页轮播广告
		public static final int TYPE_CODE_INDEX_CAROUSEL = 1;
		// 特价封面广告
		public static final int TYPE_CODE_PROMOTION = 3;
		// 新品举介轮播广告
		public static final int TYPE_CODE_NEW_RECOMMEND_CAROUSEL = 4;
		// LBS广告
		public static final int TYPE_CODE_LBS = 5;
		// 搜索条底部广告
		public static final int TYPE_CODE_SEARCH_BOTTOM = 6;
		// 搜索无结果广告
		public static final int TYPE_CODE_SEARCH_NO_RESULT = 7;
	}
	/**
	 * 关联关系
	 */
	public static final class AdV2Relation {
		// 无关联
		public static final int NO_RELATION = 1;
		// 商品
		public static final int	GOODS_RELATION = 2;
		// 超链接
		public static final int LINK_RELATION = 3;
		// 商品和货架
		public static final int GOODS_AND_SHELF = 4;
	}
}
