package cn.huace.goods.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yld on 2017/5/19.
 */
@Data
public class GoodsListOV implements Serializable{
    private static final long serialVersionUID = -1519231431295475794L;

    private Integer id;
    private Integer promotionPrice;
    private Integer normalPrice;
    private String url;
    private String title;

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
     * 当前时间
     */
    private  Date   currentTime;

    public GoodsListOV() {
        this.currentTime = new Date();
    }
}
