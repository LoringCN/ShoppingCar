package cn.huace.statis.search.repository;

import cn.huace.common.repository.BaseRepository;
import cn.huace.statis.search.entity.StatisSearch;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by yld on 2017/12/24.
 */
public interface StatisSearchRepository extends BaseRepository<StatisSearch, Integer> {

    @Query(value = "SELECT DISTINCT shop_id from statis_search where search_time >= ?1 and search_time <= ?2", nativeQuery = true)
    List<Integer> findShopIdByTime(Date startTime, Date endTime);

    @Query(value = "SELECT * from statis_search where search_time >= ?1 and search_time <= ?2 and shop_id = ?3", nativeQuery = true)
    List<StatisSearch> findByTimeAndShopId(Date startDate, Date endDate, Integer shopId);

}
