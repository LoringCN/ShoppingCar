package cn.huace.shop.device.repository;


import cn.huace.common.repository.BaseRepository;
import cn.huace.shop.device.entity.Device;
import cn.huace.shop.device.entity.DeviceGroup;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 设备分组 dao 层
 * created by Loring on 2018-06-21
 */
public interface DeviceGroupRepository extends BaseRepository<DeviceGroup, Integer> {

    @Query("select m from DeviceGroup t join t.deviceSet m where t.id =?1")
    List<Device> findDeviceByGroupId(Integer groupId);

    @Query(value = "select " +
            "a.id id," +
            "a.created_time createdTime," +
            "a.description description," +
            "a.md5 MD5," +
            "a.name name," +
            "a.package_name packageName," +
            "a.url url," +
            "a.use_flag useFlag," +
            "a.version_code versionCode," +
            "a.shop_id shopId," +
            "a.app_list_id appListId" +
            " from app a where EXISTS (SELECT 1 from app_device_group_rel r WHERE r.app_id = a.id and r.group_id = ?1)",nativeQuery = true)
    Object findAppByGroupId(Integer groupId);

    @Query( value="select t.* from device t where t.shop_id = ?1 and not EXISTS (select 1 from device_group_rel r where r.dev_id = t.id AND EXISTS ( SELECT 1 FROM applist_device_group dg WHERE dg.id = r.group_id AND dg.app_list_id = ?2 ))",nativeQuery = true)
    List<Object> findUnUsedDevice(Integer shopId,Integer appListId);

    @Query(value = "select t from DeviceGroup t where t.shopId = ?1 and t.appList.id = ?2 and t.isEnabled = true")
    List<DeviceGroup> findGroup(Integer shopId,Integer appListVoId);

    @Query(value = "select t.* from applist_device_group t where t.shop_id = ?1 and NOT EXISTS ( select 1 from app_device_group_rel r WHERE r.group_id = t.id )",nativeQuery = true)
    List<Object> findUnUsedGroup(Integer shopId);

    @Query(value = "select t from DeviceGroup t where t.shopId = ?1 and t.name like concat('%',?2,'%') ")
    List<DeviceGroup> findGroup(Integer shopId,String name);

    @Modifying
    @Query(value = "delete from DeviceGroup where id = ?1")
    Integer deleteById(Integer id);

    @Query(value = "SELECT a.id , a.created_time , a.description , a.md5 , a.name , a.package_name , a.url , a.use_flag , a.version_code , a.shop_id ,'',a.app_list_id FROM app a WHERE a.use_flag = 1 and a.shop_id = ?1 and a.package_name = ?3  and EXISTS ( SELECT 1 FROM app_device_group_rel agr WHERE agr.app_id = a.id AND EXISTS ( SELECT 1 FROM device_group_rel dgr WHERE agr.group_id = dgr.group_id AND EXISTS ( SELECT 1 FROM device d WHERE dgr.dev_id = d.id AND d.dev_id = ?2 AND d.shop_id = ?1 ))) order by a.version_code desc limit 1",nativeQuery = true)
    Object findAppBydevId(Integer shopId,String devId,String packageName);

    @Query(value = "SELECT dg.id,dg.shop_id,dg.name,''appListVo,dg.created_time,dg.remark,'' percent,dg.app_list_id FROM applist_device_group dg WHERE EXISTS ( " +
            "SELECT 1 FROM device_group_rel r WHERE r.group_id = dg.id AND EXISTS ( " +
            "SELECT 1 FROM device d WHERE d.dev_id = ?2 AND d.id = r.dev_id AND d.shop_id = ?1 ))",nativeQuery = true)
    List<Object> findGroupByDevId(Integer shopId,String devId);

    @Query(value = "select count(1) from applist_device_group t where t.app_list_id = ?1 and t.name = ?2 and t.id <> ?3",nativeQuery = true)
    Integer checkDuplicateName(Integer appListId,String name,Integer id);
}
