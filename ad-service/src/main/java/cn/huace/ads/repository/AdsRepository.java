package cn.huace.ads.repository;

import cn.huace.ads.entity.Ads;
import cn.huace.common.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * 广告 dao层 created on 2018-05-25
 * @author Loring
 */
public interface AdsRepository extends BaseRepository<Ads,Integer> {
    /**
     * 查询广告是否有效
     * @param id
     * @return
     */
    @Query("select t.isEnabled from Ads t where t.id = ?1")
    Boolean findEnabledById(Integer id);

    /**
     * 更新广告有效标志
     * @param isEnabled
     * @param id
     * @return
     */
    @Modifying
    @Query("update Ads t set t.isEnabled = ?1 where t.id = ?2")
    Integer updateEnalbedById(Boolean isEnabled,Integer id);

    /**
     * 更新广告上架标志
     * @param isEnabled
     * @param id
     * @return
     */
    @Modifying
    @Query("update Ads t set t.isShelf = ?1 where t.id = ?2")
    Integer updateShelfById(Integer isEnabled,Integer id);

    /**
     * 更新广告审核状态
     * @param auditStatus
     * @param id
     * @param reason
     * @return
     */
    @Modifying
    @Query("update Ads t set t.auditStatus = ?1 , t.reason = ?3,t.isShelf = ?4 where t.id = ?2")
    Integer updateAuditStatus(Integer auditStatus,Integer id,String reason,Integer shelf);

    /**
     * 查询默认广告
     * @param shopId
     * @param adType
     * @return
     */
    @Query("select t from Ads t where t.shop.id = ?1 and t.type = ?2 and t.isDefalut = true")
    List<Ads> findByDefalut (Integer shopId,Integer adType);

    /**
     * 查询可上线广告（不包含默认广告）
     * @param shopId
     * @param adType
     * @return
     */
    @Query("select t from Ads t where t.shop.id = ?1 and t.type = ?2 and (t.isVoted = false or t.isVoted is null)and (t.isDefalut = false or t.isDefalut is null) and t.isShelf = 1 ")
    List<Ads> findByPreOnline (Integer shopId,Integer adType);

    @Modifying
    @Query(value = "update ads t set t.expiry_time = ?2 where t.id = ?1",nativeQuery = true)
    Integer renewal(Integer adId, Date expiryTime);

}
