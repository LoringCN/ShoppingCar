package cn.huace.shop.device.entity;

import cn.huace.common.entity.IntBaseEntity;
import cn.huace.shop.app.entity.App;
import cn.huace.shop.app.entity.AppList;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 设备分组实体类
 *
 * created by Loring on 2018-06-21
 */
@Data
@Entity
@Table(name = "applist_device_group")
public class DeviceGroup extends IntBaseEntity {

    @Column(name = "shop_id")
    private Integer shopId;

    /**
     * 设备组名称
     */
    private String name;

    /**
     * app大类id
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "app_list_id")
    private AppList appList;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "device_group_rel",joinColumns = {@JoinColumn(name = "group_id")
    },inverseJoinColumns = {@JoinColumn(name = "dev_id")}
    )
    private Set<Device> deviceSet = new HashSet<Device>();

}
