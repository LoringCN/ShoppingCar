package cn.huace.ads.repository;

import cn.huace.ads.entity.AdsAuditTask;
import cn.huace.common.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 广告审核 dao层 created on 2018-05-29
 * @author Loring
 */
public interface AdsAuditTaskRepository extends BaseRepository<AdsAuditTask,Integer> {

    @Modifying
    @Query("update AdsAuditTask t set t.auditStatus = ?1 , t.reason = ?3 where t.id = ?2")
    Integer updateAuditStatus(Integer auditStatus,Integer id,String reason);

    @Modifying
    @Query("update AdsAuditTask t set t.isEnabled = false where t.ads.id = ?1 and t.isEnabled =true ")
    Integer deleteByAdId(Integer adId);


}
