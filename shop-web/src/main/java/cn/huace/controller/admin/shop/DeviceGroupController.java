package cn.huace.controller.admin.shop;

import cn.huace.common.bean.HttpResult;
import cn.huace.controller.admin.base.AdminBasicController;
import cn.huace.shop.app.vo.AppVo;
import cn.huace.shop.device.Vo.DeviceGroupVo;
import cn.huace.shop.device.Vo.DeviceVo;
import cn.huace.shop.device.service.DeviceGroupService;
import cn.huace.sys.bean.ShiroUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备分组 控制层
 */

@Slf4j
@RestController
@Api(value = "/admin/deviceGroup",description = "设备分组管理")
@RequestMapping(value = "/admin/deviceGroup")
public class DeviceGroupController extends AdminBasicController{
    @Autowired
    private DeviceGroupService deviceGroupService;

    @ApiOperation(value = "查询分组设备", notes = "查询分组设备")
    @RequestMapping(value="findDevice",method=RequestMethod.POST)
    public HttpResult findDeviceByGroupId(Integer groupId){
        log.info("*** 查询分组设备 start: findDeviceByGroupId()，入参 groupId：{}",groupId);
        List<DeviceVo> list = deviceGroupService.findDeviceByGroupId(groupId);
        return HttpResult.createSuccess(list);
    }

//    @ApiOperation(value = "查询可选设备", notes = "查询可选设备（废弃）")
//    @RequestMapping(value="findUnUsedDevice",method=RequestMethod.POST)
//    public HttpResult findUnUsedDevice(Integer shopId){
//        log.info("*** 查询可选设备 start: findUnUsedDevice()，入参 shopId：{}",shopId);
//        List<DeviceVo> list = deviceGroupService.findUnUsedDevice(shopId);
//        return HttpResult.createSuccess(list);
//    }

//    @ApiOperation(value = "查询分组已选和未选设备", notes = "查询分组已选和未选设备(废弃)")
//    @RequestMapping(value="findSelOrUnselDevice",method=RequestMethod.POST)
//    public HttpResult findSelOrUnselDevice(Integer shopId,Integer groupId){
//        log.info("*** 查询分组已选和未选设备 start: findSelOrUnselDevice()，入参 shopId:{},groupId：{}",shopId,groupId);
//        List<DeviceVo> selectedDevice = deviceGroupService.findDeviceByGroupId(groupId);
//        List<DeviceVo> unSelectedDevice = deviceGroupService.findUnUsedDevice(shopId);
//        //封装返回数据
//        Map<String,Object> resultMap = new HashMap<>();
//        resultMap.put("unSelectedDevice",unSelectedDevice);
//        resultMap.put("selectedDevice",selectedDevice);
//
//        return HttpResult.createSuccess("查询成功！",resultMap);
//    }

    @ApiOperation(value = "查询分组已选和未选设备", notes = "查询分组已选和未选设备")
    @RequestMapping(value="findSelOrUnselDevice",method=RequestMethod.POST)
    public HttpResult findSelOrUnselDevice(Integer shopId,Integer groupId){
        log.info("*** 查询分组已选和未选设备 start: findSelOrUnselDevice()，入参 shopId:{},groupId：{}",shopId,groupId);
        List<DeviceVo> selectedDevice = deviceGroupService.findDeviceByGroupId(groupId);
        List<DeviceVo> unSelectedDevice = deviceGroupService.findUnUsedDevice(shopId,groupId);
        //封装返回数据
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("unSelectedDevice",unSelectedDevice);
        resultMap.put("selectedDevice",selectedDevice);

        return HttpResult.createSuccess("查询成功！",resultMap);
    }

    @ApiOperation(value = "查询分组APP",notes = "查询分组APP")
    @RequestMapping(value = "findApp",method = RequestMethod.POST)
    public HttpResult findAppByGroupId(Integer groupId){
        log.info("*** 查询分组APP start: findApp()，入参 groupId：{}",groupId);
        AppVo appVo = deviceGroupService.findAppByGroupId(groupId);
        return HttpResult.createSuccess(appVo);
    }

