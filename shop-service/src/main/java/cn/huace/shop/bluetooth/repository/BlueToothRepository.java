package cn.huace.shop.bluetooth.repository;

import cn.huace.common.repository.BaseRepository;
import cn.huace.shop.bluetooth.entity.BlueTooth;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by wjcomputer on 2017/10/30.
 */
public interface BlueToothRepository extends BaseRepository<BlueTooth, Integer> {

    @Query ("select bt.floorNo from BlueTooth bt where bt.blueToothId=?1 and bt.flag='true'")
     Integer findFloorNoByBlueToothId(String blueToothId);

    @Query("select bt from BlueTooth bt where bt.blueToothId =?1 and bt.flag=true")
    BlueTooth findByBlueBoothId(String blueToothId);



}
