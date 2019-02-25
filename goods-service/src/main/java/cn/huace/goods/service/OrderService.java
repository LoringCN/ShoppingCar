package cn.huace.goods.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import cn.huace.common.config.WxConfigProPertiesConfig;
import cn.huace.common.service.BaseService;
import cn.huace.common.utils.DateUtils;
import cn.huace.goods.constant.PayConstants;
import cn.huace.goods.constant.PayConstants.TradeStatus;
import cn.huace.goods.entity.Goods;
import cn.huace.goods.entity.Orders;
import cn.huace.goods.enums.TempGoodsEnum;
import cn.huace.goods.enums.TempGoodsEnum.TempGoods;
import cn.huace.goods.repository.jpa.OrdersRepository;
import cn.huace.goods.vo.OrderGoodsInfoVo;
import cn.huace.goods.vo.OrderVo;
@Slf4j
@Service
public class OrderService extends BaseService<Orders, Integer> {
	
	@Autowired
	OrderGoodsInfoService orderGoodsInfoService;
	
	@Autowired
	RedisTemplate redisTemplate;
	
	@Autowired
	GoodsService goodsService;	
	
	@Autowired
	OrdersRepository ordersRepository;
	
	/**
	 * 创建订单
	 * @param orderVo
	 * @return
	 */
	@Transactional
	public OrderVo createOrder(OrderVo orderVo){
		try{
			//存储订单商品详情表
			List<OrderGoodsInfoVo> eList = orderVo.getOrderGoodsInfoList();
			if(CollectionUtils.isEmpty(eList)){
				return null;
			}
			
			/*生产订单id 规则 deviceId + HHmmssSSS*/
			buildOrderId(orderVo);
			Orders orders = voToPo(orderVo,new Orders());
			Integer total = 0;
			for(OrderGoodsInfoVo e:eList){
				e.setOrderId(orders.getOrderId());
				//校验商品
				String barCode = e.getBarCode();
				//判断是否为临时打称商品
				TempGoods tempGoods = null;
				if(TempGoodsEnum.isTempGoods(barCode)){
					tempGoods = TempGoodsEnum.getTempGoods(barCode);
					barCode = tempGoods.getCode();
				}
				Goods goods = goodsService.findGoodsByBarcodeForApp(barCode,orderVo.getShopId());
				if(null == goods){
					log.info("获取不到当前商品价格");
					return null;
				}
				//根据价格规则  获取当前价格
				Double currPrice = getCurrentPrice(goods);
				if(null == currPrice){
					log.info("获取不到当前商品价格"+goods.toString());
					return null;
				}
				//总价
				Integer tempPrice = null == tempGoods?currPrice.intValue()*e.getNum():tempGoods.getPrice().intValue()*e.getNum();
				
				//进行赋值
				e.setTitle(goods.getTitle());
				e.setPrice(null == tempGoods?currPrice.intValue():tempGoods.getPrice().intValue());
				e.setTotalPrice(tempPrice);
				e.setDetailImgUrl(goods.getDetailImgUrl());
				e.setGoodsId(goods.getId());
				e.setBarCode(e.getBarCode());
				//订单价格汇总
				total = total + tempPrice;
			}
			//订单总额
			orders.setTotal(total);
			//TODO  测试使用 此价格统一设置为1分钱
			orderVo.setTotal(total);
//			if(StringUtils.equalsIgnoreCase(WxConfigProPertiesConfig.getInstance().getDev(), "test")){
//				orders.setTotal(1);
//				orderVo.setTotal(1);
//			}
			super.save(orders);
			orderGoodsInfoService.addList(eList);
		}catch(Exception e){
			log.error(e.getMessage(),e);
			return null;
		}
		
		return orderVo;
	}
	/**
	 * 查询订单
	 * @param orderVo
	 * @return
	 */
	public  OrderVo  findOrderByOrderId(String orderId){
		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("EQ_orderId", orderId);
		Orders orders = super.findOne(searchParams);
		OrderVo orderVo = poToVo(orders,new OrderVo());
		orderVo.setOrderGoodsInfoList(orderGoodsInfoService.findByOrderId(orderId));
		return orderVo;
	}
	
