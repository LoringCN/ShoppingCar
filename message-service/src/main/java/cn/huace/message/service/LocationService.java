package cn.huace.message.service;

import cn.huace.message.entity.Location;

import java.util.List;

/**
 * Created by yld on 2017/10/19.
 */
public interface LocationService {
    Location saveLocation(Location location);
    Location findLocationByDevId(String devId);
    Boolean handleMessage(String message);
    List<Location> findLocationsByDevIds(String[] devIds);
    List<Location> findAllRequirePushLocations(Integer shopId);
}
