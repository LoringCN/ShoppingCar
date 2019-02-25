package cn.huace.sys.entity;


import cn.huace.common.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Data
@Entity
@Table(name="region_area")
public class RegionArea extends BaseEntity {

    @Column(name = "area_id")
    private Integer areaId;

    @Column(name = "name")
	private String name;

    @Column(name = "city_id")
    private Integer cityId;


}