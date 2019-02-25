package cn.huace.ad.entity;

import cn.huace.common.entity.BaseEntity;
import cn.huace.shop.shop.entity.Shop;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * V2版本广告
 */
@Data
@Entity
@Table(name = "ad_v2")
public class AdV2 extends BaseEntity{
    private static final long serialVersionUID = 3875576920801558430L;

    @ManyToOne(optional = false)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne(optional = false)
    @JoinColumn(name = "type_id")
    private AdTypeV2 type;

    @ManyToOne(optional = false)
    @JoinColumn(name = "relation_id")
    private AdRelationV2 relation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id")
    private AdGroupV2 group;

    /**
     * 广告名
     */
    @Column(name = "name",length = 128)
    private String name;

    /**
     * 关联关系的值
     * 无关联 --> null
     * 关联商品 --> goodsId或货架IDs(定位广告使用)
     * 关联超链接 --> 完整url
     * 关联商品和货架 --> goodsId,sid,sid
     */
    private String extra;

    /**
     * 是否被选中为线上广告
     */
    @Column(name = "is_voted")
    private Boolean voted;

    /**
     * 广告资源在OSS上相对路径
     */
    private String url;

    /**
     * 广告资源文件MD5值
     */
    @Column(length = 32)
    private String md5;

    /**
     * 广告状态
     * 1：正常，2：下架，3：已删除
     * 默认值为1
     */
    private Byte status;

    /**
     * 0：待审核
     * 1：审核通过
     * 2：审核未通过
     * 3：重新提交待审核(上一次审核失败)
     */
    @Column(name = "audit")
    private Byte audit;

    /**
     * 广告投放方式
     * 默认：-1
     * 指定设备：1
     */
    @Column(name = "deliver_method")
    private Byte deliverMethod;

    /**
     * 广告标识：
     * true -- 内部广告
     * false -- 外部广告
     */
    private Boolean flag;

    /**
     * 广告开始生效时间戳
     */
    @Column(name = "active_time")
    private Date activeTime;

    /**
     * 广告结束时间戳
     */
    @Column(name = "overdue_time")
    private Date overdueTime;

    /**
     * 广告描述信息
     */
    @Column(length = 128)
    private String description;

    /**
     *  广告价格
     */
    private Integer price;

    /**
     * 广告剩余有效天数
     */
    @Transient
    private Integer days;
    /**
     *  投放广告位(轮播广告有效，其他为0)
     */
    @Transient
    @JsonIgnore
    private Integer rank;

    @Transient
    @JsonIgnore
    private Date onlineTime;

    @Transient
    private String goodsName;
}
