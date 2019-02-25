package cn.huace.goods.service;

import cn.huace.common.service.BaseService;
import cn.huace.goods.entity.SyncRecord;
import cn.huace.goods.repository.jpa.SyncRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 *
 * Created by yld on 2017/8/21.
 */
@Slf4j
@Service
public class SyncRecoredService extends BaseService<SyncRecord,Integer>{
    @Autowired
    private SyncRecordRepository recoredRepository;

    /**
     * 查询最近一次同步记录时间
     */
    public Date findRecentSyncTime(String type,String shopName,Integer state){
        return recoredRepository.findRecentSyncTime(type,shopName,state);
    }

    /**
     * 查询第一次同步记录时间
     */
    public Date findFirstSyncTime(String type,String shopName,Integer state){
        return recoredRepository.findFirstSyncTime(type,shopName,state);
    }
}
