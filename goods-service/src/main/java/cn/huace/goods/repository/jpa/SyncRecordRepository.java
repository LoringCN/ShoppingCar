package cn.huace.goods.repository.jpa;

import cn.huace.common.repository.BaseRepository;
import cn.huace.goods.entity.SyncRecord;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 *
 * Created by yld on 2017/8/21.
 */
public interface SyncRecordRepository extends BaseRepository<SyncRecord,Integer>{
    /**
     * 查询最近一次同步成功时间
     */
    @Query("select max(sr.syncTime) from SyncRecord sr where sr.type = ?1 and sr.shopName = ?2 and sr.syncState = ?3")
    Date findRecentSyncTime(String type,String shopName,Integer state);

    /**
     * 查询最近一次同步成功时间
     */
    @Query("select min(sr.syncTime) from SyncRecord sr where sr.type = ?1 and sr.shopName = ?2 and sr.syncState = ?3")
    Date findFirstSyncTime(String type,String shopName,Integer state);

}
