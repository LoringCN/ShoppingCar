package cn.huace.shop.device.service;


import cn.huace.common.service.BaseService;
import cn.huace.common.utils.Reflections;
import cn.huace.shop.app.entity.App;
import cn.huace.shop.app.entity.AppList;
import cn.huace.shop.app.service.AppListService;
import cn.huace.shop.app.service.AppService;
import cn.huace.shop.app.vo.AppListVo;
import cn.huace.shop.app.vo.AppVo;
import cn.huace.shop.device.Vo.DeviceGroupVo;
import cn.huace.shop.device.Vo.DeviceVo;
import cn.huace.shop.device.entity.Device;
import cn.huace.shop.device.entity.DeviceGroup;
import cn.huace.shop.device.repository.DeviceGroupRepository;
import cn.huace.shop.shop.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.ResultTransformer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 设备分组 服务层
 * created by Loring on 2018-06-21
 */
@Service
@Slf4j
public class DeviceGroupService extends BaseService<DeviceGroup, Integer> {
    @Autowired
    private DeviceGroupRepository deviceGroupRepository;
    @Autowired
    private DeviceService deviceService;

    @Autowired
    private AppService appService;

    @Autowired
    private AppListService appListService;

    /**
     *  根据设备分组类的设备
     * @param id
     */
    public List<DeviceVo> findDeviceByGroupId(Integer id){
        List<Device> list =   deviceGroupRepository.findDeviceByGroupId(id);
        if(list.isEmpty()){
            return null;
        }
        return list.stream().map(device -> PoToVo(device,new DeviceVo())).collect(Collectors.toList());
    }

    /**
     * 查询商店下设备分组可选设备
     * @param shopId
     * @return
     */
    public List<DeviceVo> findUnUsedDevice(Integer shopId,Integer groupId){
        DeviceGroup deviceGroup = findOne(groupId);
        List<Object> list2 = deviceGroupRepository.findUnUsedDevice(shopId,deviceGroup.getAppList().getId());
        if(list2.isEmpty()){
            return null;
        }
        return list2.stream().map(object -> (DeviceVo) new AliasToBeanResultTransformer(DeviceVo.class).transformTuple((Object[]) object,Reflections.getFiledName(new DeviceVo()))).collect(Collectors.toList());
    }

    /**
     * 查询分组对应的APP
     * @param id
     * @return
     */
    public AppVo findAppByGroupId(Integer id){
        Object a = deviceGroupRepository.findAppByGroupId(id);
        if(a == null){
            return  null;
        }
        ResultTransformer rtf = new AliasToBeanResultTransformer(AppVo.class);
        AppVo vo = (AppVo)rtf.transformTuple((Object[]) a,Reflections.getFiledName(new AppVo()));
        return vo;
    }
    /**
     * 查询当前商店下所有的分组信息
     * @param shopId
     * @return
     */
    public Page<DeviceGroupVo> findGroup(Integer shopId,String name,String devId,String appName, Integer page, Integer rows){
        List <DeviceGroup> list ;
        List<DeviceGroupVo> list2 = new ArrayList<>();
        //根据设备号查询分组
        if(StringUtils.isNotBlank(devId)){
            list2 = findGroupByDevId(shopId, devId);
            if(list2 == null){
                return null;
            }
            return new PageImpl<>(list2,null,list2.size());
        }

//        if (StringUtils.isBlank(name)) {
//            //查询所有分组
//            list = deviceGroupRepository.findGroup(shopId);
//        } else {
//            //根据name查询分组
//            list = deviceGroupRepository.findGroup(shopId, name);
//        }
        Map<String,Object> searchMap = new HashMap<>();
        if(shopId != null){
            searchMap.put("EQ_shopId",shopId);
        }
        if(StringUtils.isNotBlank(name)){
            searchMap.put("LIKE_name",name);
        }
        if(StringUtils.isNotBlank(appName)){
            searchMap.put("LIKE_appList.name",appName);
        }
        Page<DeviceGroup> poPage = findAll(searchMap,page,rows,Sort.Direction.DESC,"id");
        list = poPage.getContent();
        list2 = list.stream().map(deviceGroup -> PoToVo(deviceGroup,new DeviceGroupVo())).collect(Collectors.toList());
        return new PageImpl<>(list2,null,poPage.getTotalElements());
    }

    /**
     * 查询当前商店下所有的分组信息
     * @param shopId
     * @return
     */
    public List<DeviceGroupVo> findGroup(Integer shopId,Integer appListVoId){
        List <DeviceGroup> list = deviceGroupRepository.findGroup(shopId,appListVoId);
        if(list.isEmpty()){
            return null;
        }
        return list.stream().map(deviceGroup -> PoToVo(deviceGroup,new DeviceGroupVo())).collect(Collectors.toList());
    }

    /**
     * 查询可选分组
     * @param shopId
     * @return
     */
    public List<DeviceGroupVo> findUnUsedGroup(Integer shopId){
        List<Object> list2 = deviceGroupRepository.findUnUsedGroup(shopId);
        if(list2.isEmpty()){
            return null;
        }
        return list2.stream().map(object -> (DeviceGroupVo) new AliasToBeanResultTransformer(DeviceGroupVo.class).transformTuple((Object[]) object,Reflections.getFiledName(new DeviceGroupVo()))).collect(Collectors.toList());

    }

