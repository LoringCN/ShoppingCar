package cn.huace.goods.service;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.huace.common.config.WxConfigProPertiesConfig;
import cn.huace.goods.constant.PayConstants;
import cn.huace.goods.constant.PayConstants.TradeStatus;
import cn.huace.goods.util.PayUtils;
import cn.huace.goods.vo.OrderVo;
/**
 * 微信支付管理
 * @author Lin Huan
 * @date  2018年12月17日
 * @desc   描述
 * @version 1.0.0
 */
@Slf4j
@Service
public class PayService {
	
	@Autowired
	OrderService orderService;
	/**
	 * 统一下单API
	 * @param orderVo
	 * @return
	 */
	public String pay(OrderVo orderVo){

		String appid = WxConfigProPertiesConfig.getInstance().getAppId();

		String mch_id = WxConfigProPertiesConfig.getInstance().getMchId();

		String nonce_str = PayUtils.generateNonceStr();

		String body = "天虹商品扫码支付-商品付款";

		String out_trade_no = orderVo.getOrderId();

		String total_fee = orderVo.getTotal().toString();

		String spbill_create_ip = WxConfigProPertiesConfig.getInstance().getSpbillCreateIp();

		String notify_url = WxConfigProPertiesConfig.getInstance().getNotifyUrl();

		String trade_type = PayConstants.TRADE_TYPE;

		String product_id = orderVo.getOrderId();

		Map<String, String> data = new HashMap<String, String>();

		data.put("appid", appid);
		data.put("mch_id", mch_id);
		data.put("nonce_str", nonce_str);
		data.put("body", body);
		data.put("out_trade_no", out_trade_no);
		data.put("total_fee", total_fee);
		data.put("spbill_create_ip", spbill_create_ip);
		data.put("notify_url", notify_url);
		data.put("trade_type", trade_type);
		data.put("product_id", product_id);   

		try {
			String requestXML = PayUtils.generateSignedXml(data, WxConfigProPertiesConfig.getInstance().getKey());

			String resXml = PayUtils.requestOnce(PayConstants.WX_PAY_ORDER,requestXML);
			
			Map<String, String> resMap = PayUtils.xmlToMap(resXml);
			
			if("SUCCESS".equalsIgnoreCase(resMap.get("result_code"))){
				return resMap.get("code_url");
			}
			
			return "";
			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return "";
		}

	}
	/**
	 * 查询订单状态
	 * @param orderVo
	 * @return
	 */
	public String getOrderStatus(OrderVo orderVo){
		
		String appid = WxConfigProPertiesConfig.getInstance().getAppId();

		String mch_id = WxConfigProPertiesConfig.getInstance().getMchId();

		String nonce_str = PayUtils.generateNonceStr();//随机字符串
		
		String out_trade_no = orderVo.getOrderId();
		
		Map<String, String> data = new HashMap<String, String>();

		data.put("appid", appid);
		data.put("mch_id", mch_id);
		data.put("nonce_str", nonce_str);
		data.put("out_trade_no", out_trade_no);
		
		try {
			
			String requestXML = PayUtils.generateSignedXml(data, WxConfigProPertiesConfig.getInstance().getKey());
			
			String resXml = PayUtils.requestOnce(PayConstants.WX_PAY_SEARCH,requestXML);
			//再次校验签名
			if(!PayUtils.isSignatureValid(resXml, WxConfigProPertiesConfig.getInstance().getKey())){
				//校验失败
				return "FAIL";
			}
			
			Map<String, String> resMap = PayUtils.xmlToMap(resXml);
			//请求成功 return_code=success  业务结果  result_code = success
			if("SUCCESS".equalsIgnoreCase(resMap.get("return_code")) && "SUCCESS".equalsIgnoreCase(resMap.get("result_code"))){
				//订单交易状态
				String status = resMap.get("trade_state");
				// 判断trade_state为支付成功状态 去修改数据库
				if(StringUtils.equalsIgnoreCase(TradeStatus.SUCCESS.toString(), status)){
					//订单号
					String orderId = resMap.get("out_trade_no");
					//现金交易金额
					String caseFee = resMap.get("cash_fee");
					//支付时间
					String payTime = resMap.get("time_end");
					
				  	String transactionId = resMap.get("transaction_id");
				  	
				  	String openid = resMap.get("openid");
					
				  	OrderVo order = orderService.findOrderNoGoodsInfo(orderId);
					//由于没有优惠卷等等  付款现金交易金额即为订单中需付款金额
					if(order.getTotal().intValue() == Integer.valueOf(caseFee).intValue()){
						order.setPayTime(payTime);
						order.setStatus(TradeStatus.getTradeCode(status));
						order.setTransactionId(transactionId);
						order.setOpenid(openid);
						//更新库中订单数据
						orderService.save(order);
					}else{
						log.error("支付金额错误:"+order.toString()+"\n"+resXml);
						return "FAIL";
					}
					return "SUCCESS";
				}
			}
			log.error("微信支付返回订单查询结果："+resXml);
			return "FAIL";
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return "FAIL";
	}
}
