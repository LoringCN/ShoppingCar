package cn.huace.ads.repository;

import cn.huace.ads.entity.Ads;
import cn.huace.ads.entity.AdsOnline;
import cn.huace.common.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 *  线上广告 dao 层 created by Loring on 2018-05-30
 */
public interface AdsOnlineRepository extends BaseRepository<AdsOnline,Integer> {
    /**
     * 查询广告的投放设备，默认广告返回空
     * @param shopId
     * @param adId
     * @return
     */
    @Query(value = "select DISTINCT t.dev_id from ads_online t where t.shop_id = ?1 and t.ad_id = ?2",nativeQuery = true)
    List<String> findDevId(Integer shopId, Integer adId);

    /**
     * 删除线上广告
     * @param adId
     * @return
     */
    @Modifying
    @Query(value = "delete from ads_online where ad_id = ?1",nativeQuery = true)
    Integer deleteByadId(Integer adId);

    /**
     * 查询指定设备投放的广告
     * @param shopId
     * @param adType
     * @param devid
     * @return
     */
    @Query(value = "select t from AdsOnline t where t.shopId = ?1 and t.type = ?2 and t.devId = ?3 and t.deliverScope = 1 order by t.modifiedTime desc")
    List<AdsOnline> findByDevId(Integer shopId,Integer adType,String devid);

    /**
     * 查询默认投放的广告
     * @param shopId
     * @param adType
     * @return
     */
    @Query(value = "select t from AdsOnline t where t.shopId = ?1 and t.type = ?2 and t.deliverScope = -1 order by t.modifiedTime desc")
    List<AdsOnline> findByDefault(Integer shopId,Integer adType);

    /**
     * 查询轮播线上广告
     * @param shopId
     * @param adType
     * @param devid
     * @return
     */
    @Query(value = "select t from AdsOnline t where t.shopId = ?1 and t.type = ?2 and (t.devId = ?3 or t.deliverScope = -1) order by t.rank asc,t.deliverScope desc ,t.modifiedTime desc")
    List<AdsOnline> findLBByDevId(Integer shopId,Integer adType,String devid);

    /**
     * 查询线上广告的广告id
     * @param shopId
     * @param type
     * @param rank
     * @return
     */
    @Query(value = "select distinct t.ad_id from ads_online t where t.shop_id = ?1 and t.type = ?2 and t.rank = ?3 and t.is_enabled = 1",nativeQuery = true)
    List<Integer> findAdId(Integer shopId,Integer type,Integer rank);

    /**
     * 查询线上广告
     * @param shopId
     * @param type
     * @param rank
     * @return
     */
    @Query(value = "select a from Ads a where  a.name like concat('%',?3,'%') and exists (select 1 from AdsOnline o where o.ads.id = a.id and o.shopId = ?1 and o.type = ?2 and o.rank = ?4 and o.isEnabled = true ) order by modifiedTime desc ")
    List<Ads> listNew(Integer shopId,Integer type,String name,Integer rank);

}
