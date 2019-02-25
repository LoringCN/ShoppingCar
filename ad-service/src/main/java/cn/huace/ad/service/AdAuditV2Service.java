package cn.huace.ad.service;

import cn.huace.ad.entity.AdAuditV2;
import cn.huace.ad.util.AdCodeConstants;
import cn.huace.common.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Date:2018/1/24
 */
@Slf4j
@Service
public class AdAuditV2Service extends BaseService<AdAuditV2,Integer>{

    /**
     * 查询审核失败原因
     * @param adId
     * @return
     */
    public List<AdAuditV2> findAuditFailureList(Integer adId) {
        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_adId",adId);
        searchMap.put("EQ_status", AdCodeConstants.AuditStatus.FAILURE_AUDIT);
        //创建时间倒序
        Sort sort = new Sort(Sort.Direction.DESC,"modifiedTime");
        return findAll(searchMap,sort);
    }
}
