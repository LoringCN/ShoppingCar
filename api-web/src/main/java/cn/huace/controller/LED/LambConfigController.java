package cn.huace.controller.LED;

import cn.huace.common.bean.HttpFrontResult;
import cn.huace.controller.base.BaseFrontController;
import cn.huace.shop.bluetooth.entity.LambConfig;
import cn.huace.shop.bluetooth.service.LambConfigService;
import cn.huace.shop.shop.entity.Shop;
import cn.huace.shop.shop.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Date;

/**
 *  商品管理
 * Created by yld on 2017/5/16.
 */
@Slf4j
@RestController
@Api(value = "/led",description = "LED灯管理")
@RequestMapping("/led/info")
public class LambConfigController extends BaseFrontController {

    @Autowired
    private LambConfigService lambConfigService;

    @Autowired
    private ShopService shopService;

    @RequestMapping
    public HttpFrontResult info(String data, HttpServletRequest request){
        String jsonStr ;
        try {
            jsonStr = new String (Base64.getDecoder().decode(data),"UTF-8");
            log.info("LED接口 info(), data:{}",jsonStr);
        } catch (UnsupportedEncodingException e) {
            log.error("LED接口base64解码异常！data:{}",data);
            return HttpFrontResult.createFAIL("LED接口base64解码异常！");
        }

        JSONObject jsonObj = JSONObject.fromObject(jsonStr);
        String mac = jsonObj.get("mac").toString();
        String id = jsonObj.get("id").toString();
        Double x = (Double) jsonObj.get("x");
        Double y = (Double) jsonObj.get("y");
        String pulse = jsonObj.get("pulse").toString();
        Integer power = (Integer) jsonObj.get("power");
        Integer shopId = (Integer) jsonObj.get("shopId");

        LambConfig lambConfig = lambConfigService.findByLedId(id) == null ? new LambConfig():lambConfigService.findByLedId(id);
        lambConfig.setLedId(id);
        lambConfig.setMac(mac);
        lambConfig.setX(x);
        lambConfig.setY(y);
        lambConfig.setPulse(pulse);
        lambConfig.setPower(power);
//        Shop shop = shopService.findOne(shopId);
//        if(shop == null){
//            log.error("LED接口数据异常，商店匹配错误！ data:{}",jsonStr);
//            return HttpFrontResult.createFAIL("shopId="+shopId+",未找到对应商店信息！");
//        }
//        lambConfig.setShop(shop);
        lambConfig.setSign(data);
        lambConfig.setCreator("system");
        lambConfig.setModifier("system");
        lambConfig.setModifiedTime(new Date());
        lambConfig.setIsEnabled(true);

        if(lambConfigService.save(lambConfig) == null){
            log.error("LED接口保存异常! data:{}",jsonStr);
            return HttpFrontResult.createFAIL("LED接口base64解码异常！");
        }else {
            log.info("LED接口保存成功!");
            return HttpFrontResult.createSuccess("LED接口保存成功！");
        }

    }

    @RequestMapping(value = "decode",method = RequestMethod.POST)
    public HttpFrontResult decode(String str, HttpServletRequest request){
        /**test start*/
//        String str = "{\"mac\": \"68:DB:CA:29:0C:5B\",\"id\": \"FE8766555\",\"x\": 11.78,\"y\": 13.22,\"pulse\": \"Ab123\",\"power\": 1}";
        try {
            String base64encodedString = Base64.getEncoder().encodeToString(str.getBytes("utf-8"));
            return HttpFrontResult.createSuccess(base64encodedString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        /**test end*/
        return HttpFrontResult.createFAIL("接口异常！");
    }

}