    @ApiOperation(value = "商店下所有的设备分组信息",notes = "商店下所有的设备分组信息")
    @RequestMapping(value = "findGroup",method = RequestMethod.POST)
    public HttpResult findGroup(Integer shopId,
                                String name,
                                String devId,
                                String appName,
                                @RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "20") Integer rows){
       log.info("*** 商店下所有的设备分组信息 start: findGroup()，入参 shopId：{},name:{},devId:{},page:{},rows:{}",shopId,name,devId,page,rows);
        Page<DeviceGroupVo> pageList = deviceGroupService.findGroup(shopId, name ,devId,appName, page, rows);
       return HttpResult.createSuccess(pageList);
    }

    @ApiOperation(value = "查询分组信息",notes = "查询分组信息")
    @RequestMapping(value = "findOne",method = RequestMethod.POST)
    public HttpResult findOne(Integer id){
        log.info("*** 查询分组信息 start: findOne()，入参 id：{}",id);
        DeviceGroupVo deviceGroupVo = deviceGroupService.findById(id);
        if(deviceGroupVo == null){
            return HttpResult.createFAIL("未找到对应的数据，请检查参数后，重试！");
        }
        return HttpResult.createSuccess(deviceGroupVo);
    }

    @ApiOperation(value = "保存分组信息",notes = "保存分组信息")
    @RequestMapping(value = "save",method = RequestMethod.POST)
    public HttpResult save(DeviceGroupVo deviceGroupVo){
        log.info("*** 保存分组信息 start: save()，入参 deviceGroupVo：{}",JSONObject.fromObject(deviceGroupVo).toString());
        Integer appListVoId;
        if(deviceGroupVo.getAppListVo() != null && deviceGroupVo.getAppListVo().getId() != null){
            appListVoId = deviceGroupVo.getAppListVo().getId();
        }else {
            appListVoId = deviceGroupVo.getAppListVoId();
        }
        //检查分组名称是否存在
        if(deviceGroupService.checkDuplicateName(appListVoId,deviceGroupVo.getName(),deviceGroupVo.getId())){
            return HttpResult.createFAIL("分组名称已存在！");
        }

        ShiroUser user = getCurrentUser();
        if(StringUtils.isBlank(deviceGroupVo.getCreator())){
            deviceGroupVo.setCreator(user.getAccount());
        }
        if(StringUtils.isBlank(deviceGroupVo.getModifier())){
            deviceGroupVo.setModifier(user.getAccount());
        }
        //默认有效数据
        deviceGroupVo.setIsEnabled(null!= deviceGroupVo.getIsEnabled()?deviceGroupVo.getIsEnabled():true);

        if(deviceGroupService.saveVo(deviceGroupVo)){
            return HttpResult.createSuccess("分组保存成功！");
        }
        return HttpResult.createFAIL("分组保存失败！");
    }

    @ApiOperation(value = "删除分组信息",notes = "删除分组信息")
    @RequestMapping(value = "delete",method = RequestMethod.POST)
    public HttpResult delete(Integer id){
        log.info("*** 删除分组信息 start: delete()，入参 id：{}",id);
        if(deviceGroupService.deleteById(id)){
            return HttpResult.createSuccess("分组删除成功！");
        }
        return HttpResult.createFAIL("分组删除失败！");
    }

    @ApiOperation(value = "保存分组的设备信息",notes = "保存分组的设备信息")
    @RequestMapping(value = "saveDevicesRel",method = RequestMethod.POST)
    public HttpResult saveDevicesRel(Integer id,Integer[] devIds ){
        log.info("*** 保存分组的设备信息 start: saveDevicesRel()，入参 id:{}，devIds:{}",id,devIds);
        if(deviceGroupService.saveDevicesRel(id,devIds)){
            return HttpResult.createSuccess("保存分组的设备信息成功！");
        }
        return HttpResult.createFAIL("保存分组的设备信息成功！");
    }

}
