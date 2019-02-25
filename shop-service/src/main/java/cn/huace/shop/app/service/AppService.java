package cn.huace.shop.app.service;

import cn.huace.common.service.BaseService;
import cn.huace.shop.app.entity.App;
import cn.huace.shop.app.vo.AppVo;
import cn.huace.shop.device.Vo.DeviceGroupVo;
import cn.huace.shop.device.entity.DeviceGroup;
import cn.huace.shop.device.service.DeviceGroupService;
import cn.huace.shop.shop.entity.Shop;
import cn.huace.shop.shop.service.ShopService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by huangdan on 2016/12/27.
 */
@Service
public class AppService extends BaseService<App,Integer>
{
    @Autowired
    private DeviceGroupService deviceGroupService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private AppListService appListService;

    public App findLatestVersion(int shopId,String packageName,Integer versionCode) {
        Map<String, Object> searchParams = new HashedMap();
        searchParams.put("EQ_shop.id",shopId);
        searchParams.put("EQ_useFlag",true);
        searchParams.put("EQ_packageName",packageName);
//        searchParams.put("GT_versionCode",versionCode);

        Sort sort = new Sort(Sort.Direction.DESC, "versionCode");
        List<App> apps=findAll(searchParams,sort);
        if(apps.size()>0){
            return apps.get(0);
        }else{
            return null;
        }
    }

    public Map<String,Object> findGroup(Integer shopId,Integer appId,Integer appListVoId){
        //封装返回数据
        Map<String,Object> resultMap = new HashMap<>();
        List<DeviceGroupVo> allDeviceGroup ;
        allDeviceGroup = deviceGroupService.findGroup(shopId,appListVoId);
        Set<DeviceGroup> selectDeviceGroup = null;
        if(appId != null){
            selectDeviceGroup = findOne(appId).getDeviceGroupSet();
        }
        resultMap.put("allDeviceGroup",allDeviceGroup);
        resultMap.put("selectDeviceGroup",selectDeviceGroup);
        return resultMap;
    }

    /**
     * 保存app分组信息
     * @param id
     * @param groupIds
     * @return
     */
    public Boolean saveAppGroupRel(Integer id,Integer [] groupIds){
        App po = findOne(id);
        if(po == null){
            return false;
        }
        List<DeviceGroup> list = new ArrayList<DeviceGroup>();
        if(groupIds != null && groupIds.length>0){
            list = deviceGroupService.findAll(Arrays.asList(groupIds));
        }
        po.setDeviceGroupSet(new HashSet<DeviceGroup>(list));
        return save(po)!=null ? true : false;
    }

    /**
     * 保存方法
     * @param vo
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean saveApp(AppVo vo,Integer [] groupIds){
        App app = save(VoToPo(vo,new App()));
        if(app == null){
            return false;
        }
        return saveAppGroupRel(app.getId(), groupIds);
    }

    /**
     * 查询默认全部投放的最新app
     * @param shopId
     * @param packageName
     * @return
     */
    public App findNewDefalueApp(Integer shopId,String packageName){
        Map<String, Object> searchParams = new HashedMap();
        searchParams.put("EQ_shop.id",shopId);
        searchParams.put("EQ_useFlag",true);
        searchParams.put("EQ_packageName",packageName);
        searchParams.put("EQ_deliverScope",-1);//

        Sort sort = new Sort(Sort.Direction.DESC, "versionCode");
        List<App> apps=findAll(searchParams,sort);
        if(apps.size()>0){
            return apps.get(0);
        }else{
            return null;
        }
    }

    /**
     * vo to po
     * @param source
     * @param target
     * @return
     */
    public App VoToPo(AppVo source,App target){
        if(source.getId() != null){
            target = findOne(source.getId());
        }else {
            target.setShop(shopService.findOne(source.getShopId()));
        }
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
}
