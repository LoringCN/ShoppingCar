package cn.huace.ads.entity;

import cn.huace.common.entity.IntBaseEntity;
import cn.huace.shop.device.entity.Device;
import lombok.Data;

import javax.persistence.*;

/**
 * 线上广告 实体类 created on 2018-05-30
 * @author Loring
 */
@Data
@Entity
@Table(name = "ads_online")
public class AdsOnline extends IntBaseEntity {
    private static final long serialVersionUID = 3875576920801558430L;
    /**
     * 商店
     */
    @Column(name = "shop_id")
    private Integer shopId;
    /**
     * 广告类型
     */
    private Integer type;
    /**
     * 广告
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "ad_id")
    private Ads ads;
    /**
     * 广告投放范围
     * 默认全部投放：-1; 指定设备：1
     */
    @Column(name = "deliver_scope")
    private Integer deliverScope;
    /**
     * 设备ID
     */
//    @ManyToOne(optional = false)
//    @JoinColumn(name = "dev_id")
//    private Device device;
    @Column(name = "dev_id")
    private String devId;
    /**
     * 排序
     * 排序字段（广告位置排序，轮播广告有效,默认0）
     */
    private Integer rank;


}
