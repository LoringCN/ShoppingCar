package cn.huace.ad.repository;

import cn.huace.ad.entity.AdGroupV2;
import cn.huace.common.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * Date:2018/2/28
 */
public interface AdGroupV2Repository extends BaseRepository<AdGroupV2,Integer>{

    @Query("select g from AdGroupV2 g where g.adGroupName = ?1")
    AdGroupV2 findByAdGroupName(String adGroupName);
}
