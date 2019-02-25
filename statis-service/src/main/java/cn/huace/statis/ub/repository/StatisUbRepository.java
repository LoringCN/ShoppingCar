package cn.huace.statis.ub.repository;

import cn.huace.common.repository.BaseRepository;
import cn.huace.statis.ub.entity.StatisUb;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by huangdan on 2016/12/27.
 */
public interface StatisUbRepository extends BaseRepository<StatisUb, Integer> {

    @Query(value = "SELECT DISTINCT shop_id from statis_ub where statis_date >= ?1 and statis_date <= ?2 and type = ?3",nativeQuery = true)
    List<Integer> findShopIdByTimeAndType(Date startTime, Date endTime,int type);

    @Query(value = "SELECT * from statis_ub where statis_date >= ?1 and statis_date <= ?2 and type = ?3 and shop_id = ?4",nativeQuery = true)
    List<StatisUb> findByTimeAndTypeAndShopId(Date startDate, Date endDate, int type, Integer shopId);

//    @Query(value = "SELECT * from statis_ub where type = ?1",nativeQuery = true)
//    List<StatisUb> findByTimeAndTypeAndShopId(int type);
}
