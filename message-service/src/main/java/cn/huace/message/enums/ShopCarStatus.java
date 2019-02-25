package cn.huace.message.enums;


/**
 * Created by yld on 2017/10/19.
 */
public enum  ShopCarStatus {
    GATHER("1"),
    ALARM("2"),
    HANLDED("3"),
    LOSS("4");
    private String value;
    ShopCarStatus(String value){
        this.value = value;
    }
    public String getValue(){
        return this.value;
    }
}
