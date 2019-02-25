package cn.huace.shop.stat.repository;

import cn.huace.common.repository.BaseRepository;
import cn.huace.shop.device.entity.Device;
import cn.huace.shop.stat.entity.DeviceStatus;
import org.springframework.data.jpa.repository.Query;

public interface DeviceStatusRepository extends BaseRepository<DeviceStatus, Integer> {

    @Query("select o from DeviceStatus o where o.shop.id=?1 and o.devId=?2")
    public DeviceStatus findOne(Integer shopId,String devId);
}
