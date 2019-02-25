package cn.huace.shop.stat.service;

import cn.huace.common.service.BaseService;
import cn.huace.shop.stat.entity.LowBattery;
import cn.huace.shop.stat.repository.LowBatteryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by xiaoling on 2017/5/26.
 */
@Service
public class LowBatteryService extends BaseService<LowBattery, Integer> {
    @Autowired
    private LowBatteryRepository lowBatteryRepository;


    public LowBattery findOneRecord(Integer shopId, String devId){
        return ((LowBatteryRepository) baseRepository).findOneByDevId(shopId, devId);
    }

    // base on shopid+devid+NoProcess
    public LowBattery findOneRecordNoProcess(Integer shopId, String devId){
        return ((LowBatteryRepository) baseRepository).findOneByDevIdNoProcess(shopId, devId);
    }

}
