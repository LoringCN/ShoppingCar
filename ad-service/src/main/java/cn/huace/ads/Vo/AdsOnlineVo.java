package cn.huace.ads.Vo;

import cn.huace.common.Vo.BaseVo;
import lombok.Data;

/**
 * 线上广告VO created on 2018-05-29
 * @author Loring
 */
@Data
public class AdsOnlineVo extends BaseVo {
    /**
     * ID
     */
    private Integer id;

    /**
     * 商店
     */
    private Integer shopId;
    /**
     * 广告类型
     */
    private Integer type;
    /**
     * 广告对象Vo
     */
    private AdsVo adsVo;
//    /**
//     * 广告ID
//     */
//    private Integer adId;
    /**
     * 广告投放范围
     * 默认全部投放：-1; 指定设备：1
     */
    private Byte deliverScope;
    /**
     * 设备ID
     */
    private String devId;
    /**
     * 排序
     * 排序字段（广告位置排序，轮播广告有效,默认0）
     */
    private Integer rank;

}
