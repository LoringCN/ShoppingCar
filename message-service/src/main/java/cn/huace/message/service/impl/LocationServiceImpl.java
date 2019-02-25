package cn.huace.message.service.impl;

import cn.huace.common.service.BaseService;
import cn.huace.message.constant.TypeValues;
import cn.huace.message.entity.Location;
import cn.huace.message.enums.ShopCarStatus;
import cn.huace.message.repository.LocationRepository;
import cn.huace.message.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by yld on 2017/10/19.
 */
@Slf4j
@Service
public class LocationServiceImpl extends BaseService<Location,Integer> implements LocationService{
    @Autowired
    private LocationRepository locationRepository;

    private static final String TYPE_GATHER = "gather";
    private static final String TYPE_ALARM= "alarm";

    @Override
    public Location saveLocation(Location location) {
        return save(location);
    }

    @Override
    public Location findLocationByDevId(String devId) {
        return locationRepository.findLocationByDevId(devId);
    }

    @Override
    public List<Location> findLocationsByDevIds(String[] devIds) {
        return locationRepository.findLocationsByDevIds(devIds);
    }

    @Override
    public List<Location> findAllRequirePushLocations(Integer shopId) {
        return locationRepository.findAllRequirePushLocations(shopId);
    }

    /**
     * 批量更新购物车回收状态
     */
    @Transactional
    public Boolean handleMessage(String message){
        if(StringUtils.isEmpty(message)){
            return false;
        }
        JSONObject template = JSONObject.fromObject(message);
        String type = template.getString("type");
        String typeValue = handleType(type);
        JSONObject content = template.getJSONObject("content");
        if(content == null || content.isEmpty()){
            log.info("没有要处理的购物车信息！！！");
            return false;
        }

        String devIdKey = content.has("devId")?"devId":"devIdList";
        String devId = content.getString(devIdKey);
        if(StringUtils.isEmpty(devId)){
            log.info("购物车ID为空！！！");
            return false;
        }
        String[] devIds = devId.split("\\|");
        List<Location> locationList = findLocationsByDevIds(devIds);

        String reason = content.getString("reason");
        int handle = content.getInt("handle");

        for (Location location:locationList){
            if(ShopCarStatus.HANLDED.getValue().equals(location.getCarStatus())||
                    ShopCarStatus.LOSS.getValue().equals(location.getCarStatus())){
                /*
                    避免由于寻车app上购物车状态刷新不及时，出现多个管理员对同一购物车重复操作
                    可能导致购物车状态数据错误。
                 */
                continue;
            }
            location.setReason(reason);
            if(TYPE_GATHER.equals(typeValue)){
                location.setCarStatus(ShopCarStatus.HANLDED.getValue());
            }else if(TYPE_ALARM.equals(typeValue)){
                location.setCarStatus(
                        handle == 0 ? ShopCarStatus.HANLDED.getValue():ShopCarStatus.LOSS.getValue());
            }
        }
        int batchRes = batchUpdate(locationList);
        if(batchRes == locationList.size()){
            return true;
        }
        return false;
    }

    /**
     *  根据请求type判断是处理待收集购物车请求还是告警购物车请求
     */
    private String handleType(String type){
        if(StringUtils.isEmpty(type)){
            return null;
        }
        if(TypeValues.HANDLE_GATHER_REQ.equalsIgnoreCase(type.trim()) ||
                TypeValues.BATCH_HANDLE_GATHER_REQ.equalsIgnoreCase(type.trim())){

            return TYPE_GATHER;
        }else if(TypeValues.HANDLE_ALARM_REQ.equalsIgnoreCase(type.trim()) ||
                TypeValues.BATCH_HANDLE_ALARM_REQ.equalsIgnoreCase(type.trim())){

            return TYPE_ALARM;
        }else {
            return null;
        }
    }
}