    /**
     * 查询设备分组信息
     * @param id
     * @return
     */
    public DeviceGroupVo findById(Integer id){
        DeviceGroup deviceGroup = findOne(id);
        if(null == deviceGroup){
            return null;
        }
        return PoToVo(deviceGroup,new DeviceGroupVo());
    }

    /**
     * 保存方法
     * @param vo
     * @return
     */
    public Boolean saveVo(DeviceGroupVo vo){
        DeviceGroup po = new DeviceGroup();
        if(vo.getId()!=null){
            po = findOne(vo.getId());
        }
        DeviceGroup deviceGroup =  save(VoToPo(vo,po));
        if(deviceGroup != null){
            return true;
        }
        return false;
    }

    /**
     * 删除分组
     * @param id
     * @return
     */
    @Transactional
    public Boolean deleteById(Integer id){
        return deviceGroupRepository.deleteById(id)>0?true:false;
    }

    /**
     * 保存分组设备信息
     * @param id
     * @param devIds
     * @return
     */
    public Boolean saveDevicesRel(Integer id ,Integer [] devIds){
        DeviceGroup po = findOne(id);
        if(po == null){
            return false;
        }
        List<Device> list = deviceService.findAll(Arrays.asList(devIds));
        po.setDeviceSet(new HashSet<Device>(list));
        return save(po) != null ? true:false;
    }

    /**
     * 获取设备最新发布的版本app (api-web调用，请勿擅自更改)
     * @param shopId
     * @param devId
     * @param packageName
     * @return
     */
    public App findAppByDevId(Integer shopId,String devId,String packageName){
        /*1、查询指定投放的最新app*/
        App app = null;
        Object a = deviceGroupRepository.findAppBydevId(shopId, devId,packageName);
        if(a != null){
            ResultTransformer rtf = new AliasToBeanResultTransformer(AppVo.class);
            AppVo vo = (AppVo)rtf.transformTuple((Object[]) a,Reflections.getFiledName(new AppVo(),"appListVo","deliverScope"));
            app = appService.VoToPo(vo,new App());
//            log.info("*** app check(),指定投放的最新app={}",app.toString());
        }
        /*2、查询默认投放的最新app */
        App dfApp = appService.findNewDefalueApp(shopId,packageName);
        if(app == null || (dfApp != null && dfApp.getVersionCode() > app.getVersionCode()) ){
            app = dfApp;
//            log.info("*** app check(),默认投放的最新app={}",app.toString());
        }
        return app;
    }


    /**
     * 根据设备号查询对应分组
     * @param shopId
     * @param devId
     * @return
     */
    public List<DeviceGroupVo> findGroupByDevId(Integer shopId,String devId){
        List<Object> retList = deviceGroupRepository.findGroupByDevId(shopId, devId);
        if(retList == null || retList.size() == 0){
            return null;
        }
        ResultTransformer rtf = new AliasToBeanResultTransformer(DeviceGroupVo.class);
        return retList.stream().map(object ->VoTrans((DeviceGroupVo) new AliasToBeanResultTransformer(DeviceGroupVo.class).transformTuple((Object[]) object,Reflections.getFiledName(new DeviceGroupVo(),"appListVo","percent")))).collect(Collectors.toList());
    }

    /**
     *  检查name是否存在
     * @param appListId
     * @param name
     * @return
     */
    public Boolean checkDuplicateName(Integer appListId , String name , Integer id){
        return  deviceGroupRepository.checkDuplicateName(appListId, name, id)>0?true:false;
    }

    /**
     * VO to PO
     * @param source
     * @param target
     * @return
     */
    public DeviceGroup VoToPo(DeviceGroupVo source, DeviceGroup target){
        BeanUtils.copyProperties(source,target);
        Integer appListVoId;
        if(source.getAppListVo() != null && source.getAppListVo().getId() != null){
            appListVoId = source.getAppListVo().getId();
        }else {
            appListVoId = source.getAppListVoId();
        }
        target.setAppList(appListService.findOne(appListVoId));
        return target;
    }

    /**
     * PO to VO
     * @param source
     * @param target
     * @return
     */
    public DeviceGroupVo PoToVo(DeviceGroup source,DeviceGroupVo target){
        BeanUtils.copyProperties(source,target);
        target.setId(source.getId());
        target.setAppListVo(appListService.PoToVo(source.getAppList(),new AppListVo()));
        Integer selnum = source.getDeviceSet().size();
        Integer allnum = deviceService.findAllDevByShopId(source.getShopId()).size();

        target.setPercent(new BigDecimal(selnum).divide( new BigDecimal(allnum),2, RoundingMode.HALF_UP));
        return target;
    }

    /**
     * PO to VO
     * @param source
     * @param target
     * @return
     */
    public DeviceVo PoToVo(Device source, DeviceVo target){
        BeanUtils.copyProperties(source,target);
        target.setId(source.getId());
        target.setShopId(source.getShop().getId());
        return target;
    }

    /**
     * 大类转换 && 计算百分比
     * @param source
     * @return
     */
    public DeviceGroupVo VoTrans(DeviceGroupVo source){
        AppList appList = appListService.findOne(source.getAppListVoId());
        AppListVo appListVo = appListService.PoToVo(appList,new AppListVo());
        source.setAppListVo(appListVo);
        Integer selnum = findDeviceByGroupId(source.getId()).size();
        Integer allnum = deviceService.findAllDevByShopId(source.getShopId()).size();
        source.setPercent(new BigDecimal(selnum).divide( new BigDecimal(allnum),2, RoundingMode.HALF_UP));
        return source;
    }
}
