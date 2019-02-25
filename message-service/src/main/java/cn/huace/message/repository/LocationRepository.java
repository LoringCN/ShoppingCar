package cn.huace.message.repository;

import cn.huace.common.repository.BaseRepository;
import cn.huace.message.entity.Location;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by yld on 2017/10/19.
 */
public interface LocationRepository extends BaseRepository<Location,Integer>{
    Location findLocationByDevId(String devId);

    @Query("select l from Location l where l.devId in ?1")
    List<Location> findLocationsByDevIds(String[] devIds);

    @Query("select l from Location l where l.shopId = ?1 and (l.carStatus = '1' or l.carStatus = '2')")
    List<Location> findAllRequirePushLocations(Integer shopId);

}
