package cn.huace.sys.repository;



import cn.huace.common.repository.BaseRepository;
import cn.huace.sys.entity.RegionArea;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RegionAreaRepository extends BaseRepository<RegionArea, Integer>
{
    @Query("select distinct p from RegionArea p  where p.cityId =?1")
    List<RegionArea> findAreas(Integer cityId);

    @Query("select p from RegionArea p where p.areaId =?1")
    RegionArea find(Integer areaId);
}
