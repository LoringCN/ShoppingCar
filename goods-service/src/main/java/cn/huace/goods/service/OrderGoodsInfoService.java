package cn.huace.goods.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import cn.huace.common.service.BaseService;
import cn.huace.goods.entity.OrderGoodsInfo;
import cn.huace.goods.vo.OrderGoodsInfoVo;
@Slf4j
@Service
public class OrderGoodsInfoService extends BaseService<OrderGoodsInfo, Integer>{
	
	
	public int addList(List<OrderGoodsInfoVo> eList){
		List<OrderGoodsInfo> orderGoodsInfoList = eList.stream().map(orderGoodsInfo->voToPo(orderGoodsInfo, new OrderGoodsInfo())).collect(Collectors.toList());
		int retsult = super.batchInsert(orderGoodsInfoList);
		return retsult;
	}
	/**
	 * 通过订单ID查询订单详情
	 * @param OrderId
	 * @return
	 */
	public List<OrderGoodsInfoVo> findByOrderId(String orderId){
		Map<String, Object> searchParams = new HashMap<String, Object>();
		searchParams.put("EQ_orderId", orderId);
		List<OrderGoodsInfo> eList = super.findAll(searchParams);
		
		if(!CollectionUtils.isEmpty(eList)){
			return eList.stream().map(orderGoodsInfo->poToVo(orderGoodsInfo, new OrderGoodsInfoVo())).collect(Collectors.toList());
		}
		return null;
	}
	
	 /**
     * 对象拷贝 VO TO PO
     * @param source
     * @param target
     * @return
     */
	private  OrderGoodsInfo voToPo (OrderGoodsInfoVo source,OrderGoodsInfo target){
		BeanUtils.copyProperties(source,target);
		if(null != source.getId()){
            target.setId(source.getId());
        }
		return target;
	}
	/**
     * 对象拷贝 PO TO VO
     * @param source
     * @param target
     * @return
     */
	private  OrderGoodsInfoVo poToVo (OrderGoodsInfo source,OrderGoodsInfoVo target){
		BeanUtils.copyProperties(source,target);
		if(null != source.getId()){
            target.setId(source.getId());
        }
		return target;
	}
}
