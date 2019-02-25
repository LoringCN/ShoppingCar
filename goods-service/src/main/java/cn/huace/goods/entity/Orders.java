package cn.huace.goods.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import cn.huace.common.entity.IntBaseEntity;
@Data
@Entity
@Table(name="orders")
public class Orders extends IntBaseEntity{
	private static final long serialVersionUID = -5615501050958713181L;
	/**
	 * 平板设备ID
	 */
	@Column(name="device_id")
	private String deviceId;
	/**
	 * 商店ID
	 */
	@Column(name="shop_id")
	private Integer shopId;
	/**
	 * 订单ID
	 */
	@Column(name="order_id")
	private String orderId;
	/**
	 * 订单金额
	 */
	@Column(name="total")
	private Integer total;
	/**
	 * 订单状态  0 未支付  1支付成功  2 订单关闭
	 */
	@Column(name="status")
	private Integer status;
	/**
	 * 付款时间
	 */
	@Column(name="pay_time")
	private String payTime;
	/**
	 * 第三方支付订单流水号
	 */
	@Column(name="transaction_id")
	private String transactionId;
	/**
	 * 用户在商户appid下的唯一标识 
	 */
	@Column(name="openid")
	private String openid;
}
