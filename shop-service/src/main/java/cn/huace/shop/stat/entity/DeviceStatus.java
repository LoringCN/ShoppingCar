package cn.huace.shop.stat.entity;

import cn.huace.common.entity.BaseEntity;
import cn.huace.common.utils.EStatus;
import cn.huace.shop.shop.entity.Shop;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "t_device_status")
public class DeviceStatus extends BaseEntity {
    @Column(name = "dev_id", length = 64)
    private String devId;

    @Column(name = "location", length = 64)
    private String location;

    private int battery;

    private BigDecimal x;

    private BigDecimal y;

    @Column(name = "alarm_count",columnDefinition = "" + EStatus.NORMAL )
    private int alarmCount;

    /*用户标识状态*/
    private int status;


    @ManyToOne(optional = false)
    @JoinColumn(name = "shop_id")
    private Shop shop;
}
