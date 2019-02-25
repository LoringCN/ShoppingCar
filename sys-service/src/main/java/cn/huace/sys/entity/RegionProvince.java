package cn.huace.sys.entity;


import cn.huace.common.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;


@Data
@Entity
@Table(name="region_province")
public class RegionProvince extends BaseEntity {

    @Column(name = "province_id")
    private Integer provinceId;

    @Column(name = "name")
    private String name;

    @Transient
    private List<RegionCity> cityList;

}