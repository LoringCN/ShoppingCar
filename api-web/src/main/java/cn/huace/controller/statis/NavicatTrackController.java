package cn.huace.controller.statis;

import cn.huace.common.bean.HttpResult;
import cn.huace.common.utils.DateUtils;
import cn.huace.common.utils.HeaderUtils;
import cn.huace.controller.base.BaseFrontController;
import cn.huace.controller.statis.bean.Track;
import cn.huace.controller.statis.bean.TrackItem;
import cn.huace.statis.track.entity.NavicatTrack;
import cn.huace.statis.track.service.NavicatTrackService;
import cn.huace.statis.utils.StatContants;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@Api(value = "/navicat", description = "导航轨迹上报")
@RequestMapping(value = "/navicat")
public class NavicatTrackController extends BaseFrontController {

    @Autowired
    private NavicatTrackService navicatTrackService;

    @ApiOperation(value = "导航轨迹上报")
    @RequestMapping(value = "/track", method = {RequestMethod.POST})
    public HttpResult track(HttpServletRequest request) throws Exception {

        Integer versionCode = HeaderUtils.getAppCode(request);
        if(versionCode < StatContants.NAVICAT_TRACK_VERSION) {
            return HttpResult.createSuccess("");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Track track = objectMapper.readValue(request.getInputStream(), Track.class);
        List<TrackItem> content = track.getTrack();
        HttpResult httpResult = null;
        if (content != null) {
            log.info("********* 上报运动轨迹数据总数，trackNum={}", content.size());
            Integer argsShopId = findShopId(request);
            String argsDevId = findDevId(request);
            Date date = new Date();
            List<NavicatTrack> navicatTracks = new ArrayList<>();
            for (TrackItem key : content) {
                if (key != null) {
                    NavicatTrack navicatTrack = new NavicatTrack();
                    navicatTrack.setShopId(argsShopId);
                    navicatTrack.setAtTime(date);
//                    navicatTrack.setBatchId(DateUtils.date2String(date,DateUtils.YEARMONTHDATEHOURMINUTESECOND));
                    navicatTrack.setBatchId(date.getTime());
                    navicatTrack.setDevId(argsDevId);
                    navicatTrack.setLocation(key.getLocation());
                    navicatTrack.setLocationX(key.getX());
                    navicatTrack.setLocationY(key.getY());
                    navicatTrack.setTs(key.getTs());
                    navicatTracks.add(navicatTrack);
                }
            }
          int num =  navicatTrackService.batchInsert(navicatTracks);
            if(num == navicatTracks.size() || num == content.size()){
                httpResult = HttpResult.createSuccess("保存成功，数量："+num);
            }else {
                log.info("导航轨迹上报接口：插入数量为："+num+";总数量"+navicatTracks.size());
                httpResult = HttpResult.createFAIL("保存失败");
            }
        }
        return httpResult;
    }

    @ApiOperation(value = "test")
    @RequestMapping(value = "/test", method = {RequestMethod.POST})
    public String test(){
        Track track = new Track();
        track.setDate(DateUtils.getCurrentTime());
        List<TrackItem> list = new ArrayList<>();
        TrackItem trackItem = new TrackItem();
        trackItem.setLocation("测试");
        trackItem.setTs("12345678");
        trackItem.setX("1.1");
        trackItem.setY("2.2");
        list.add(trackItem);
        track.setTrack(list);
        return JSON.toJSONString(track);
    }

}
