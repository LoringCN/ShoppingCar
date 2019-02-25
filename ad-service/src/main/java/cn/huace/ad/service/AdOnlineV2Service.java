package cn.huace.ad.service;

import cn.huace.ad.entity.AdOnlineV2;
import cn.huace.ad.entity.AdV2;
import cn.huace.ad.repository.AdOnlineV2Repository;
import cn.huace.ad.util.AdCodeConstants;
import cn.huace.common.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Date:2018/1/23
 */
@Slf4j
@Service
public class AdOnlineV2Service extends BaseService<AdOnlineV2,Integer>{

    @Autowired
    private AdOnlineV2Repository adOnlineV2Repository;
    /**
     * 根据广告id和商店id查询上线广告
     * @param adId 广告ID
     * @param shopId 商店Id
     * @return
     */
    public List<AdOnlineV2> findByAdIdAndShopId(Integer adId, Integer shopId) {
        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_adId",adId);
        searchMap.put("EQ_shopId",shopId);
        return findAll(searchMap);
    }

    /**
     * 批量插入线上广告
     * @param onlineAdList
     * @return
     */
    @Transactional
    public Boolean batchSave(List<AdOnlineV2> onlineAdList) {
        int result = batchInsert(onlineAdList);
        return result == onlineAdList.size();
    }

    /**
     * 根据广告ID查询线上广告
     * @param shopId
     * @param adId
     * @return
     */
    public List<AdOnlineV2> findOnlineAdByAdId(Integer shopId, Integer adId) {
        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_shop.id",shopId);
        searchMap.put("EQ_adId",adId);
        return findAll(searchMap);
    }

    /**
     * 批量删除
     * @param oldEntity
     */
    public void batchDelete(List<AdOnlineV2> oldEntity) {
        delete(oldEntity);
    }

    /**
     * 根据商店ID和广告ID查询设备ID
     * @param shopId
     * @param adId
     * @return
     */
    public List<Integer> findDevIdsByShopIdAndAdId(Integer shopId, Integer adId) {
        return adOnlineV2Repository.findDevIdsDevIdsByShopIdAndAdId(shopId,adId);
    }

    /**
     * 根据广告ID集合查询所有上线广告记录
     * @param adIds
     * @return
     */
    public List<AdOnlineV2> findAllOnlineAdsInAdIds(List<Integer> adIds) {
        Integer[] adIdArr = adIds.toArray(new Integer[adIds.size()]);
        return adOnlineV2Repository.findAllOnlineAdsInAdIds(adIdArr);
    }

    /**
     * 根据超市ID和rank查询对应广告id集合
     * @param shopId
     * @param rank
     * @return
     */
    public List<Integer> findAdIdsByShopIdAndRank(Integer shopId, Integer typeId,Integer rank) {
        return adOnlineV2Repository.findAdIdsByShopIdAndRank(shopId,typeId,rank);
    }

    /**
     * 查询线上广告
     * @param devId
     * @param shopId
     * @return
     */
    public List<AdOnlineV2> findAdOnlineV2ByDevId(Integer devId, Integer shopId) {
        return adOnlineV2Repository.findAdOnlineV2ByDevId(devId,shopId);
    }

    /**
     * 根据adTypeId查询线上广告Id
     */
    public List<Integer> findAdIdsByShopIdAndTypeId(Integer shopId, Integer adTypeId) {
        return adOnlineV2Repository.findAdIdsByShopIdAndTypeId(shopId,adTypeId);
    }

    /**
     * 检查该类型广告是否已有默认线上广告
     */
    public Boolean checkDefaultAdOnlineV2(Map<String, Object> searchMap, AdV2 adV2) {
        int typeCode = adV2.getType().getCode();
        // 轮播广告位可以有多个广告
        if (AdCodeConstants.AdV2Type.TYPE_CODE_INDEX_CAROUSEL == typeCode
            || AdCodeConstants.AdV2Type.TYPE_CODE_NEW_RECOMMEND_CAROUSEL == typeCode
            || AdCodeConstants.AdV2Type.TYPE_CODE_LBS == typeCode) {
            return false;
        }
        List<AdOnlineV2> adOnlineV2List = findAll(searchMap);
        return !CollectionUtils.isEmpty(adOnlineV2List);
    }
}
