package cn.huace.ad.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.huace.common.entity.BaseEntity;
import lombok.Data;

@Data
@Entity
@Table(name="ad_goods")
public class AdGoods extends BaseEntity {
	private static final long serialVersionUID = -1L;
	/** 广告id*/
	@Column(name = "ad_id")
	private Integer adId;
	/** 超市id*/
	@Column(name = "shop_id")
	private Integer shopId;
	/** 广告id*/
	@Column(name = "goods_id")
	private Integer goodsId;
	/** 是否有效  0-无效 , 1-有效*/
	@Column(name = "valid_flag")
	private String validFlag;
}
