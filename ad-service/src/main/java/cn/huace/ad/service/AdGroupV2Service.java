package cn.huace.ad.service;

import cn.huace.ad.entity.AdGroupV2;
import cn.huace.ad.repository.AdGroupV2Repository;
import cn.huace.common.service.BaseService;
import cn.huace.common.utils.jpa.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 *
 * Date:2018/2/28
 */
@Slf4j
@Service
public class AdGroupV2Service extends BaseService<AdGroupV2,Integer> {

    @Autowired
    private AdGroupV2Repository adGroupV2Repository;

    public Boolean existAdGroupName(String adGroupName) {
        return adGroupV2Repository.findByAdGroupName(adGroupName) != null;
    }

    public Page<AdGroupV2> listAdGroupV2Pages(Map<String,Object> searchMap, Integer page, Integer rows) {
        PageRequest pageRequest = PageUtil.buildPageRequest(page,rows, Sort.Direction.DESC,"createdTime");
        return findAll(searchMap,pageRequest);
    }
}
