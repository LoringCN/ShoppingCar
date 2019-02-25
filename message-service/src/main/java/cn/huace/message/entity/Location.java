package cn.huace.message.entity;

import cn.huace.common.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 购物车位置上报信息
 * Created by yld on 2017/10/19.
 */
@Data
@Entity
@Table(name = "location")
public class Location extends BaseEntity{
    private static final long serialVersionUID = -7251176998785661492L;

    @Column(name = "dev_id")
    private String devId;
    @Column(name = "shop_id")
    private Integer shopId;

    private String x;
    private String y;
    /*
        偏离位置，用于判断当前购物车状态
        单位：米
     */
    private Byte offset;

    /*
        楼层:
        byte --> tinyint
        范围: -128 ~ 127
     */
    @Column(name = "`floor`")
    private Byte floor;

    /*
        购物车状态：
           1 -- 集散
           2 -- 告警
           3 -- 已处理
           4 -- 丢失
     */
    @Column(name = "car_status")
    private String carStatus;

    private String reason;
    private String bt1_id;
    private String bt2_id;
}
