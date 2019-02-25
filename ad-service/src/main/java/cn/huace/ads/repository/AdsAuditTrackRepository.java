package cn.huace.ads.repository;

import cn.huace.ads.entity.AdsAuditTrack;
import cn.huace.common.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * 广告审核轨迹 dao层 created on 2018-05-29
 * @author Loring
 */
public interface AdsAuditTrackRepository extends BaseRepository<AdsAuditTrack,Integer> {
    /**
     * 查询最大版本号的任务记录
     * @param taskId
     * @return
     */
    @Query("select t from AdsAuditTrack t where t.adsAuditTask.id = ?1 and t.versionNo = (select max(tt.versionNo) from AdsAuditTrack tt where tt.adsAuditTask.id = ?1 )")
    AdsAuditTrack findByMaxTaskId(Integer taskId);
}
