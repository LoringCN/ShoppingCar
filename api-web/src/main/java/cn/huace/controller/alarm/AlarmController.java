package cn.huace.controller.alarm;

import cn.huace.common.bean.HttpFrontResult;
import cn.huace.controller.base.BaseFrontController;
import cn.huace.shop.app.entity.Alarm;
import cn.huace.shop.app.entity.App;

import cn.huace.shop.app.service.AlarmService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaoling on 2017/5/23.
 */

@Slf4j
@RestController
@Api(value = "/alarm", description = "获取告警灯ID列表")
@RequestMapping("/alarm")
public class AlarmController extends BaseFrontController {

    @Autowired
    private AlarmService alarmService;

    @RequestMapping ("/ledid_list")
    public HttpFrontResult list(HttpServletRequest request){

        Integer shopId = findShopId(request);
        log.info("shopId : " + shopId);
        String ledIdList = alarmService.getByShopId(shopId);
        log.info("ledIdList : " + ledIdList);
        return HttpFrontResult.createSuccess("",ledIdList);
    }
}
