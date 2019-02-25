package cn.huace.ad.repository;

import cn.huace.ad.entity.AdV2;
import cn.huace.common.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 *
 * Date:2018/1/22
 */
public interface AdV2Repository extends BaseRepository<AdV2,Integer>{

    @Query("select ad from AdV2 ad where ad.shop.id = ?1 and ad.name = ?2 and ad.status <> '3'")
    AdV2 findByShopAndName(Integer shopId,String name);

    @Query("select ad from AdV2 ad where ad.id in ?1")
    Page<AdV2> findAdsByAdIds(Integer[] adIdArr, Pageable pageable);

    @Query("select ad from AdV2 ad where ad.shop.id = ?1 and ad.audit <> 1")
    Page<AdV2> listToAuditOrFailureAuditAds(Integer shopId, Pageable pageable);

    @Query("select ad from AdV2 ad where ad.shop.id = ?1 and ad.audit <> 1 and ad.name like concat('%',?2,'%')")
    Page<AdV2> searchToAuditOrFailureAuditAds(Integer shopId, String adName,Pageable pageable);

    @Query("select ad from AdV2 ad where ad.shop.id = ?1 and ad.type.id in (?2) and ad.flag = true and ad.status <> 3")
    List<AdV2> findInternalAdV2ByAdTypeIds(Integer shopId,Integer[] typeIds);

    @Query("select ad.id from AdV2 ad where ad.group.id = ?1")
    List<Integer> findAdsByGroupId(Integer groupId);

    @Query("select ad from AdV2 ad where ad.id in ?1")
    List<AdV2> findByAdIds(Integer[] adIdArr);
}
