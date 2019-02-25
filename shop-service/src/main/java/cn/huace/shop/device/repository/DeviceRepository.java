package cn.huace.shop.device.repository;


import cn.huace.common.repository.BaseRepository;
import cn.huace.shop.device.entity.Device;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by huangdan on 2016/12/27.
 */
public interface DeviceRepository extends BaseRepository<Device, Integer> {

    @Query(value = "select t from Device t where t.shop.id = ?1")
    List<Device> findAllDevByShopId(Integer shopId);

    @Query(value = "select t.devId from Device t where t.shop.id = ?1")
    List<String> findDevByShopId(Integer shopId);

    @Query(value = "select t from Device t where t.shop.id = ?1 and t.devId = ?2")
    List<Device> findByShopIdAndDevId(Integer shopId,String devId);
}
