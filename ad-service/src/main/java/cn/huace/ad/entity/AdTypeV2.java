package cn.huace.ad.entity;

import cn.huace.common.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 广告类型表
 * Date:2018/1/22
 */
@Data
@Entity
@Table(name = "ad_type_v2")
public class AdTypeV2 extends BaseEntity{
    private static final long serialVersionUID = 4258734585646076761L;
    /**
     * 广告类型名称，如：主页轮播广告
     */
    @Column(name = "name",length = 64)
    private String name;
    /**
     * 约定的广告标识码
     */
    @Column(name = "code")
    private Integer code;

    @Column(columnDefinition = "bit default true")
    private Boolean flag;
}
