package cn.huace.ad.service;

import cn.huace.ad.entity.AdTypeV2;
import cn.huace.ad.repository.AdTypeV2Repository;
import cn.huace.common.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * Date:2018/1/28
 */
@Slf4j
@Service
public class AdTypeV2Service extends BaseService<AdTypeV2,Integer>{

    @Autowired
    private AdTypeV2Repository adTypeV2Repository;

    /**
     * 查询所有adTypeId
     * @return
     */
    public List<Integer> findAllAdTypeV2Ids() {
        return adTypeV2Repository.findAllAdTypeV2Ids();
    }
}
