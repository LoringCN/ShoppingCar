package cn.huace.ads.service;

import cn.huace.ads.entity.Ads;
import cn.huace.ads.entity.AdsAuditTrack;
import cn.huace.ads.repository.AdsAuditTrackRepository;
import cn.huace.common.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 广告审核轨迹 服务层 created by 2018-05-29
 * @author Loring
 */
@Service
public class AdsAuditTrackService extends BaseService<AdsAuditTrack,Integer> {

    @Autowired
    private AdsAuditTrackRepository adsAuditTrackRepository;

    /**
     * 查询版本最大的任务记录
     * @param taskId
     * @return
     */
    public AdsAuditTrack findByMaxTaskId(Integer taskId){
      return adsAuditTrackRepository.findByMaxTaskId(taskId);
    }


}
