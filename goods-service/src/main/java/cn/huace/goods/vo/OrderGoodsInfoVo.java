package cn.huace.goods.vo;

import lombok.Data;
import cn.huace.common.entity.IntBaseEntity;

@Data
public class OrderGoodsInfoVo extends IntBaseEntity
{
	  private static final long serialVersionUID = -6096028693556251171L;
	  
	  private String barCode;
	  //订单ID
	  private String orderId;
	  //商品ID
	  private Integer goodsId;
	  //商品标记
	  private String title;
	  //商品图片地址
	  private String detailImgUrl;
	  //商品单价
	  private Integer price;
	  //商品数量
	  private Integer num;
	  //商品总价
	  private Integer totalPrice;
}
