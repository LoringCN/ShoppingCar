package cn.huace.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 封装返回广告实体
 * Date:2018/3/5
 */
@Data
public class AdV2OV implements Serializable {
    private static final long serialVersionUID = -2718588999645969625L;

    private Integer id;
    private Integer type;
    private Integer relationType;
    private String url;
    private String md5;
    private String title;
    private Integer productId;
    private Integer promotionPrice;
    private Integer normalPrice;
    private String extra;
    private Integer rank;
}
