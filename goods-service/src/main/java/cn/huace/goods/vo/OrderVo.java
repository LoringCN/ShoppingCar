package cn.huace.goods.vo;

import java.util.List;

import lombok.Data;
import cn.huace.common.entity.IntBaseEntity;

@Data
public class OrderVo extends IntBaseEntity {
	private static final long serialVersionUID = -5615501050958713181L;
	/**
	 * 设备ID
	 */
	private String deviceId;
	/**
	 * 商店ID
	 */
	private Integer shopId;
	/**
	 * 订单ID
	 */
	private String orderId;
	/**
	 * 总价
	 */
	private Integer total;
	/**
	 * 商品详情
	 */
	private List<OrderGoodsInfoVo> orderGoodsInfoList;
	/**
	 * 订单状态 0未支付  1已支付  -1 订单异常  2订单关闭
	 */
	private Integer status=0;
	/**
	 * 付款时间
	 */
	private String payTime;
	/**
	 * 付款URL
	 */
	private String payUrl;
	/**
	 *订单详情URL 当status = 1的时候返回 
	 */
	private String detailUrl;
	/**
	 * 订单小票打印成功后回调地址
	 */
	private String callback;
	
	/**
	 * 是否实时请求 去微信支付获取最新订单状态
	 */
	private Boolean realReq = false;
	/**
	 * 第三方订单ID
	 */
	private String transactionId;
	/**
	 * 用户在商户appid下的唯一标识 
	 */
	private String openid;
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrderVo [deviceId=");
		builder.append(deviceId);
		builder.append(", shopId=");
		builder.append(shopId);
		builder.append(", orderId=");
		builder.append(orderId);
		builder.append(", total=");
		builder.append(total);
		builder.append(", orderGoodsInfoList=");
		builder.append(orderGoodsInfoList);
		builder.append(", status=");
		builder.append(status);
		builder.append(", payTime=");
		builder.append(payTime);
		builder.append(", payUrl=");
		builder.append(payUrl);
		builder.append(", detailUrl=");
		builder.append(detailUrl);
		builder.append(", callback=");
		builder.append(callback);
		builder.append(", realReq=");
		builder.append(realReq);
		builder.append(", transactionId=");
		builder.append(transactionId);
		builder.append(", openid=");
		builder.append(openid);
		builder.append("]");
		return builder.toString();
	}
	
	

}
