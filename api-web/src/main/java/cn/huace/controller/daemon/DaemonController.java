package cn.huace.controller.daemon;

import cn.huace.common.bean.HttpFrontResult;
import cn.huace.common.utils.HeaderUtils;
import cn.huace.controller.base.BaseFrontController;
import cn.huace.shop.device.service.DeviceService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RestController
@Api(value = "/daemon", description = "守护进程")
@RequestMapping("/daemon")
public class DaemonController  extends BaseFrontController {

    @Autowired
    private DeviceService deviceService;

    @RequestMapping("/versionReport")
    public HttpFrontResult versionReport(HttpServletRequest request) {
        HttpFrontResult httpFrontResult;
        Integer shopId = findShopId(request);
        String devId = findDevId(request);
        String packageName = HeaderUtils.getAppPackage(request);
        String daemonVersion = HeaderUtils.getAppVersion(request);
//        log.info("versionReport() *** start *** shopId : " + shopId +",devId:"+ devId + ",packageName : " + packageName + ",daemonVersion : " + daemonVersion );
        if(packageName == null && daemonVersion == null){
            log.info("versionReport() *** 参数异常：hc_package={},hc_version={}",packageName,daemonVersion);
            return HttpFrontResult.createFAIL("参数异常：hc_package="+packageName+",hc_version="+daemonVersion);
        }
        Map<String,String> map = deviceService.upDaemonVersion(shopId,devId,daemonVersion);
        if(map.get("code").equals("0")){
//            log.info("versionReport() *** success *** 设备守护进程版本更新成功！，当前设备:devId = {},当前版本号daemonVersion={}，msg ={}",devId,daemonVersion,map.get("msg"));
            httpFrontResult = HttpFrontResult.createSuccess(map.get("msg"));
        }else {
//            log.info("versionReport() *** fail *** 设备守护进程版本更新失败！，当前设备:devId = {},当前版本号daemonVersion={}，msg={}",devId,daemonVersion,map.get("msg"));
            httpFrontResult = HttpFrontResult.createFAIL(map.get("msg"));
        }
        return httpFrontResult;
    }

}
