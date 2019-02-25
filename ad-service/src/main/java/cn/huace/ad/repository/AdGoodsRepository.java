package cn.huace.ad.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import cn.huace.ad.entity.AdGoods;
import cn.huace.common.repository.BaseRepository;

public interface AdGoodsRepository extends BaseRepository<AdGoods, Integer> {

	@Query("select adgoods from AdGoods adgoods where adId = ?1")
	List<AdGoods> findAdGoodsByAdId(Integer adId);
	@Query("select adgoods from AdGoods adgoods where shopId = ?1")
	List<AdGoods> findAdGoodsByShopId(Integer shopId);
	@Query("select adgoods from AdGoods adgoods where adId = ?1 and shopId = ?2")
	List<AdGoods> findAdGoodsByAdIdAndShopId(Integer adId,Integer shopId);
}
