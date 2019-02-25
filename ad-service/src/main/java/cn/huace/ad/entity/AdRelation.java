package cn.huace.ad.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.huace.common.entity.BaseEntity;
import lombok.Data;

/**
 * 关联关系实体
 * @author zhouyanbin
 */
@Data
@Entity
@Table(name="ad_relation")
public class AdRelation extends BaseEntity {
	private static final long serialVersionUID = -1L;
	/** 名称 */
	@Column(name = "name")
	private String name;
	/** 是否有效  0-无效 , 1-有效*/
	@Column(name = "valid_flag")
	private String validFlag;
}
