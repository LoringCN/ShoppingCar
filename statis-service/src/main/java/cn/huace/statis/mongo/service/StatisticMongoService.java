package cn.huace.statis.mongo.service;

import java.util.List;

import cn.huace.statis.mongo.entity.StatisticMongoEntity;
/**
 * 
 * @author Lin Huan
 * @date  2018年9月11日
 * @desc   描述
 * @version 1.0.0
 */
public interface StatisticMongoService {
	
	public List<StatisticMongoEntity> findList(Integer shopId,String type,String date);
	
	public void save(StatisticMongoEntity statisticMongoEntity);
	
}
