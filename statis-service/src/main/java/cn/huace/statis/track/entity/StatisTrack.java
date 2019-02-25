package cn.huace.statis.track.entity;


import cn.huace.common.entity.BaseEntity;
import cn.huace.shop.shop.entity.Shop;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by huangdan on 2017/5/2.
 */
@Data
@Entity
@Table(name = "statis_track")
public class StatisTrack extends BaseEntity {

    @Column(name = "shop_id")
    private Integer shopId;

    @Column(name = "dev_id")
    private String devId;

    @Column(name = "location_x")
    private String locationX;

    @Column(name = "location_y")
    private String locationY;

    @Column(name = "at_time")
    private Date atTime;

    private String location;

    @Transient
    private Shop shop;

}
