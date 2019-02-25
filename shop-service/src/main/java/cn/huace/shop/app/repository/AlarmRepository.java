package cn.huace.shop.app.repository;

import cn.huace.common.repository.BaseRepository;
import cn.huace.shop.app.entity.Alarm;
import cn.huace.shop.shop.entity.Shop;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by herry on 2017/5/22.
 */
public interface AlarmRepository extends BaseRepository<Alarm,Integer>{

    @Modifying
    @Transactional
    @Query("delete from Alarm o where o.shop.id=?1")
    public void deleteAllByShopId(Integer shopId);

    @Query("select o.ledIdlist from Alarm o where o.shop.id=?1")
    public String getByShopId(Integer shopId);
}
