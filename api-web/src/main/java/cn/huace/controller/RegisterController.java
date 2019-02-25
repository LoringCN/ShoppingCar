package cn.huace.controller;

import cn.huace.common.bean.HttpFrontResult;
import cn.huace.common.config.SystemConfig;
import cn.huace.common.utils.Encodes;
import cn.huace.common.utils.HeaderUtils;
import cn.huace.controller.base.BaseFrontController;
import cn.huace.shop.device.entity.Device;
import cn.huace.shop.device.service.DeviceService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by huangdan on 2017/5/7.
 */
@Slf4j
@RestController
@Api(value = "/register", description = "自动注册")
@RequestMapping(value = "/register")
public class RegisterController extends BaseFrontController {
    @Autowired
    private DeviceService deviceService;

    @RequestMapping
    public HttpFrontResult register(String devId, String ts, String random, String sign, HttpServletRequest request) {
        log.info("register() *** start *** devId:" + devId + "sign : " + sign + ",ts : " + ts + ",random : " + random);
        if (!StringUtils.isAllLowerCase(random)) {
            log.error("error params");
            return HttpFrontResult.createFAIL("注册失败，请求参数非法");
        }
        String mSign = Encodes.md5(ts + devId + random);
        log.info("register():mSign:" + mSign +";devId:"+devId);
        if (mSign.equalsIgnoreCase(sign)) {
            Device device = deviceService.findDevice(devId);
            if (device == null) {
                return HttpFrontResult.createFAIL("注册失败，设备不存在");
            } else {
                deviceService.updateVersion(devId, HeaderUtils.getAppVersion(request));
                setDevIdToSession(request, device.getDevId());
                setShopIdToSession(request, device.getShop().getId());
                Map map = new HashedMap();
                map.put("shopId", device.getShop().getId());
                map.put("filePre", SystemConfig.getInstance().getFilePre());
                map.put("compass", device.getCompass());
                map.put("ssid",device.getShop().getSsid());
//                map.put("pwd",device.getShop().getPwd());
                return HttpFrontResult.createSuccess(map);
            }

        } else {
            return HttpFrontResult.createFAIL("注册失败，签名错误");
        }
    }

}
