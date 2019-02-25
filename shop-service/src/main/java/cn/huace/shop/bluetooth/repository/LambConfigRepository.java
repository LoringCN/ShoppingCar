package cn.huace.shop.bluetooth.repository;

import cn.huace.common.repository.BaseRepository;
import cn.huace.shop.bluetooth.entity.LambConfig;
import org.springframework.data.jpa.repository.Query;

/**
 * LED灯 dao层
 * created by Loring on 2018-05-05
 */
public interface LambConfigRepository extends BaseRepository<LambConfig,Integer> {

    @Query("select o from LambConfig o where o.ledId = ?1")
    LambConfig findByLedId(String ledId);
}
