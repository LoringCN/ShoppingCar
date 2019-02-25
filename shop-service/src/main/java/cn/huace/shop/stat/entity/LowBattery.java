package cn.huace.shop.stat.entity;

import cn.huace.common.entity.BaseEntity;
import cn.huace.shop.shop.entity.Shop;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by xiaoling on 2017/5/26.
 */

@Data
@Entity
@Table(name = "low_battery")
public class LowBattery extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Column(name = "dev_id",length = 64)
    private String devId;

    @Column(name = "led_id")
    private String ledId;

    @Column(name = "location")
    private String loc;

    @Column(name = "count")
    private  Integer count;

    @Column(name = "process_flag")
    private Boolean isProcess;

    @Column(name = "x")
    private Float x;

    @Column(name = "y")
    private Float y;

}
