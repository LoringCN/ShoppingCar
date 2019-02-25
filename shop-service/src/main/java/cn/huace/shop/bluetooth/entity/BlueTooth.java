package cn.huace.shop.bluetooth.entity;

import cn.huace.common.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Iterator;

/**
 * Created by wjcomputer on 2017/10/30.
 */

@Data
@Entity
@Table(name="bluetooth")
public class BlueTooth extends BaseEntity {

    @Column(name = "bt_id")
    private String blueToothId;

    @Column(name="floor_no")
    private Integer floorNo;

    @Column(name="shop_id")
    private Integer shopId;

    @Column(name="al_dis")
    private Integer alDis;

    private Boolean flag;

}
