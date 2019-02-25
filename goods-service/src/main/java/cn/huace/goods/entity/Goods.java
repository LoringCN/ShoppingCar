package cn.huace.goods.entity;

import cn.huace.common.entity.BaseEntity;
import cn.huace.shop.shop.entity.Shop;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.Date;

/**
 * <商品表>
 * Created by yld on 2017/5/8.
 */
@Data
@Entity
@Table(name="goods")
public class Goods extends BaseEntity{
    private static final long serialVersionUID = 7484835195932444256L;
    /**
     * 购物车所属超市id
     */
    @ManyToOne(optional = false)//表之间inner join
    @JoinColumn(name = "shop_id")
    private Shop shop;

    /*
     * 表之间进行外连接防止因为有商品数据，没有商品分类导致查询不到数据
     */
    @ManyToOne(optional = true)
    @JoinColumn(name = "cat_id")
    private GoodsCategory category;
    /**
     * 商品名称
     */
   // @Column(name="title")
    private String title;
    /**
     * 商品折扣价
     */
    @Column(name="promotion_price")
    private Integer promotionPrice;
    /**
     * 商品原价
     */
    @Column(name="normal_price")
    private Integer normalPrice;

    /**
     * 商品封面图片，目前只用到此字段
     */
    @JsonProperty("url")
    @Column(name="cover_img_url")
    private String coverImgUrl;

    /**
     * 商品详情图片，暂时没用
     */
    @Column(name="detail_img_url")
    private String detailImgUrl;

    /**
     * 商品所在位置信息（基于地图粗粒度位置）
     */
    @Column(name="sid")
    private String sid;

    /**
     * 商品所在货架信息
     */
    @Column(name="location")
    private String location;

    /**
     * 商品详细信息介绍信息,如：颜色，尺寸等
     */
    @JsonProperty("desc")
    @Column(name="descr")
    private String descr;

    /**
     * 商品类型（normal - 正常，promotion - 特价）
     */
    @Column(name="type")
    private String type;

    /**
     * 显示顺序
     */
    //@Column(name="sort_no")
    private Integer sortNo;

    /**
     * 备注信息
     */
    @Column(name="remark")
    private String remark;

    /**
     * 商品条码
     */
    @Column(name = "barcode")
    private String barcode;
    /**
     * 标记新品举荐（1-新品举荐，0-不举荐）
     */
    @Column(name = "new_recommend",length = 2)
    private String newRecommend;

    /**
     * 删除标记：1 - 正常 ，-1 - 删除
     * */
    @Column(name = "flag")
    private String flag;

    /**
     * SEO标签：用于优化搜索引擎查询结果
     */
    @Column(name = "seo_tag")
    private String seoTag;

    /**
     * 售价
     */
    private	 Double	price;
    /**
     * 会员价
     */
    private	 Double	memberPrice;
    /**
     * 促销原价
     */
    private	 Double	promotionalPrice;
    /**
     * 促销价
     */
    private	 Double	promotionalSalePrice;
    /**
     * 促销开始日期
     */
    private  Date promotionStartDate;
    /**
     * 促销结束日期
     */
    private	 Date	promotionEndDate;

    /**
     * 库存 (1:正常，0:缺货)
     */
    private	 Integer stock;

    /**
     * 品牌名称
     */
    private	 String	brandName;

    public Goods() {
    }

    public Goods(String coverImgUrl,String detailImgUrl){
        this.coverImgUrl = coverImgUrl;
        this.detailImgUrl = detailImgUrl;
    }
    public Goods(Integer id , String title, Integer promotionPrice, Integer normalPrice, String coverImgUrl, String detailImgUrl) {
        this.title = title;
        this.promotionPrice = promotionPrice;
        this.normalPrice = normalPrice;
        this.coverImgUrl = coverImgUrl;
        this.detailImgUrl = detailImgUrl;
        this.setId(id);
    }
    public Goods(Integer id , String title, Integer promotionPrice, Integer normalPrice, String coverImgUrl,String sid,String location,String descr, String detailImgUrl) {
        this.title = title;
        this.promotionPrice = promotionPrice;
        this.normalPrice = normalPrice;
        this.coverImgUrl = coverImgUrl;
        this.sid = sid;
        this.location = location;
        this.descr = descr;
        this.detailImgUrl = detailImgUrl;
        this.setId(id);
    }
    public Goods(Integer id, String coverImgUrl, String sid, String location, String descr, String detailImgUrl,String barcode,Shop shop) {
        this.coverImgUrl = coverImgUrl;
        this.sid = sid;
        this.location = location;
        this.descr = descr;
        this.detailImgUrl = detailImgUrl;
        this.barcode = barcode;
        this.shop = shop;
        this.setId(id);
    }

    public Goods(Integer id , String title, Integer promotionPrice, Integer normalPrice, String coverImgUrl,String sid,String location,String descr, String detailImgUrl,
                 Double price,Double memberPrice,Double promotionalPrice, Double promotionalSalePrice ,Date promotionStartDate,Date promotionEndDate,Integer stock,String barcode
                 ) {
        this.title = title;
        this.promotionPrice = promotionPrice;
        this.normalPrice = normalPrice;
        this.coverImgUrl = coverImgUrl;
        this.sid = sid;
        this.location = location;
        this.descr = descr;
        this.detailImgUrl = detailImgUrl;
        this.setId(id);
        this.price = price;
        this.memberPrice=memberPrice;
        this.promotionalPrice =  promotionalPrice;
        this.promotionalSalePrice =promotionalSalePrice;
        this.promotionStartDate=promotionStartDate;
        this.promotionEndDate=promotionEndDate;
        this.stock = stock;
        this.barcode = barcode;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
