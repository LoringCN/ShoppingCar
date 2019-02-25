package cn.huace.controller;

import io.swagger.annotations.Api;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.annotations.ApiIgnore;
import cn.huace.common.bean.HttpResult;
import cn.huace.common.config.WxConfigProPertiesConfig;
import cn.huace.goods.constant.PayConstants.TradeStatus;
import cn.huace.goods.service.OrderService;
import cn.huace.goods.service.PayService;
import cn.huace.goods.util.PayUtils;
import cn.huace.goods.vo.OrderVo;
/**
 * 订单-支付管理
 * @author Lin Huan
 * @date  2018年12月10日
 * @desc   描述
 * @version 1.0.0
 */
@Slf4j
@RestController
@Api(value = "/order",description = "订单-支付管理")
@RequestMapping("/order")
public class OrderController {
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	PayService payService;
	
	@Value("${pay.ticket.callback}")
	private String callback;
	
	
	/**
	 * 通过orderId查询订单
	 * @param request
	 * @param orderVo
	 * @return
	 */
	@RequestMapping(value = "get")
	public 	HttpResult findByOrderId(OrderVo orderVo){
		
	    String orderId = orderVo.getOrderId();
	    
	    if(!StringUtils.isEmpty(orderId)){
	    	//进行数据库查询	
	    	orderVo = orderService.findOrderByOrderId(orderId);
	    	
	    	String url = callback+"?orderId="+orderId;
	    	
	    	orderVo.setCallback(Base64Utils.encodeToString(url.getBytes()));
	    	//如果小票已打印 不返回详情
	    	if(TradeStatus.getTradeCode(TradeStatus.TICKET).intValue() == orderVo.getStatus().intValue()){
	    		return HttpResult.createSuccess("此小票已打印，如需再次打印请联系管理人员");
	    	}
	    
	    	return HttpResult.createSuccess("订单查询成功",orderVo);
	    	
	    }else{
	    	return HttpResult.createFAIL("订单orderId不能为空");
	    }
	}
	/**
	 * 更新订单详情为打印小票
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value = "upTicket")
	public 	HttpResult updateTicketStatus(String orderId){
	    
	    if(!StringUtils.isEmpty(orderId)){
	    	//进行数据库查询	
	    	int ret = orderService.upOrdersStatus(orderId, TradeStatus.TICKET.getCode());
	    
	    	return HttpResult.createSuccess("更新成功");
	    	
	    }else{
	    	return HttpResult.createFAIL("订单orderId不能为空");
	    }
	}
	
	
	/**
	 * 微信订单回调地址
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ApiIgnore
	@RequestMapping(value="notify")
	public String notify(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		String resXml = null;
		
		//读取参数  
        InputStream inputStream ;  
        StringBuffer sb = new StringBuffer();  
        inputStream = request.getInputStream();  
        String s ;  
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));  
        while ((s = in.readLine()) != null){  
            sb.append(s);  
        }  
        in.close();  
        inputStream.close();  
        log.info("微信支付回调信息:"+sb.toString());
        Map<String, String> xmlMap = PayUtils.xmlToMap(sb.toString());
        //验证签名是否正确
        if(PayUtils.isSignatureValid(xmlMap, WxConfigProPertiesConfig.getInstance().getKey())){
        	//获取相关参数 进行判断
        	// 去微信再次查询此订单
        	String orderId = xmlMap.get("out_trade_no");
        	OrderVo orderVo = new OrderVo();
        	orderVo.setOrderId(orderId);
        	payService.getOrderStatus(orderVo);
        	
        	
        /*	String result_code = xmlMap.get("result_code");
        	if(StringUtils.equalsIgnoreCase(result_code, "SUCCESS")){
        		String appid = xmlMap.get("appid");
            	String mch_id = xmlMap.get("mch_id");
            	String openid = xmlMap.get("openid");
            	String orderId = xmlMap.get("out_trade_no");
            	String transactionId = xmlMap.get("transaction_id");
            	String caseFee = xmlMap.get("cash_fee");
            	String payTime = xmlMap.get("time_end");
            	
    		 	OrderVo order = orderService.findOrderNoGoodsInfo(orderId);
    			//由于没有优惠卷等等  付款现金交易金额即为订单中需付款金额
    			if( null!= order && order.getTotal() == Integer.valueOf(caseFee) && order.getStatus().intValue() != TradeStatus.getTradeCode(TradeStatus.SUCCESS) ){
    				order.setPayTime(payTime);
    				order.setStatus(TradeStatus.getTradeCode(TradeStatus.SUCCESS));
    				order.setTransactionId(transactionId);
    				//更新库中订单数据
    				orderService.save(order);
    			}
        	}*/
        	resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"  
                    + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";  
        }else{
        	resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"  
                    + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
        }
        
        BufferedOutputStream out = new BufferedOutputStream(  
                response.getOutputStream());  
        out.write(resXml.getBytes());  
        out.flush();  
        out.close();  
		return resXml;
	}
	public static void main(String[] args) {
		System.out.println(new String(Base64Utils.decode("aHR0cDovLzQ3Ljk3LjEwNC4xMTAvcGF5L29yZGVyL3VwVGlja2V0P29yZGVySWQ9cTAwNTAyODM1NDkzNQ==".getBytes())));
	}
}	
