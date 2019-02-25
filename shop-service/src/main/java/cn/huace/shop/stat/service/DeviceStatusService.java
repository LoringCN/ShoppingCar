package cn.huace.shop.stat.service;

import cn.huace.common.service.BaseService;
import cn.huace.shop.stat.entity.DeviceStatus;
import cn.huace.shop.stat.repository.DeviceStatusRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DeviceStatusService extends BaseService<DeviceStatus, Integer> {

    public DeviceStatus findOne(Integer shopId, String devId) {
        return ((DeviceStatusRepository) baseRepository).findOne(shopId, devId);
    }

}
