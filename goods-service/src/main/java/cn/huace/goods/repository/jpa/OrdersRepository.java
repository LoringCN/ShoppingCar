package cn.huace.goods.repository.jpa;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import cn.huace.common.repository.BaseRepository;
import cn.huace.goods.entity.Orders;
/**
 * 
 * @author Lin Huan
 * @date  2018年12月17日
 * @desc   描述
 * @version 1.0.0
 */
public interface OrdersRepository extends BaseRepository<Orders,Integer>{

	 @Modifying
	 @Transactional
	 @Query(value = "UPDATE orders set status=?2 where order_id =?1", nativeQuery = true)
	public int upOrderStatus(String orderId,int status);
	
}
