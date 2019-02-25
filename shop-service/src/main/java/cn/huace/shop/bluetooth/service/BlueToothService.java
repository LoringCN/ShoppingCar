package cn.huace.shop.bluetooth.service;

import cn.huace.common.service.BaseService;
import cn.huace.shop.bluetooth.entity.BlueTooth;
import cn.huace.shop.bluetooth.repository.BlueToothRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by wjcomputer on 2017/10/30.
 */
@Service
public class BlueToothService extends BaseService<BlueTooth,Integer> {
    public Integer findFloorNoByBlueToothId(String blueToothId){
        return ((BlueToothRepository)baseRepository).findFloorNoByBlueToothId(blueToothId);
    }

    public BlueTooth findByBlueBoothId(String blueToothId){
        return ((BlueToothRepository)baseRepository).findByBlueBoothId(blueToothId);
    }
}
