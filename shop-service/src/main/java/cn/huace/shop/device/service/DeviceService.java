package cn.huace.shop.device.service;


import cn.huace.common.service.BaseService;
import cn.huace.shop.device.Vo.DeviceVo;
import cn.huace.shop.device.entity.Device;
import cn.huace.shop.device.repository.DeviceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by huangdan on 2016/12/27.
 */
@Service
@Slf4j
public class DeviceService extends BaseService<Device, Integer> {
    @Autowired
    private DeviceRepository deviceRepository;

    public Device findDevice(String devId) {
        Map<String, Object> searchParams = new HashedMap();
        if (!StringUtils.isBlank(devId)) {
            searchParams.put("EQ_devId", devId);
        }
        try {
            Device device = findOne(searchParams);
            return device;
        } catch (Exception e) {
            return null;
        }
    }

    public void updateVersion(String devId, String currentVersion) {
        Map<String, Object> searchParams = new HashedMap();
        searchParams.put("EQ_devId", devId);
        try {
            Device device = findOne(searchParams);
            String initVersion = device.getInitialVersion();
            if (StringUtils.isEmpty(initVersion)) {
                device.setInitialVersion(currentVersion);
            }
            if (StringUtils.equals(currentVersion, device.getCurrentVersion())) {
                return;
            }
            device.setCurrentVersion(currentVersion);
            deviceRepository.save(device);

        } catch (Exception e) {
            //
            e.printStackTrace();
        }
    }

    public void updateCompass(String devId, String compass) {
        if (TextUtils.isEmpty(compass)) {
            return;
        }
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("EQ_devId", devId);
        try {
            Device device = findOne(searchParams);
            if (device == null) {
                log.error("no device found for update compass,devId : " + devId + ",compass : " + compass);
                return;
            }
            boolean shouldUpdate = false;
            String savedCompass = device.getCompass();
            if (TextUtils.isEmpty(savedCompass)) {
                device.setCompass(compass);
                shouldUpdate = true;
            } else if (savedCompass.equals(compass)) {
                shouldUpdate = false;
            } else {
                device.setCompass(compass);
                shouldUpdate = true;
            }
            if (shouldUpdate) {
                deviceRepository.save(device);
            }
        } catch (Exception e) {
            //
            e.printStackTrace();
        }
    }

    /**
     * 查詢商店下所有的设备号列表
     * @param shopId
     * @return
     */
    public List<String> findDevByShopId(Integer shopId ){
        return  deviceRepository.findDevByShopId(shopId);
    }
    /**
     * 查詢商店下所有的设备列表
     * @param shopId
     * @return
     */
    public List<DeviceVo> findAllDevByShopId(Integer shopId){
      return  deviceRepository.findAllDevByShopId(shopId).stream().map(device -> PoToVo(device,new DeviceVo())).collect(Collectors.toList());
    }

    public Map<String,String> upDaemonVersion(Integer shopId,String devId,String daemonVersion){
        Map<String,String> map = new HashMap<>();
        List<Device> deviceList = deviceRepository.findByShopIdAndDevId(shopId, devId);
        if(deviceList == null || deviceList.size() == 0){
            //失败
            map.put("code","1");
            map.put("msg","设备号:"+devId+" 不存在！");
        }else{
            Device device = deviceList.get(0);
            if(StringUtils.equals(device.getDaemonVersion(),daemonVersion)){
                map.put("code","1");
                map.put("msg","设备号:"+devId+" 版本一致，无需更新！");
            }else {
                device.setDaemonVersion(daemonVersion);
                if(save(device) != null){
                    //成功
                    map.put("code","0");
                    map.put("msg","设备守护进程版本更新成功！");
                }else {
                    //失败
                    map.put("code","1");
                    map.put("msg","设备号:"+devId+"设备守护进程版本更新失败！");
                }
            }

        }


        return map;
    }

    public DeviceVo PoToVo(Device source,DeviceVo target){
        BeanUtils.copyProperties(source,target);
        target.setId(source.getId());
        target.setShopId(source.getShop().getId());
        return target;
    }
}
