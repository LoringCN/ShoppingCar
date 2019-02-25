package cn.huace.controller.admin.shop;


import cn.huace.common.bean.HttpResult;
import cn.huace.common.bean.TreeBean;
import cn.huace.common.utils.redis.RedisService;
import cn.huace.controller.admin.base.AdminBasicController;
import cn.huace.shop.device.entity.Device;
import cn.huace.shop.device.service.DeviceService;
import cn.huace.shop.shop.entity.Shop;
import cn.huace.shop.shop.service.ShopService;
import cn.huace.sys.bean.ShiroUser;
import cn.huace.sys.entity.RegionArea;
import cn.huace.sys.entity.RegionCity;
import cn.huace.sys.entity.RegionProvince;
import cn.huace.sys.entity.SystemUser;
import cn.huace.sys.service.RegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by huangdan on 2016/12/27.
 */
@Slf4j
@RestController
@Api(value = "/admin/device",description = "设备管理")
@RequestMapping(value = "/admin/device")
public class DeviceController extends AdminBasicController
{
    
    @Autowired
    private DeviceService deviceService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private RedisService redisService;

    @RequestMapping(method=RequestMethod.GET)
    public HttpResult list(
            Integer shopId,String devId,String currentVersion,
            @RequestParam(defaultValue="1") Integer page,
            @RequestParam(defaultValue="10") Integer rows
    ) {
        Map<String, Object> searchParams = new HashedMap();
        if(!StringUtils.isBlank(devId)){
            searchParams.put("LIKE_devId",devId);
        }
        if(org.springframework.util.StringUtils.isEmpty(shopId)){
            ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
            SystemUser systemUser = (SystemUser) redisService.getObjectCacheValue("cn.huace.sys.systemUser:"+user.getAccount(),3);
            if(systemUser.getType() != 0 ){
                String shopIds = systemUser.getShopIds();
                String [] shops = shopIds.split(",");
                if(shopIds.split(",").length == 1){
                    searchParams.put("EQ_shop.id",Integer.parseInt(shopIds.trim()));
                }else{
                    searchParams.put("INI_shop.id",StringToInt(shops));
                }
            }
        }else{
            searchParams.put("EQ_shop.id",shopId);
        }


//        if(shopId!=null){
//            searchParams.put("EQ_shop.id",shopId);
//        }
        if(!StringUtils.isEmpty(currentVersion)){
            searchParams.put("EQ_currentVersion",currentVersion);
        }
        Page<Device> pageResult=deviceService.findAll(searchParams,page,rows);
        return HttpResult.createSuccess(pageResult);
    }

    @ApiOperation(value = "保存设备", notes = "保存设备")
    @RequestMapping(value="save",method=RequestMethod.POST)
    public HttpResult save(Device entity,Integer shopId){
        Shop shop=  shopService.findOne(shopId);
        if(entity.isNew()){
            entity.setShop(shop);
            deviceService.save(entity);
        }else{
            Device old=  deviceService.findOne(entity.getId());
            old.setName(entity.getName());
            old.setDevId(entity.getDevId());
            old.setShop(shop);
            deviceService.save(old);
        }
        return HttpResult.createSuccess("保存成功！");
    }

    @ApiOperation(value = "删除设备", notes = "删除设备")
    @RequestMapping(value="del",method=RequestMethod.POST)
    public HttpResult del(Integer id){
        deviceService.delete(id);
        return HttpResult.createSuccess("删除成功！");
    }

    @ApiOperation(value = "获取设备", notes = "获取设备")
    @RequestMapping(value="find",method=RequestMethod.GET)
    public HttpResult find(Integer id){
        Device entity =deviceService.findOne(id);
        return HttpResult.createSuccess(entity);
    }


    public List<Integer> StringToInt(String[] arrs){
        List<Integer> list = new ArrayList<>();
        for(int i=0;i<arrs.length;i++){
            list.add(Integer.parseInt(arrs[i]));
        }
        return list;
    }
}
