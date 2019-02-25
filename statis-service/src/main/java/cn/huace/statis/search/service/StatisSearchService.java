package cn.huace.statis.search.service;

import cn.huace.common.service.BaseService;
import cn.huace.statis.search.entity.StatisSearch;
import cn.huace.statis.search.repository.StatisSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by yld on 2017/12/24.
 */
@Service
public class StatisSearchService extends BaseService<StatisSearch, Integer> {

    @Autowired
    private StatisSearchRepository statisSearchRepository;

    public Page<StatisSearch> list(
            Map<String, Object> searchMap, Integer page, Integer rows
    ) {
        return findAll(searchMap, page, rows, Sort.Direction.DESC, "searchTime");
    }

    public List<Integer> findShopIdByTime(Date startDate, Date endDate) {
        return statisSearchRepository.findShopIdByTime(startDate, endDate);
    }

    public List<StatisSearch> findByTimeAndShopId(Date startDate, Date endDate, Integer shopId) {
        return statisSearchRepository.findByTimeAndShopId(startDate, endDate, shopId);
    }
}
