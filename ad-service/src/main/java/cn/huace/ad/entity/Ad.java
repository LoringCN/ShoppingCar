package cn.huace.ad.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cn.huace.common.entity.BaseEntity;
import lombok.Data;

/**
 * 广告实体类
 * @author zhouyanbin
 */
@Data
@Entity
@Table(name="ad")
public class Ad extends BaseEntity{
	private static final long serialVersionUID = -1L;
	/** 广告名称 */
	@Column(name = "name")
	private String name;
	/** 广告类型 */
	@Column(name = "type")
	private String type;
	/** 广告路径*/
	@Column(name = "path")
	private String path;
	/** 备注 */
	@Column(name = "remark")
	private String remark;
	/** 所属超市 */
	@Column(name = "shop_id")
	private Integer shopId;
	/** 是否有效  0-无效 , 1-有效*/
	@Column(name = "valid_flag")
	private String validFlag;
	/** 关联关系*/
	@Column(name = "relation")
	private String relation;
	/** 图片上传的md5值 */
	@Column(name = "md5")
	private String md5;
	/** 上架时间 */
	@Column(name = "online_time")
	private Date onlineTime;
	/** 下架时间*/
	@Column(name = "offline_time")
	private Date offlineTime;
}
