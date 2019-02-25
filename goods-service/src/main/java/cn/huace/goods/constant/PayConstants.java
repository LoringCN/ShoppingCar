package cn.huace.goods.constant;

import org.apache.commons.lang3.StringUtils;

public class PayConstants {
	public enum SignType {
        MD5, HMACSHA256
    }
	/**
	 * 微信支付订单状态
	 * @author Lin Huan
	 * @date  2018年12月11日
	 * @desc   描述
	 * @version 1.0.0
	 */
	public enum TradeStatus{
		
		NOTPAY(0),//未支付
		SUCCESS(1),//支付成功 
		CLOSED(2),//已关闭 
		REFUND(3),//转入退款
		REVOKED(4),//已撤销（付款码支付）
		USERPAYING(5),//用户支付中（付款码支付） 
		PAYERROR(6),//支付失败(其他原因，如银行返回失败)
		TICKET(7),//小票已打印
		;
		private Integer code;
		
		TradeStatus(Integer code) {
			this.code = code;
		}
		
		public static  Integer getTradeCode(TradeStatus ts){
			
			TradeStatus[] tradeStatu = TradeStatus.values();
			
			for(TradeStatus t:tradeStatu){
				if(StringUtils.endsWithIgnoreCase(ts.toString(), t.toString())){
					return t.getCode();
				}
			}
			return -1;
		}
		
		public static  Integer getTradeCode(String key){
			
			TradeStatus[] tradeStatu = TradeStatus.values();
			
			for(TradeStatus t:tradeStatu){
				if(StringUtils.endsWithIgnoreCase(key, t.toString())){
					return t.getCode();
				}
			}
			return -1;
		}
		
		public Integer getCode() {
			return code;
		}
		public void setCode(Integer code) {
			this.code = code;
		}
		
		
	}
	/**
	 * 微信统一下单API
	 */
	public static String WX_PAY_ORDER  = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	/**
	 * 查询订单
	 */
	public static String WX_PAY_SEARCH = "https://api.mch.weixin.qq.com/pay/orderquery";
	/**
	 * redis订单生产唯一key
	 */
	public static String REDIS_ORDER_KEY = "ORDER_KEY";
	
	public static final String FIELD_SIGN = "sign";
	
	public static final String TRADE_TYPE = "NATIVE";
	
}
