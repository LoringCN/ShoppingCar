package cn.huace.controller.order;

import io.swagger.annotations.Api;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.huace.common.bean.HttpResult;
import cn.huace.common.config.WxConfigProPertiesConfig;
import cn.huace.controller.base.BaseFrontController;
import cn.huace.goods.constant.PayConstants.TradeStatus;
import cn.huace.goods.service.OrderService;
import cn.huace.goods.service.PayService;
import cn.huace.goods.vo.OrderVo;

import com.alibaba.fastjson.JSONObject;
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
public class OrderController extends BaseFrontController{
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	PayService payService;
	
	/**
	 * 创建订单  生成微信支付链接
	 * @param request
	 * @param shopId
	 * @param orderVo
	 * @return
	 */
	@RequestMapping(value = "submit" , method = RequestMethod.POST)
	public HttpResult Order(HttpServletRequest request,String orders) {
		
		log.info("submit "+orders);
		
		OrderVo orderVo = null;
		   
	    if(!StringUtils.isEmpty(orders)){
	    	orderVo = JSONObject.parseObject(orders, OrderVo.class);	    	
	    }
		
		Integer shopId = findShopId(request);
		
		String deviceId = findDevId(request);
		
	    if(shopId == null || StringUtils.isEmpty(deviceId)){
	    	
	         return HttpResult.createFAIL("非法接口访问！");
	    }  
	    orderVo.setShopId(shopId);
	    orderVo.setDeviceId(deviceId);
	    
	    if(StringUtils.isEmpty(orderVo.getOrderId())){//创建订单
	    	orderVo =  orderService.createOrder(orderVo);
	    	
	    }else{//进行数据库查询，确认库中订单,防止订单被修改
	    	OrderVo order = orderService.findOrderNoGoodsInfo(orderVo.getOrderId());
	    	
	    	//进行校验  付款金额  商店id  设备id  订单支付状态
	    	if( null == order ||
	    	  shopId.intValue() != order.getShopId().intValue() ||
	    	  !StringUtils.equalsIgnoreCase(deviceId, order.getDeviceId()) ||
	    	  order.getStatus().intValue() != TradeStatus.getTradeCode(TradeStatus.NOTPAY).intValue()
	    	  ){
	    		return HttpResult.createFAIL("创建订单失败-0,金额错误：订单应付金额："+order.getTotal());
	    	}
	    	orderVo = order;
	    }
		
		if(orderVo == null){
			return HttpResult.createFAIL("创建订单失败-1");
		}
		
		//调用微信支付 返回支付链接
		String payUrl =  payService.pay(orderVo);
	
		if(StringUtils.isEmpty(payUrl)){
			
			return HttpResult.createFAIL("创建订单失败-2");
		}
		
		orderVo.setPayUrl(Base64Utils.encodeToString(payUrl.getBytes()));
	    
		return HttpResult.createSuccess("创建订单成功",orderVo);
		
	}
	/**
	 * 通过orderId查询订单
	 * @param request
	 * @param orderVo
	 * @return
	 */
	@RequestMapping(value = "findByOrderId")
	public 	HttpResult findByOrderId(HttpServletRequest request,OrderVo orderVo){
		
		Integer shopId = findShopId(request);
		
	    if(shopId == null){
	         return HttpResult.createFAIL("非法接口访问！");
	    }  
	    orderVo.setShopId(shopId);
	    
	    String orderId = orderVo.getOrderId();
	    
	    if(!StringUtils.isEmpty(orderId)){
	    	//是否是实时请求是的话  去微信支付同步结果   经讨论 不进行此操作
	    	/*if(orderVo.getRealReq())
	    	{
	    		payService.getOrderStatus(orderVo);
	    	}*/
	    	//进行数据库查询	
	    	orderVo = orderService.findOrderByOrderId(orderId);
	    	
	    	if(null != orderVo && orderVo.getStatus().intValue() == TradeStatus.SUCCESS.getCode().intValue()){
	    	//订单支付成功 设置订单详情URL  供客户端生产二维码  打印机打印小票	
	    		String url = WxConfigProPertiesConfig.getInstance().getOrderInfoUrl()+"?orderId="+orderId;
	    		
	    		orderVo.setDetailUrl(Base64Utils.encodeToString(url.getBytes()));
	    	}
	    
	    	return HttpResult.createSuccess("订单查询成功",orderVo); 
	    	
	    }else{
	    	return HttpResult.createFAIL("订单orderId不能为空");
	    }
	}
	
	/**
	 * 通过设备号 或者 商店id查询订单列表
	 * @param request
	 * @param deviceId
	 * @param shopId
	 * @return
	 */
	@RequestMapping(value = "findListByPage")
	public HttpResult findOrderListByDeviceId(HttpServletRequest request,OrderVo orderVo,
			@RequestParam(defaultValue = "1",name = "pageNum")Integer pageNum,
            @RequestParam(defaultValue = "20",name = "pageSize")Integer pageSize){
		Integer shopId = findShopId(request);
		
		String deviceId = findDevId(request);
		
	    if(shopId == null || StringUtils.isEmpty(deviceId)){
	    	
	         return HttpResult.createFAIL("非法接口访问！");
	    }  
	    orderVo.setShopId(shopId);
	    orderVo.setDeviceId(deviceId);
	    
	    Page<OrderVo> page =  orderService.findOrderListByPage(orderVo, pageNum, pageSize);
	    
	    return HttpResult.createSuccess("查询成功",page);
	}
}
