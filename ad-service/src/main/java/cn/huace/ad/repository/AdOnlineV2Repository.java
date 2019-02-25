package cn.huace.ad.repository;

import cn.huace.ad.entity.AdOnlineV2;
import cn.huace.common.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 线上广告
 * Date:2018/1/23
 */
public interface AdOnlineV2Repository extends BaseRepository<AdOnlineV2,Integer> {

    @Query(value = "select DISTINCT dev_id from ad_online_v2 where shop_id = ?1 and ad_id = ?2",nativeQuery = true)
    List<Integer> findDevIdsDevIdsByShopIdAndAdId(Integer shopId, Integer adId);

    @Query("select ao from AdOnlineV2 ao where ao.adId in (?1)")
    List<AdOnlineV2> findAllOnlineAdsInAdIds(Integer[] adIdArr);

    @Query(value = "select DISTINCT ad_id from ad_online_v2 where shop_id = ?1 and type_id = ?2 and rank = ?3",nativeQuery = true)
    List<Integer> findAdIdsByShopIdAndRank(Integer shopId, Integer typeId,Integer rank);

    @Query("select ao from AdOnlineV2 ao where shopId = ?2 and devId = ?1")
    List<AdOnlineV2> findAdOnlineV2ByDevId(Integer devId, Integer shopId);

    @Query("select distinct adId from AdOnlineV2 where shopId = ?1 and typeId = ?2")
    List<Integer> findAdIdsByShopIdAndTypeId(Integer shopId, Integer adTypeId);
}
