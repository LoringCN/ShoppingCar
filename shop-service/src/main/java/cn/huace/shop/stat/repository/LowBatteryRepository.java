package cn.huace.shop.stat.repository;

import cn.huace.common.repository.BaseRepository;
import cn.huace.shop.stat.entity.LowBattery;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * Created by xiaoling on 2017/5/26.
 */

public interface LowBatteryRepository extends BaseRepository<LowBattery, Integer>{

    @Query("select l from LowBattery l where l.shop.id=?1 and l.devId=?2")
    public LowBattery findOneByDevId(Integer shopId,String devId);

//    @Query(value = "select l from LowBattery l where l.shop.id=?1 and l.devId=?2 and l.isProcess=false ORDER BY l.modifiedTime desc limit 1",nativeQuery = true)
    @Query(value = "SELECT * from low_battery where shop_id = ?1 and dev_id = ?2 and process_flag = 0 ORDER BY modified_time desc limit 1",nativeQuery = true)
    public LowBattery findOneByDevIdNoProcess(Integer shopId, String devId);

}
