package cn.huace.shop.map.service;

import cn.huace.common.service.BaseService;
import cn.huace.shop.map.entity.NaviMap;
import cn.huace.shop.map.repository.NaviMapRepository;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NaviMapService extends BaseService<NaviMap, Integer> {

    public Integer findMaxMapIdByShop(Integer shopId) {
        return ((NaviMapRepository) baseRepository).findMaxIdByShop(shopId);
    }

    public NaviMap findNewestNavimapByShop(Integer shopId) {
        Map<String, Object> searchParams = new HashedMap();
        searchParams.put("EQ_shop.id", shopId);
        searchParams.put("EQ_useFlag", true);
        Sort sort = new Sort(Sort.Direction.DESC, "version");
        List<NaviMap> list = findAll(searchParams, sort);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
}
