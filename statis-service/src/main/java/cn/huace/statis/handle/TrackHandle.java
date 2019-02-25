package cn.huace.statis.handle;

import cn.huace.statis.track.entity.StatisTrack;
import cn.huace.statis.track.service.StatisTrackService;
import cn.huace.statis.utils.StatContants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huangdan on 2017/7/13.
 */
@Component
@Slf4j
public class TrackHandle implements BasicHandle {

    @Autowired
    private StatisTrackService statisTrackService;

    public void readLog(Map<String, String> str, String timeStr) {
        String x, y;
        StatisTrack track = new StatisTrack();
        track.setDevId(str.get(StatContants.TRACK_KEY.DEV_Id));
        track.setAtTime(new Date(Long.parseLong(str.get(StatContants.TRACK_KEY.TS))));
        track.setShopId(Integer.parseInt(str.get(StatContants.TRACK_KEY.SHOP_ID)));
        track.setLocation(str.get(StatContants.TRACK_KEY.LOCATION));
        x = str.get(StatContants.TRACK_KEY.X);
        y = str.get(StatContants.TRACK_KEY.Y);
        track.setLocationX(x);
        track.setLocationY(y);

//        将x,y,权值存在map中
        int shopId = Integer.parseInt(str.get(StatContants.TRACK_KEY.SHOP_ID));
        if (HandleData.xAndYMap.get(shopId) == null) {
            Map<String, Integer> map = new HashMap<>();
            map.put(x + "," + y, 1);
            HandleData.xAndYMap.put(shopId, map);
        } else {
            if (HandleData.xAndYMap.get(shopId).get(x + "," + y) == null) {
                HandleData.xAndYMap.get(shopId).put(x + "," + y, 1);
            } else {
                HandleData.xAndYMap.get(shopId).put(x + "," + y, 1 + HandleData.xAndYMap.get(shopId).get(x + "," + y));
            }
        }
        statisTrackService.save(track);
    }

    public void handleData(String timeStr) {

    }
}
