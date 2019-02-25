package cn.huace.ad.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.huace.ad.entity.AdGoods;
import cn.huace.ad.repository.AdGoodsRepository;
import cn.huace.common.service.BaseService;

@Service
public class AdGoodsService extends BaseService<AdGoods, Integer> {
	@Autowired
	private AdGoodsRepository adGoodsRepository;
	
	public AdGoods findAdGoodsByAdId(Integer adId){
		List<AdGoods> adGoodsList = adGoodsRepository.findAdGoodsByAdId(adId);
		if(adGoodsList == null || adGoodsList.isEmpty()){
			return null;
		}
		return adGoodsList.get(0);
	}
	
	public List<AdGoods> findAdGoodsByShopId(Integer shopId){
		List<AdGoods> adGoodsList = adGoodsRepository.findAdGoodsByShopId(shopId);
		return adGoodsList;
	}
	
	public List<AdGoods> findAdGoodsByAdIdAndShopId(Integer adId,Integer shopId){
		List<AdGoods> adGoodsList = adGoodsRepository.findAdGoodsByAdIdAndShopId(adId,shopId);
		return adGoodsList;
	}
	
}