	/**
	 * 更新订单状态
	 */
	@Transactional
	public  int  upOrdersStatus(String orderId,Integer status){
		int ret = ordersRepository.upOrderStatus(orderId, status);
		return ret;
	}
	
	
	/**
	 * 查询订单 没有订单详情
	 * @param orderId
	 * @return
	 */
	public OrderVo findOrderNoGoodsInfo(String orderId){
		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("EQ_orderId", orderId);
		Orders orders = super.findOne(searchParams);
		OrderVo orderVo = poToVo(orders,new OrderVo());
		return orderVo;
	}
	/**
	 * 分页查询订单
	 * @param orderVo
	 * @return
	 */
	public Page<OrderVo> findOrderListByPage(OrderVo orderVo,Integer pageNum,Integer pageSize){
		Map<String, Object> searchParams = new HashMap<String, Object>();
		if(!StringUtils.isEmpty(orderVo.getDeviceId())){
			searchParams.put("EQ_deviceId", orderVo.getDeviceId());
		}
		
		if(null != orderVo.getShopId() || orderVo.getShopId()!=0){
			searchParams.put("EQ_shopId", orderVo.getShopId());
		}
		//当前库中使用的是 0   1  和  7 两个有效标示为  如后续业务扩展需求 请修改pay-web中入库订单状态标示位
		searchParams.put("GT_status", TradeStatus.NOTPAY.getCode());
		Page<Orders> orderPage = super.findAll(searchParams, pageNum,pageSize,Sort.Direction.DESC,"createdTime");
		List<Orders> eList = orderPage.getContent();
		List<OrderVo> orderVoLlist = new ArrayList<OrderVo>(); 
		for(Orders e:eList){
			orderVo = new OrderVo();
			orderVo.setOrderGoodsInfoList(orderGoodsInfoService.findByOrderId(e.getOrderId()));	
			//支付成功的 返回小票详情链接
			if(e.getStatus().intValue() == TradeStatus.getTradeCode(TradeStatus.SUCCESS).intValue() || 
			   e.getStatus().intValue() == TradeStatus.getTradeCode(TradeStatus.TICKET).intValue() 	
				){
				String url = WxConfigProPertiesConfig.getInstance().getOrderInfoUrl()+"?orderId="+e.getOrderId();
	    		orderVo.setDetailUrl(Base64Utils.encodeToString(url.getBytes()));
			}
			orderVoLlist.add(poToVo(e, orderVo));
		}
		PageRequest pageRequest = new PageRequest(pageNum, pageSize);
		
		Page<OrderVo> retPage = new PageImpl<OrderVo>(orderVoLlist, pageRequest, orderPage.getTotalElements());
		
		return retPage;
	}
	
	
	/**
	 * 保存更新操作
	 * @param orderVo
	 */
	public void save(OrderVo orderVo){
		Orders order = voToPo(orderVo, new Orders());
		super.save(order);
	}
	
	/**
	 * 生成订单ID  
	 * 生成规则 时间+redis全局唯一ID
	 * @param orderVo
	 */
	private void buildOrderId(OrderVo orderVo){
		/**
		 * 模拟生产天虹的订单号 q001234567890   暂时按时间戳解决
		 */
//		String orderId = "q00"+(System.currentTimeMillis()-1540000000000L);
//		
//		orderVo.setOrderId(orderId);
		
		RedisAtomicLong redisAtomicLong = new RedisAtomicLong(PayConstants.REDIS_ORDER_KEY, redisTemplate.getConnectionFactory());		
		
		Long increment = redisAtomicLong.getAndIncrement();
		
		if(( null == increment || increment ==0 ) ){
			redisAtomicLong.expire(100, TimeUnit.SECONDS);
		}
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(DateUtils.getToday("yyyyMMddHHmmssSSS")).append(increment);
		
		orderVo.setOrderId(sb.toString());
	}
	
	 /**
     * 对象拷贝 VO TO PO
     * @param source
     * @param target
     * @return
     */
	private  Orders voToPo (OrderVo source,Orders target){
		BeanUtils.copyProperties(source,target);
		if(null != source.getId()){
            target.setId(source.getId());
        }
		return target;
	}
	/**
     * 对象拷贝 VO TO PO
     * @param source
     * @param target
     * @return
     */
	private  OrderVo poToVo (Orders source,OrderVo target){
		BeanUtils.copyProperties(source,target);
		if(null != source.getId()){
            target.setId(source.getId());
        }
		return target;
	}
	
	private Double getCurrentPrice(Goods goods){
		//开始促销时间
		Long dateCurr = System.currentTimeMillis();
		Date dateStart = goods.getPromotionStartDate();  
		Date dateEnd =  goods.getPromotionEndDate();
		//新版天虹数据格式   ------------------------
		//暂不考虑会员价格
		//开始促销时间非空 且当前时间在促销时间内 促销价小于标准价
		if(null!= dateStart &&
		   null!=dateEnd && 
		   null!=goods.getPromotionalSalePrice() &&
		   null!=goods.getPrice() && 
		   dateStart.getTime()<= dateCurr && 
		   dateCurr<= dateEnd.getTime()&&
		   goods.getPromotionalSalePrice()<goods.getPrice()&&
		   goods.getPromotionalSalePrice() >=0){
		   return goods.getPromotionalSalePrice();
		
		}
		//促销时间为空 或者不在促销时间  但price非空  仍然是天虹数据格式
		if(null != goods.getPrice() && goods.getPrice()>=0){
			return goods.getPrice();
		}
		//天虹数据结束-------------------------------
		
		//兼容旧版本数据
		if(null != goods.getPromotionPrice() && goods.getPromotionPrice()>=0){
			return goods.getPromotionPrice().doubleValue();
		}
		
		if(null != goods.getNormalPrice() && goods.getNormalPrice()>=0){
			return goods.getNormalPrice().doubleValue();
		}
		log.error("商品价格异常");
		return null;
	}
}
