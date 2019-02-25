package cn.huace.shop.app.entity;

import cn.huace.common.entity.IntBaseEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Loring on 2017/7/24
 */
@Data
@Entity
@Table(name = "app_list")
public class AppList extends IntBaseEntity {
    /**
     * 商店Id
     */
    private Integer shopId;
    /**
     * app大类名称
     */
    private String name;
    /**
     * app包名
     */
    private String packageName;
}
