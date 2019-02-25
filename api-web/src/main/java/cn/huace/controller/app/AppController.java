package cn.huace.controller.app;

import cn.huace.common.bean.HttpFrontResult;
import cn.huace.common.utils.HeaderUtils;
import cn.huace.common.utils.redis.RedisService;
import cn.huace.controller.base.BaseFrontController;
import cn.huace.shop.app.entity.App;
import cn.huace.shop.app.service.AppService;
import cn.huace.shop.device.service.DeviceGroupService;
import cn.huace.shop.device.service.DeviceService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by herry on 2017/5/21.
 */


@Slf4j
@RestController
@Api(value = "/app", description = "应用升级")
@RequestMapping("/app")
public class AppController extends BaseFrontController {
    @Autowired
    private AppService appService;

    @Autowired
    private DeviceGroupService deviceGroupService;

    @Autowired
    private RedisService redisService;

    @RequestMapping("/check")
    public HttpFrontResult check(HttpServletRequest request) {
        String packageName = HeaderUtils.getAppPackage(request);
        Integer versionCode = HeaderUtils.getAppCode(request);
        Integer shopId = findShopId(request);
        String devId = findDevId(request);
        /*添加同一设备请求周期设为：30分钟,周期内所有请求设置为不更新*/
        String key = "upgrade_check_"+devId + "_"+ packageName;
        String lastCheckTsStr =  redisService.getCacheValue(key);
        boolean shouldDeliveryMeta =  false;
        if (TextUtils.isEmpty(lastCheckTsStr)){
            shouldDeliveryMeta = true;
        }else  {
            long lastCheckTs =  Long.parseLong(lastCheckTsStr);
            if (Math.abs(System.currentTimeMillis() - lastCheckTs) >= 60 * 60 * 1000) {
                shouldDeliveryMeta = true;
            }
        }
        if (!shouldDeliveryMeta) {
            return new HttpFrontResult(1, "无更新", null);
        }
        redisService.setCacheValue(key,String.valueOf(System.currentTimeMillis()));

        log.info("check() *** start *** shopId : " + shopId +",devId:"+ devId + ",packageName : " + packageName + ",versionCode : " + versionCode );
//        App app = appService.findLatestVersion(shopId, packageName, versionCode);
        App app = deviceGroupService.findAppByDevId(shopId,devId,packageName);
//        if (app == null || app.getVersionCode() <= versionCode || shouldNotUpdate(devId)) {

        if (app == null || app.getVersionCode() <= versionCode ) {
            return new HttpFrontResult(1, "无更新", null);
        }
        return HttpFrontResult.createSuccess(app);
    }

    /**
     * 判断是否不需要更新 临时条件
     * 小于 100 的不升(40台)，大于100的升(150台)
     * @param devId
     * @return
     */
    private boolean shouldNotUpdate(String devId) {
        if(devId.startsWith("SM0")){
            return devId.substring(devId.length()-3,devId.length()-2).equals("0");
        }
        return false;
    }
}
