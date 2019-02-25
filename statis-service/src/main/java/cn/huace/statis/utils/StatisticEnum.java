package cn.huace.statis.utils;

/**
 * 统计类型枚举类
 *
 * @author Lin Huan
 * @version 1.0.0
 * @date 2018年9月12日
 * @desc 描述
 */
public enum StatisticEnum {

    SEARCH("1", "搜索统计"),
    HPADCLICK("2", "首页广告点击统计"),
    HPADSHOW("3", "首页广告展示统计"),
    LOCADCLICK("4", "定位广告点击统计"),
    LOCADSHOW("5", "定位广告展示统计"),
    TRACK("6", "购物车上报位置统计"),
    TRACKLOC("7", "位置区域统计"),
    YYADSHOW("8","运营广告展示统计");
    private String type;

    private String desc;

    private StatisticEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
