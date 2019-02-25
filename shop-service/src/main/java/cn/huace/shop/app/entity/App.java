package cn.huace.shop.app.entity;


import cn.huace.common.entity.BaseEntity;
import cn.huace.shop.device.entity.DeviceGroup;
import cn.huace.shop.shop.entity.Shop;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by huangdan on 2017/5/2.
 */
@Data
@Entity
@Table(name = "app")
public class App extends BaseEntity {

    @Column(name = "version_code")
    private int versionCode;

    @Column(name = "package_name")
    private String packageName;

    private String name;

    @Column(name = "use_flag")
    private Boolean useFlag;

    @Column(name = "url")
    private String url;

    @Column(name = "md5",length = 32)
    private String md5;

    @Column(name="description",length = 1024)
    private String desc;

    @ManyToOne(optional = false)
    @JoinColumn(name="shop_id")
    private Shop shop;

    /**
     * app大类id
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "app_list_id")
    private AppList appList;

    /**
     * 投放范围
     * 默认全部投放：-1; 指定设备分组：1
     */
    @Column(name = "deliver_scope")
    private Integer deliverScope;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "app_device_group_rel",joinColumns = {@JoinColumn(name = "app_id")},inverseJoinColumns = {@JoinColumn(name = "group_id")})
    private Set<DeviceGroup> deviceGroupSet = new HashSet<DeviceGroup>();
}
