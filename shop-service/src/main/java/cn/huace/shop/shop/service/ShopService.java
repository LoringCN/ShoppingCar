package cn.huace.shop.shop.service;


import cn.huace.common.service.BaseService;
import cn.huace.shop.shop.entity.Shop;
import cn.huace.shop.shop.repository.ShopRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huangdan on 2016/12/27.
 */
@Service
public class ShopService extends BaseService<Shop,Integer>
{
    @Autowired
    private ShopRepository shopRepository;


    public boolean findDulplicateName(String name,Integer dishId){
        Map<String, Object> searchParams = new HashedMap();
        if (StringUtils.isNotBlank(name)){
            searchParams.put("EQ_name",name);
        }
        if (null!=dishId){
            searchParams.put("NE_id",dishId);
        }
        List<Shop> restaurants = findAll(searchParams);
        if (CollectionUtils.isNotEmpty(restaurants)){
            return true;
        }
        return false;
    }

    /**
     * 根据商店名查询商店信息
     */
    public Shop findShopByShopName(String shopName){
        return shopRepository.findShopByName(shopName);
    }

    /**
     * 查询所有正常的商店
     */
    public List<Shop> findAllAviableShop() {
        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_useFlag",true);
        return findAll(searchMap);
    }

    /**
     * 根据时间查询新增和修改的商店数据
     * @return
     */
    public List<Shop> findModifyAndNewShops(Date lastShopCacheTime) {
        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("GTE_modifiedTime",lastShopCacheTime);
        searchMap.put("EQ_useFlag",true);
        return findAll(searchMap);
    }
}
