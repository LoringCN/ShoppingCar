package cn.huace.entity;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * <商品表>
 * Created by Loring on 2018/11/15.
 */
@Data
public class GoodsRenewalVo {
    /**
     * 更新类型
     * add: 添加商品
     * modify: 修改商品
     * del：下架商品
     */
    private	 String	action;
    /**
     * 商品条码
     */
    private	 String	barcode;
    /**
     * 商品名称
     */
    private	 String	title;
    /**
     * 品牌编号
     */
    private	 String	brandId;
    /**
     * 品牌名称
     */
    private	 String	brandName;
    /**
     * 一级分类编号
     */
    private	 String	categoryId;
    /**
     * 一级分类名称
     */
    private	 String	categoryName;
    /**
     * 二级分类编号
     */
    private	 String	subCategoryId;
    /**
     * 二级分类名称
     */
    private	 String	subCategoryName;
    /**
     * 三级分类编号
     */
    private	 String	classificationId;
    /**
     * 三级分类名称
     */
    private	 String	classificationName;
    /**
     * 商品规格
     */
    private	 String	specification;
    /**
     * 商品详情
     */
    private	 String	description;
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
    private  String promotionStartDate;
    /**
     * 促销结束日期
     */
    private	 String	promotionEndDate;
    /**
     * 促销信息
     */
    private	 String	promotionInfo;
    /**
     * 产地
     */
    private	 String	origin;
    /**
     * 库存 (1:正常，0:缺货)
     */
    private	 Integer stock;
    /**
     * 店内码
     */
    private	 String	shopCode;
    /**
     * 门店编号
     */
    private	 String	shopId;
    /**
     * 门店名称
     */
    private	 String	shopName;
    /**
     * 单位编号
     */
    private	 String	unitId;
    /**
     * 单位名称
     */
    private	 String	unitName;


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
