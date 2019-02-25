package cn.huace.controller.ads;

import cn.huace.ads.Vo.AdsOnlineVo;
import cn.huace.ads.constant.AdsConstant;
import cn.huace.ads.service.AdsOnlineService;
import cn.huace.common.bean.HttpResult;
import cn.huace.controller.base.AdminBasicController;
import cn.huace.shop.device.service.DeviceService;
import cn.huace.sys.bean.ShiroUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 线上广告 控制层 created on 2018-05-30
 * @author Loring
 */
@Slf4j
@RestController
@Api(value = "/admin/ads/online",description = "最新版线上广告管理后台接口")
@RequestMapping(value = "/admin/ads/online")
public class AdsOnlineController  extends AdminBasicController {

    @Autowired
    private AdsOnlineService adsOnlineService;

    @Autowired
    private DeviceService deviceService;

    @ApiOperation(value = "线上广告列表查询 方法 (已停用)")
    @RequestMapping(value = "/list",method = RequestMethod.POST)
    public HttpResult list(AdsOnlineVo adsOnlineVo,
                           @RequestParam(defaultValue = "1") Integer page,
                           @RequestParam(defaultValue = "20") Integer rows
    ){
        log.info("*** 线上广告列表查询开始：list()，入参 adsOnlineVo：{}",JSONObject.fromObject(adsOnlineVo).toString());
        HttpResult httpResult = null;
        if(adsOnlineVo.getShopId()==null){
            httpResult = HttpResult.createFAIL("请选着一个商店！");
        }else {
            if(adsOnlineVo.getType() == null ){
            //默认开机视频广告
            adsOnlineVo.setType(AdsConstant.ADS.AD_TYPE.BOOT_VIDEO);
            }
            //默认查询有效的
            adsOnlineVo.setIsEnabled(null!= adsOnlineVo.getIsEnabled()?adsOnlineVo.getIsEnabled():true);

            httpResult = HttpResult.createSuccess(adsOnlineService.list(adsOnlineVo,page,rows));
        }

        log.info("*** 线上广告列表查询结束：出参 HttpResult {}",ToStringBuilder.reflectionToString(httpResult));
        return httpResult;
    }

    @ApiOperation(value = "线上广告列表查询 方法 (最新)")
    @RequestMapping(value = "/listNew",method = RequestMethod.POST)
    public HttpResult listNew(Integer shopId,
                           @RequestParam(defaultValue = "1") Integer type,
                           String name,
                           @RequestParam(defaultValue = "0")  Integer rank ,
                           @RequestParam(defaultValue = "1") Integer page,
                           @RequestParam(defaultValue = "20") Integer rows
    ){
        log.info("*** 线上广告列表查询开始：listNew()，入参 shopId={},type={},rank={},page={},rows={}",shopId,type,rank,page,rows);
        HttpResult httpResult = null;
        if(shopId == null){
            httpResult = HttpResult.createFAIL("请选着一个商店！");
        }else {
            httpResult = HttpResult.createSuccess(adsOnlineService.listNew(shopId,type,name,rank,page,rows));
        }
        log.info("*** 线上广告列表查询结束：出参 HttpResult {}",ToStringBuilder.reflectionToString(httpResult));
        return httpResult;

    }



    @ApiOperation(value = "查询投放设备列表")
    @RequestMapping(value = "/device/list",method = {RequestMethod.GET,RequestMethod.POST})
    public HttpResult deviceList(@RequestParam Integer shopId,
                                 @RequestParam Integer adId){
        log.info("*** 查询线上广告设备列表查询开始：deviceList()，入参 adId：{}",adId);
        HttpResult httpResult = null;
        if(shopId == null){
            httpResult = HttpResult.createFAIL("请选择一个商店！");
        }else if(adId == null){
            httpResult = HttpResult.createFAIL("请选择一个广告！");
        }else {
            List<String>  devList = adsOnlineService.findDevId(shopId,adId);
            Map<String,Object> resultMap = new HashMap<>();
            List<String> allDevices = deviceService.findDevByShopId(shopId);
            resultMap.put("allDevice",allDevices);
            if(devList == null || devList.size() == 0 || devList.get(0) == null){
                resultMap.put("deliverScope",-1);
                resultMap.put("selectedDevice",null);
            }else {
                resultMap.put("deliverScope",1);
                resultMap.put("selectedDevice",devList);
            }
            httpResult = HttpResult.createSuccess(resultMap);
        }
        log.info("*** 查询线上广告设备列表查询结束：出参 HttpResult {}",ToStringBuilder.reflectionToString(httpResult));
        return httpResult;
    }

    @ApiOperation(value = "修改投放设备列表")
    @RequestMapping(value = "/device/save",method = {RequestMethod.GET,RequestMethod.POST})
    public HttpResult deviceSave(@RequestParam Integer adId,
                                 @RequestParam String[] devIds,
                                 Integer rank){
        log.info("*** 查询线上广告设备列表查询开始：deviceList()，入参 adId：{},devIds",adId,devIds);
        HttpResult httpResult = null;
        ShiroUser user = getCurrentUser();
        if(rank == null){
            httpResult = HttpResult.createFAIL("请选择广告位！");
        }else {
            Boolean flag = adsOnlineService.updateDevId(adId, devIds, rank, user);
            if (flag) {
                httpResult = HttpResult.createSuccess("保存成功！");
            } else {
                httpResult = HttpResult.createFAIL("保存失败！");
            }
        }

        log.info("*** 查询线上广告设备列表查询结束：出参 HttpResult {}",ToStringBuilder.reflectionToString(httpResult));
        return httpResult;
    }

    /**
     *
     * @param adId
     * @return
     */
    @ApiOperation(value = "修改线广告线上线下状态")
    @RequestMapping(value = "/save",method = {RequestMethod.GET,RequestMethod.POST})
    public HttpResult save(@RequestParam Integer adId,
                           @RequestParam Integer isVoted,
                           @RequestParam String[] devIds,
                           @RequestParam(required = false,defaultValue = "0") Integer rank){
        log.info("*** 线上广告列表查询开始：edit()，入参 adId：{},devIds",adId,devIds);
        HttpResult httpResult = null;
        ShiroUser user = getCurrentUser();
        if( adsOnlineService.updateisVoted(adId,isVoted,devIds,rank,user)){
            httpResult = HttpResult.createSuccess("保存成功！");
        }else {
            httpResult = HttpResult.createFAIL("保存失败！");
        }
        log.info("*** 线上广告列表查询结束：出参 HttpResult {}",ToStringBuilder.reflectionToString(httpResult));
        return httpResult;
    }

    /**
     * 根据广告类型查询可上线广告
     * @param shopId
     * @param adType
     * @return
     */
    @ApiOperation(value = "根据广告类型查询可上线广告")
    @RequestMapping(value = "/preOnline",method = {RequestMethod.GET,RequestMethod.POST})
    public HttpResult preOnline(@RequestParam Integer shopId,Integer adType){
        log.info("根据广告类型查询可上线广告 开始：preOnline()，入参：shopId:{},adType:{}",shopId,adType);
        HttpResult httpResult = null;
        if(shopId == null){
            httpResult = HttpResult.createFAIL("请选择商店！");
        }else if( adType == null){
            httpResult = HttpResult.createFAIL("请选择广告类型！");
        }else {
            httpResult = HttpResult.createSuccess(adsOnlineService.preOnline(shopId, adType));
        }
        log.info("*** 根据广告类型查询可上线广告：出参 HttpResult {}",ToStringBuilder.reflectionToString(httpResult));
        return httpResult;
    }


}
