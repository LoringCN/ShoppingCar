package cn.huace.controller.location;

import cn.huace.common.bean.HttpResult;
import cn.huace.controller.base.BaseFrontController;
import cn.huace.message.entity.Location;
import cn.huace.message.entity.LocationMessage;
import cn.huace.message.entity.Position;
import cn.huace.message.enums.ShopCarStatus;
import cn.huace.message.service.LocationMessagePublisherService;
import cn.huace.message.service.LocationService;
import cn.huace.shop.bluetooth.entity.BlueTooth;
import cn.huace.shop.bluetooth.service.BlueToothService;
import cn.huace.shop.shop.entity.Shop;
import cn.huace.shop.shop.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 购物车上报位置接口
 * Created by yld on 2017/10/19.
 */
@Slf4j
@RestController
@Api(value = "/trace",description = "购物车上报位置接口")
@RequestMapping(value = "/trace")
public class LocationController extends BaseFrontController{
    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationMessagePublisherService publisherService;

    @Autowired
    private BlueToothService blueToothService;

    @Autowired
    private ShopService shopService;

    private static final long TIMESPAN = 60 * 1000;

    @ApiOperation(value = "/position",notes = "购物车位置上报，并推送位置信息给寻车app")
    @RequestMapping(value = "/position",method = RequestMethod.POST)
    public HttpResult tracePosition(HttpServletRequest request,@RequestBody Position position){
        log.info("********* 开始调用方法：tracePosition，参数：{}",position);
        Integer shopId = findShopId(request);
        if(StringUtils.isEmpty(shopId)){
            return HttpResult.createFAIL("非法接口访问！");
        }
        String devId = findDevId(request);
        if(StringUtils.isEmpty(devId)){
            return HttpResult.createFAIL("设备ID不能为空！");
        }
        //查询是否有记录
        Location location = locationService.findLocationByDevId(devId);
        String carStatus = null;
        //购物车上一次状态
        String lastCarStatus = null;
        String btId = position.getBt_id();

        if(StringUtils.isEmpty(btId)){
            log.info("******** 光信号，devId={}",devId);
            /*
                光定位信号，此时购物车处于超市内，需要将该车从寻车app地图上删掉
             */
            carStatus = ShopCarStatus.HANLDED.getValue();
        }else{
            Shop shop = shopService.findOne(shopId);
             /*
             * 如果上一次收到的是光信号，那么接下来一分钟之内的蓝牙信号都不需要，
             * 直接忽略，光信号继续更新
             */
             if(location != null && StringUtils.isEmpty(location.getBt1_id())){
                 //判断时间间隔
                 long timespan = System.currentTimeMillis() - location.getModifiedTime().getTime();
                 if(timespan > TIMESPAN){
                     //计算楼层
                     BlueTooth bt = blueToothService.findByBlueBoothId(btId);
                     if(bt == null){
                         log.info("******* 找不到蓝牙信息！！！");
                         return HttpResult.createFAIL("找不到蓝牙信息！！！");
                     }
                     carStatus = calcCarStatus(shop,bt,position,devId,btId);
                 }else {
                    log.info("****** 蓝牙信息距离上一次光信号时间间隔小于【一分钟】，shop={},devId = {}，btId = {},timespan = {}秒",shop.getName(),devId,btId,timespan/1000);
                    return HttpResult.createSuccess("蓝牙信息距离上一次光信号时间间隔小于【一分钟】,devId="+devId+"");
                 }
             }else {
                 //计算楼层
                 BlueTooth bt = blueToothService.findByBlueBoothId(btId);
                 if(bt == null){
                     log.info("******* 找不到蓝牙信息！！！");
                     return HttpResult.createFAIL("找不到蓝牙信息！！！");
                 }
                 carStatus = calcCarStatus(shop,bt,position,devId,btId);
             }
        }

        if(location != null){
            lastCarStatus = location.getCarStatus();
            location = updateLocation(location,position,carStatus);
        }else{
            location = newLocation(position,carStatus,devId,shopId);
        }
        //异步推送消息
        pushMessage(location,lastCarStatus);
        //存在则更新，不存在则新增
        Location result = locationService.saveLocation(location);
        if(result != null){
            log.info("******** 上报购物车位置成功，location: "+ result);
        }
        return HttpResult.createSuccess("上报信息成功！");
    }
    /**
     * 根据偏移距离和所在楼层计算购物车状态
     */
    private String calcCarStatus(Shop shop,BlueTooth bt,Position position,String devId,String btId){
        String carStatus = null;
        if(shop.getShopFloor().contains(String.valueOf(bt.getFloorNo()))){
            log.info("***** 超市所在楼层蓝牙信号，直接转化为光信号！！shop={},floor={},devId={},btId={}",shop.getName(),bt.getFloorNo(),devId,btId);
            //超市所在楼层蓝牙信号，直接转化为光信号
            position.setBt_id(null);
            position.setX("0.0");
            position.setY("0.0");
            position.setOffset(new Byte("0"));
            carStatus = ShopCarStatus.HANLDED.getValue();
        }else{
            position.setFloor(new Byte(String.valueOf(bt.getFloorNo().intValue())));
            //蓝牙信号，获取位置偏移值，计算购物车状态
            int offset = position.getOffset().intValue();
//            int alarmDistance = bt.getAlDis();
            /*
                在一楼时需要根据偏移距离判断购物车是否
                有丢失危险
            */
            if(shop.getAlarmFloor().contains(String.valueOf(bt.getFloorNo())) && offset < 0){
                //警告
                carStatus = ShopCarStatus.ALARM.getValue();
            }else{
                carStatus = ShopCarStatus.GATHER.getValue();
            }
        }
        return carStatus;
    }
    /**
     * 新增购物车location位置信息
     */
    private Location newLocation(Position position,String carStatus,String devId,Integer shopId){
        //不存在则新增
        Location location = new Location();

        location.setFloor(position.getFloor());
        location.setOffset(position.getOffset());
        location.setX(position.getX());
        location.setY(position.getY());
        location.setCarStatus(carStatus);
        location.setBt1_id(position.getBt_id());

        location.setDevId(devId);
        location.setShopId(shopId);
        return location;
    }
    /**
     * 更新购物车location位置信息
     */
    private Location updateLocation(Location location,Position position,String carStatus){
        //存在更新
        location.setFloor(position.getFloor());
        location.setOffset(position.getOffset());
        location.setX(position.getX());
        location.setY(position.getY());
        location.setCarStatus(carStatus);
        location.setBt1_id(position.getBt_id());
        location.setModifiedTime(new Date());

        if(ShopCarStatus.HANLDED.getValue().equals(carStatus)){
            location.setReason("光定位信号，购物车处于超市内！");
        }else {
            location.setReason(null);
        }
        return location;
    }
    /**
     * 推送消息
     */
    private void pushMessage(Location location,String lastCarStatus){
        LocationMessage message = convertLocation2LocationMessage(location);
        if(StringUtils.isEmpty(location.getBt1_id())){
            //光定位信号根据上一次状态判断是否需要进行推送
            if(ShopCarStatus.GATHER.getValue().equals(lastCarStatus)||
                    ShopCarStatus.ALARM.getValue().equals(lastCarStatus)){
                publisherService.publish(message);
            }
        }else {
            //蓝牙信号，每次都推送
            publisherService.publish(message);
        }
    }
    /**
     * 将上报的位置信息转化为推送需要的格式
     */
    private LocationMessage convertLocation2LocationMessage(Location location){
        if(location == null){
            return null;
        }
        LocationMessage message = new LocationMessage();
        message.setShopId(location.getShopId());
        message.setX(location.getX());
        message.setY(location.getY());
        message.setDevId(location.getDevId());
        message.setCarStatus(location.getCarStatus());
        if(location.getFloor() != null){
            message.setFloorName(String.valueOf(location.getFloor().intValue()));
        }

        return message;
    }
}
