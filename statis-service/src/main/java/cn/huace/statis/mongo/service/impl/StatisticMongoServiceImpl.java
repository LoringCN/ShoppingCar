package cn.huace.statis.mongo.service.impl;

import cn.huace.common.utils.DateUtils;
import cn.huace.statis.mongo.entity.StatisticMongoEntity;
import cn.huace.statis.mongo.service.StatisticMongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author Lin Huan
 * @version 1.0.0
 * @date 2018年9月11日
 * @desc 描述
 */
@Slf4j
@Service
public class StatisticMongoServiceImpl implements StatisticMongoService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<StatisticMongoEntity> findList(Integer shopId, String type, String date) {

        Query query = new Query();
        //商店shopId type date均不为空 走索引查询
        if (null != shopId && !StringUtils.isEmpty(type) && !StringUtils.isEmpty(date)) {
            if (date.contains("-")) {
                date = date.replace("-", "");
            }
            StringBuilder sb = new StringBuilder();
            //索引字段 shopId_type_date
            sb.append(shopId).append("_").append(type).append("_").append(date);

            Criteria criteria = Criteria.where("_id").is(sb.toString());

            query.addCriteria(criteria);
            List<StatisticMongoEntity> eList = mongoTemplate.find(query, StatisticMongoEntity.class);
            return eList;
        } else {//多条件查询
            Criteria criteria = null;
            if (null != shopId) {
                criteria = Criteria.where("shopId").is(shopId);
            }
            if (!StringUtils.isEmpty(type)) {
                criteria.andOperator(Criteria.where("type").is(type));
            }
            if (!StringUtils.isEmpty(date)) {
                date = date.replace("-", "");
                if (criteria == null) {
                    criteria = Criteria.where("date").is(date);
                } else {
                    criteria.andOperator(Criteria.where("date").is(date));
                }
            }
            query.addCriteria(criteria);
            List<StatisticMongoEntity> eList = mongoTemplate.find(query, StatisticMongoEntity.class);
            return eList;
        }
    }

    @Override
    public void save(StatisticMongoEntity statisticMongoEntity) {

        Integer shopId = statisticMongoEntity.getShopId();

        if (StringUtils.isEmpty(shopId)) {
            log.info("mongo结果存储 ：商店shopId不能为空");
            return;
        }

        String type = statisticMongoEntity.getType();

        if (StringUtils.isEmpty(type)) {
            log.info("mongo结果存储 ：统计类型type不能为空");
            return;
        }

        String date = statisticMongoEntity.getDate();

        if (StringUtils.isEmpty(date)) {
            log.info("mongo结果存储 ：统计类型date为空");
            //默认昨天的日期
            date = DateUtils.getYesterday(DateUtils.DATE_STRING_FORMAT);
        }

        StringBuilder sb = new StringBuilder();
        //默认id索引  故设置索引字段 shopId_type_date
        sb.append(shopId).append("_").append(type).append("_").append(date);

        statisticMongoEntity.setId(sb.toString());

        mongoTemplate.save(statisticMongoEntity);
    }
}
