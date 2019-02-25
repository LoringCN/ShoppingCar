package cn.huace.shop.shop.repository;


import cn.huace.common.repository.BaseRepository;
import cn.huace.shop.shop.entity.Shop;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by huangdan on 2016/12/27.
 */
public interface ShopRepository extends BaseRepository<Shop, Integer> {

    @Query("select s from Shop s where s.name=?1 and s.useFlag = '1'")
    Shop findShopByName(String shopName);
}
