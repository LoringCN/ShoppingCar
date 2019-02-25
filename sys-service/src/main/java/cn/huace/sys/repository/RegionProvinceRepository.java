package cn.huace.sys.repository;



import cn.huace.common.repository.BaseRepository;
import cn.huace.sys.entity.RegionProvince;
import org.springframework.data.jpa.repository.Query;

public interface RegionProvinceRepository extends BaseRepository<RegionProvince, Integer>
{
    @Query("select p from RegionProvince p where p.provinceId =?1")
    RegionProvince find(Integer provinceId);
}
