package cn.huace.shop.bluetooth.entity;

import cn.huace.common.entity.IntBaseEntity;
import cn.huace.shop.shop.entity.Shop;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

/**
 * LED灯实体类
 * created by Loring on 2018-07-5
 */
@Data
@Entity
@Table(name = "lamb_config")
public class LambConfig extends IntBaseEntity {
    private static final long serialVersionUID = 3875576920801558430L;
    /**
     * 商店
     */
//    @ManyToOne(optional = false)
//    @JoinColumn(name = "shop_id")
//    private Shop shop;
    /**
     * 灯ID的十六进制串
     */
    @Column(name = "led_id")
    private String ledId;
    /**
     * 蓝牙MAC地址
     */
    private String mac;
    /**
     * 灯在地图中的X坐标
     */
    private Double x;
    /**
     * 灯在地图中的Y坐标
     */
    private Double y;
    /**
     * 灯光脉宽，十六进制串
     */
    private String pulse;
    /**
     * 蓝牙发射功率Enum值：0: -23db; 1: -4db; 2: 0db; 3: 4db
     */
    private Integer power;
    /**
     * base64签名串
     */
    private String sign;

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

}
