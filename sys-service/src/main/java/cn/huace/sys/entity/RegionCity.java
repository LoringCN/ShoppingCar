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
@Table(name="region_city")
public class RegionCity extends BaseEntity {

	@Column(name = "city_id")
	private Integer cityId;

	@Column(name = "name")
	private String name;

	@Column(name = "province_id")
	private Integer provinceId;

	@Transient
	private List<RegionArea> areaList;

}