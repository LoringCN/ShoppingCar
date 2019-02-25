package cn.huace.ad.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.huace.ad.entity.Ad;
import cn.huace.ad.repository.AdRepository;
import cn.huace.common.service.BaseService;

@Service
public class AdService extends BaseService<Ad, Integer> {
    @Autowired
    private AdRepository adRepository;

    /**
     * 查询超市下所有的广告
     *
     * @param shopId
     * @param validFlag
     * @return
     */
    public List<Ad> findAdListByShopId(Integer shopId, String validFlag) {
        return adRepository.findAdListByShopId(shopId, validFlag);
    }

    public List<Ad> findReccAdList(Integer shopId) {
        return adRepository.findReccAdList(shopId);
    }
}
