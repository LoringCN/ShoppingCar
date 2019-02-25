package cn.huace.ad.entity;

import cn.huace.common.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 广告关联关系表
 * Date:2018/1/22
 */
@Data
@Entity
@Table(name = "ad_relation_v2")
public class AdRelationV2 extends BaseEntity{
    private static final long serialVersionUID = -5971584942142546327L;

    /**
     * 关联关系名称，如：无、商品、链接等
     */
    @Column(name = "name",length = 32)
    private String name;

    /**
     * 约定的关联关系标志码
     */
    @Column(name = "code")
    private Integer code;

    @Column(columnDefinition = "bit default true")
    private Boolean flag;
}
