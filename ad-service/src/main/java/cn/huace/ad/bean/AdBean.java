package cn.huace.ad.bean;

import java.io.Serializable;

import lombok.Data;

/** 
 * @author zhouyanbin 
 * @date 2017年6月1日 上午10:06:48 
 * @version 1.0 
*/
@Data
public class AdBean implements Serializable{
	private static final long serialVersionUID = -1L;
	private Integer id;
	private String type;
	private String url;
	private String productId;
	private String md5;
	private Integer promotionPrice;
	private Integer normalPrice;
	private String title;
	private String sid;
}
