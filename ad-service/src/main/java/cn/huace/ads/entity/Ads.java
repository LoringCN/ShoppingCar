package cn.huace.ads.entity;

import cn.huace.common.entity.IntBaseEntity;
import cn.huace.shop.shop.entity.Shop;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.Date;

/**
 * 广告最新基础版本 created on 2018-05-25
 * @author Loring
 */
@Data
@Entity
@Table(name = "ads")
public class Ads extends IntBaseEntity {
    private static final long serialVersionUID = 3875576920801558430L;
    /**
     * 商店
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "shop_id")
    private Shop shop;
    /**
     * 广告类型
     */
    @Column(name = "type")
    private Integer type;
    /**
     * 广告关联关系
     */
    @Column(name = "relation_code")
    private Integer relationCode;
    /**
     * 广告主
     */
    @Column(name = "owner_code")
    private Integer ownerCode;

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
     * 关联商品和货架 --> goodsId,sid,sid...
     */
    @Column(name = "relation_extra")
    private String relationExtra;

    /**
     * 是否被选中为线上广告
     */
    @Column(name = "is_voted")
    private Boolean isVoted;

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
     * 1：上架，0：下架
     * 默认值为1
     */
    @Column(name = "is_shelf")
    private Integer isShelf;

    /**
     * 审核状态
     * -1：初始态 , 0:待审核,1:审核中,2:审核通过,3:下发修改，4-提交上级 ,9:审核不通过。
     */
    @Column(name = "audit_status")
    private Integer auditStatus;

    /**
     * 广告投放范围
     * 默认全部投放：-1; 指定设备：1
     */
    @Column(name = "deliver_scope")
    private Integer deliverScope;

    /**
     * 广告标识：
     * true -- 默认广告
     * false -- 正式广告
     */
    private Boolean isDefalut;

    /**
     * 广告开始生效时间戳
     */
    @Column(name = "effect_time")
    private Date effectTime;

    /**
     * 广告结束时间戳
     */
    @Column(name = "expiry_time")
    private Date expiryTime;

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
     *  审核拒绝原因
     */
    private String reason;

//    /**
//     * 有效标志位
//     */
//    @Column(name = "is_enabled")
//    private Boolean isEnabled;

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

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }
}
