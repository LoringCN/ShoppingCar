package cn.huace.sys.repository;



import cn.huace.common.repository.BaseRepository;
import cn.huace.sys.entity.RegionCity;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RegionCityRepository extends BaseRepository<RegionCity, Integer>
{

    @Query("select distinct p from RegionCity p  where p.provinceId =?1")
    List<RegionCity> findCitys(Integer provinceId);

    @Query("select p from RegionCity p where p.cityId =?1")
    RegionCity find(Integer cityId);
}
