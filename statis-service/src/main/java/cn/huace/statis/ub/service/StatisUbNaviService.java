package cn.huace.statis.ub.service;

import cn.huace.common.service.BaseService;
import cn.huace.statis.ub.entity.StatisUbNavi;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by huangdan on 2016/12/27.
 */
@Service
public class StatisUbNaviService extends BaseService<StatisUbNavi, Integer> {
    public Page<StatisUbNavi> listUbNavi(Map<String, Object> searchMap, Integer page, Integer rows) {
//        Map<String, Object> searchParams = new HashedMap();
//        if (shopId!=null) {
//            searchParams.put("EQ_shopId", shopId);
//        }
//        if (type!=null) {
//            searchParams.put("EQ_type", type);
//        }
//        if(startDate!=null){
//            searchParams.put("GTE_statisDate", DateUtils.getStartTime(startDate));
//        }
//        if(endDate!=null){
//            searchParams.put("LTE_statisDate", DateUtils.getEndTime(endDate));
//        }
        Page<StatisUbNavi> pageResult = findAll(searchMap, page, rows);
        return pageResult;
    }
}
