package cn.huace.shop.app.entity;

import cn.huace.common.entity.BaseEntity;
import cn.huace.shop.shop.entity.Shop;
import lombok.Data;

import javax.persistence.*;

/**
 * Created by herry on 2017/5/22.
 *
 * 超市报警LED ID LIST
 *
 */

@Data
@Entity
@Table(name = "t_alarm")
public class Alarm extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Column(name = "led_id_list",length = 1024)
    private String ledIdlist;
}
