package cn.huace.goods.document;

import cn.huace.common.constants.SolrCoreConstants;
import cn.huace.common.entity.BaseSearchDocument;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.SolrDocument;
import org.springframework.data.solr.repository.Score;

import java.util.Date;

/**
 *  Solr搜索document
 * Created by yld on 2017/6/23.
 */
@Data
@SolrDocument(solrCoreName = SolrCoreConstants.SOLR_CORE_NAME_GOODS)
public class SearchGoods extends BaseSearchDocument{
    private static final long serialVersionUID = 5779826318126976550L;

    /**
     * 购物车所属超市
     */
//    @Indexed
    @Field
    private Integer shopId;

    /**
     * 商品所属分类
     */
    @Field
    private Integer catId;
    /**
     * 商品名称
     */
    @Field
    private String title;
    /**
     * 商品折扣价
     */
//    @Indexed
    @Field
    private Integer promotionPrice;
    /**
     * 商品原价
     */
//    @Indexed
    @Field
    private Integer normalPrice;

    /**
     * 商品封面图片，目前只用到此字段
     */
    @JsonProperty("url")
//    @Indexed
    @Field
    private String coverImgUrl;

    /**
     * 商品详情图片，暂时没用
     */
    @JsonIgnore
//    @Indexed
    @Field
    private String detailImgUrl;

    /**
     * 商品所在位置信息（基于地图粗粒度位置）
     */
//    @Indexed
    @Field
    private String sid;

    /**
     * 商品所在货架信息
     */
//    @Indexed
    @Field
    private String location;

    /**
     * 商品详细信息介绍信息,如：颜色，尺寸等
     */
    @JsonProperty("desc")
//    @Indexed
    @Field
    private String descr;

    /**
     * 商品类型（normal - 正常，promotion - 特价）
     */
//    @Indexed
    @Field
    private String type;

    /**
     * 显示顺序
     */
//    @Indexed
    @Field
    private Integer sortNo;

    /**
     * 备注信息
     */
//    @Indexed
    @Field
    private String remark;

    /**
     * 售价
     */
    @Field
    private	 Double	price;
    /**
     * 会员价
     */
    @Field
    private	 Double	memberPrice;
    /**
     * 促销原价
     */
    @Field
    private	 Double	promotionalPrice;
    /**
     * 促销价
     */
    @Field
    private	 Double	promotionalSalePrice;
    /**
     * 促销开始日期
     */
    @Field
    private Date promotionStartDate;
    /**
     * 促销结束日期
     */
    @Field
    private	 Date	promotionEndDate;

    /**
     * 库存 (1:正常，0:缺货)
     */
    private	 Integer stock;

    /**
     * 商品条码
     */
//    @Indexed
    @Field
    private String barcode;

    @Field
    private String newRecommend;

    @Field
    private String seoTag;

    @Score
    private Float score;

    public SearchGoods(){}

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
