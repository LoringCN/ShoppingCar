package cn.huace.shop.device.entity;


import cn.huace.common.entity.BaseEntity;
import cn.huace.shop.shop.entity.Shop;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

/**
 * <门店>
 *
 * @author huangdan
 */
@Data
@Entity
@Table(name = "device")
public class Device extends BaseEntity {

    @Column(name = "name")
    private String name;
    //设备Id
    @Column(name = "dev_id", unique = true)
    private String devId;

    //软件初始版本
    @Column(name = "initial_version")
    private String initialVersion;

    //软件现在版本
    @Column(name = "current_version")
    private String currentVersion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    /*指南针数据*/
    @Column(name = "compass")
    private String compass;


    @Column(name = "daemon_version")
    private String daemonVersion;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}