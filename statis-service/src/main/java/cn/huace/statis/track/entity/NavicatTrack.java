package cn.huace.statis.track.entity;

import cn.huace.common.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 导航轨迹对象 created by Loring on 2018-10-19
 */
@Data
@Entity
@Table(name = "navicat_track")
public class NavicatTrack extends BaseEntity {

    @Column(name = "at_time")
    private Date atTime;

    @Column(name = "batch_id")
    private Long batchId;

    @Column(name = "dev_id")
    private String devId;

    private String location;

    @Column(name = "ts")
    private String ts;

    @Column(name = "location_x")
    private String locationX;

    @Column(name = "location_y")
    private String locationY;

    @Column(name = "shop_id")
    private Integer shopId;
}
