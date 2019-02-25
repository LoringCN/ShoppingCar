package cn.huace.statis.ub.service;

import cn.huace.common.service.BaseService;
import cn.huace.statis.ub.entity.StatisUb;
import cn.huace.statis.ub.repository.StatisUbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by huangdan on 2016/12/27.
 */
@Service
public class StatisUbService extends BaseService<StatisUb, Integer> {

    @Autowired
    private StatisUbRepository statisUbRepository;

    public Page<StatisUb> listUb(Map<String, Object> searchMap, Integer page, Integer rows) {
        Page<StatisUb> pageResult = findAll(searchMap, page, rows, Sort.Direction.DESC, "statisDate");
        return pageResult;
    }

    public List<Integer> findShopIdByTimeAndType(Date startTime, Date endTime, int type) {
        return statisUbRepository.findShopIdByTimeAndType(startTime, endTime, type);
    }

    public List<StatisUb> findByTimeAndTypeAndShopId(Date startDate, Date endDate, int type, Integer shopId) {
        return statisUbRepository.findByTimeAndTypeAndShopId(startDate, endDate, type, shopId);
    }
}
