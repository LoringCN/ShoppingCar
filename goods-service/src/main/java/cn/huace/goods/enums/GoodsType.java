package cn.huace.goods.enums;

import cn.huace.goods.base.EnumValue;

import java.math.BigDecimal;

/**
 * Created by yld on 2017/5/9.
 */
public enum GoodsType implements EnumValue<String>{
    NORMAL("normal"),
    PROMOTION("promotion"),
    OTHER("other");

    private String value;
    GoodsType(String value){
        this.value = value;
    }
    public String getValue() {
        return this.value;
    }
    public static GoodsType setValue(String value){
        return valueOf(value);
    }

     /*public static void main(String[] args) {
        GoodsType type = GoodsType.NORMAL;
        type.value = "normal";
        System.out.println(type.getValue());
        System.out.println(GoodsType.PROMOTION.getValue());
        System.out.println(GoodsType.setValue("NORMAL").getValue());
    }*/
}
