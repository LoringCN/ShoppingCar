package cn.huace.ad.repository;

import cn.huace.ad.entity.AdTypeV2;
import cn.huace.common.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Date:2018/1/26
 */
public interface AdTypeV2Repository extends BaseRepository<AdTypeV2,Integer>{

    @Query("select id from AdTypeV2 where flag = true")
    List<Integer> findAllAdTypeV2Ids();
}
