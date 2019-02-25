package cn.huace.shop.map.repository;

import cn.huace.common.repository.BaseRepository;
import cn.huace.shop.map.entity.NaviMap;
import org.springframework.data.jpa.repository.Query;

public interface NaviMapRepository extends BaseRepository<NaviMap, Integer> {

    @Query("select max(o.version) from NaviMap o where o.shop.id=?1")
    public Integer findMaxIdByShop(Integer shopId);
}
