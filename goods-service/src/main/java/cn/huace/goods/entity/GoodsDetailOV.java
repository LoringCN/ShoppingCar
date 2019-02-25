package cn.huace.goods.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by yld on 2017/5/19.
 */
@Data
public class GoodsDetailOV implements Serializable{
    private static final long serialVersionUID = 9093136053074408971L;

    private String sid;
    private String location;
    private String url;
    private String desc;

}
