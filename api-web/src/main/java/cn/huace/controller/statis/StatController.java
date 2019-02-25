package cn.huace.controller.statis;

import cn.huace.common.bean.HttpFrontResult;
import cn.huace.controller.base.BaseFrontController;
import cn.huace.controller.statis.bean.Behave;
import cn.huace.controller.statis.bean.Track;
import cn.huace.controller.statis.bean.TrackItem;
import cn.huace.controller.statis.bean.UserBehave;
import cn.huace.statis.utils.StatContants;
import cn.huace.statis.utils.StatUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by huangdan on 2017/5/7.
 */
@Slf4j
@RestController
@Api(value = "/stat", description = "统计上报")
@RequestMapping(value = "/stat")
public class StatController extends BaseFrontController {

    @ApiOperation(value = "用户行为")
    @RequestMapping(value = "/ub", method = {RequestMethod.POST})
    public HttpFrontResult ub(HttpServletRequest request) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserBehave userBehave = objectMapper.readValue(request.getInputStream(), UserBehave.class);
        List<Behave> content = userBehave.getContent();
        List<String> args = new ArrayList<String>();
        args.add(StatContants.STAT_SHOP_ID + StatContants.STAT_COLON + findShopId(request));
        args.add(StatContants.UB_TYPE + StatContants.STAT_COLON + userBehave.getType());
        for (Behave key : content) {
            if (key != null) {
                args.add(key.getKey() + StatContants.STAT_COLON + key.getValue());
            }
        }
        args.add(StatContants.DEV_ID + StatContants.STAT_COLON + findDevId(request));
        StatUtils.stat(StatContants.STAT_TYPE_UB, args.toArray(new String[args.size()]));
        return HttpFrontResult.createSuccess(null);
    }

    @ApiOperation(value = "运动轨迹")
    @RequestMapping(value = "/track", method = {RequestMethod.POST})
    public HttpFrontResult track(HttpServletRequest request) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Track track = objectMapper.readValue(request.getInputStream(), Track.class);
        List<TrackItem> content = track.getTrack();
        if (content != null) {
            log.info("********* 上报运动轨迹数据总数，trackNum={}", content.size());
            String argsShopId = StatContants.STAT_SHOP_ID + StatContants.STAT_COLON + findShopId(request);
            String argsDevId = StatContants.TRACK_KEY.DEV_Id + StatContants.STAT_COLON + findDevId(request);
            for (TrackItem key : content) {
                if (key != null) {
                    String argsX = StatContants.TRACK_KEY.X + StatContants.STAT_COLON + key.getX();
                    String argsY = StatContants.TRACK_KEY.Y + StatContants.STAT_COLON + key.getY();
                    String argsLocation = StatContants.TRACK_KEY.LOCATION + StatContants.STAT_COLON + key.getLocation();
                    String ts = StatContants.TRACK_KEY.TS + StatContants.STAT_COLON + key.getTs();
                    StatUtils.stat(StatContants.STAT_TYPE_TRACK, argsShopId, argsDevId, argsLocation, ts, argsX, argsY);
                }
            }
        }
        return HttpFrontResult.createSuccess(null);
    }
}
