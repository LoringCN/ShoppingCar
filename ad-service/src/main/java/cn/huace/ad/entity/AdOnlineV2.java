package cn.huace.ad.entity;

import cn.huace.common.entity.BaseEntity;
import lombok.Data;

import javax.persistence.*;

/**
 *
 * 线上广告表
 * Date:2018/1/22
 */
@Data
@Entity
@Table(
    name = "ad_online_v2",
    indexes = {
            @Index(name = "index_adOnlineV2_adId",columnList = "ad_id"),
            @Index(name = "index_adOnlineV2_devId",columnList = "dev_id"),
            @Index(name = "index_adOnlineV2_typeId",columnList = "type_id")
    }
)
public class AdOnlineV2 extends BaseEntity{
    private static final long serialVersionUID = -8325481167800459859L;

    @Column(name = "shop_id",nullable = false)
    private Integer shopId;
    /**
     * 广告ID
     */
    @Column(name = "ad_id",nullable = false)
    private Integer adId;
    /**
     * 设备号所对应记录ID
     */
    @Column(name = "dev_id",nullable = false)
    private Integer devId;

    /**
     * 广告类型ID
     */
    @Column(name = "type_id",nullable = false)
    private Integer typeId;

    /**
     * 广告位置排序，轮播广告有效(1-4),
     * 其他默认：0
     */
    private Integer rank;
}
