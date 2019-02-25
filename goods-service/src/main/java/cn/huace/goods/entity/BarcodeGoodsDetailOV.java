package cn.huace.goods.entity;

import lombok.Data;

/**
 * 封装扫描条形码，返回商品信息
 * Created by yld on 2017/6/21.
 */
@Data
public class BarcodeGoodsDetailOV extends GoodsListOV{
    private static final long serialVersionUID = 2981447986126085551L;

    private String sid;
    private String location;
    private String desc;
    private String barcode;

}
