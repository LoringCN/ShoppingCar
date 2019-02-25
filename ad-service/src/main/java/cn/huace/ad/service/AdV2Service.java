package cn.huace.ad.service;

import cn.huace.ad.entity.AdV2;
import cn.huace.ad.repository.AdV2Repository;
import cn.huace.common.service.BaseService;
import cn.huace.common.utils.jpa.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Date:2018/1/22
 */
@Service
public class AdV2Service extends BaseService<AdV2,Integer>{

    @Autowired
    private AdV2Repository adV2Repository;

    /**
     * 分页查询所有广告
     * @param searchMap
     * @param page
     * @param pageNum
     * @return
     */
    public Page<AdV2> listAds(Map<String, Object> searchMap, Integer page, Integer pageNum) {
        return findAll(searchMap,page,pageNum, Sort.Direction.DESC,"modifiedTime");
    }

    /**
     * 根据广告名判断广告是否存在
     * @param shopId
     * @param name
     * @return
     */
    public Boolean isExist(Integer shopId,String name){
        AdV2 ad = adV2Repository.findByShopAndName(shopId,name);
        return ad != null;
    }

    /**
     * 根据广告类型和商店查询广告
     * @param searchMap
     * @param page
     * @param rows
     * @return
     */
    public Page<AdV2> findAdsByType(Map<String,Object> searchMap, Integer page, Integer rows) {

        return findAll(searchMap,page,rows,Sort.Direction.DESC,"modifiedTime");
    }

    /**
     * 根据广告状态和商店查询广告
     * @param searchMap
     * @param page
     * @param rows
     * @return
     */
    public Page<AdV2> findAdsByStatus(Map<String,Object> searchMap, Integer page, Integer rows) {

        return findAll(searchMap,page,rows,Sort.Direction.DESC,"modifiedTime");
    }

    /**
     * 加载线上广告
     * @param searchMap
     * @param page
     * @param rows
     * @return
     */
    public Page<AdV2> findOnlineAds(Map<String, Object> searchMap, Integer page, Integer rows) {
        return findAll(searchMap,page,rows, Sort.Direction.DESC,"modifiedTime");
    }

    /**
     * 根据查询所有已上架广告
     * @param searchMap
     * @return
     */
    public List<AdV2> findShelfAds(Map<String, Object> searchMap) {
        Sort sort = new Sort(Sort.Direction.DESC,"modifiedTime");
        return findAll(searchMap,sort);
    }

//    跟上面的查询是一样的 就是名字不一样
    public Page<AdV2> listExpireAds(Map<String, Object> searchMap, Integer page, Integer rows){
        return findAll(searchMap,page,rows, Sort.Direction.DESC,"modifiedTime");
    }

    /**
     * 批量修改已过期广告voted状态为下线
     * @param expireAds
     */
    @Transactional
    public Boolean batchUpdateVotedForExpireAd(List<AdV2> expireAds) {
        int result =  batchUpdate(expireAds);
        return result == expireAds.size();
    }

    /**
     * 根据广告Id集合分页查询广告
     * @param adIds
     * @param page
     * @param rows
     * @return
     */
    public Page<AdV2> findAdsByAdIds(List<Integer> adIds, Integer page, Integer rows) {
        Integer[] adIdArr = adIds.toArray(new Integer[adIds.size()]);
        PageRequest pageRequest = PageUtil.buildPageRequest(page,rows, Sort.Direction.DESC,"modifiedTime");
        return adV2Repository.findAdsByAdIds(adIdArr,pageRequest);
    }

    /**
     * 查询待审核和审核失败广告
     * @param page
     * @param rows
     * @return
     */
    public Page<AdV2> listToAuditOrFailureAuditAds(Map<String,Object> searchMap, Integer page, Integer rows) {
        PageRequest pageRequest = PageUtil.buildPageRequest(page,rows, Sort.Direction.DESC,"modifiedTime");
        return findAll(searchMap,pageRequest);
    }

    /**
     * 查询待审核和审核失败广告
     * @param shopId
     * @param adName
     * @param page
     * @param rows
     * @return
     */
    public Page<AdV2> searchToAuditOrFailureAuditAds(Integer shopId, String adName, Integer page, Integer rows) {
        PageRequest pageRequest = PageUtil.buildPageRequest(page,rows, Sort.Direction.DESC,"modifiedTime");
        return adV2Repository.searchToAuditOrFailureAuditAds(shopId,adName,pageRequest);
    }

    /**
     * 查询内部广告(没有外部广告投放时默认播放广告)
     * @param shopId
     * @return
     */
    public List<AdV2> findInternalAdV2ByShopId(Integer shopId) {
        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_shop.id",shopId);
        searchMap.put("EQ_flag",true);
        return findAll(searchMap);
    }

    public List<AdV2> findInternalAdV2ByAdTypeId(List<Integer> allAdTypeIdList, Integer shopId) {
        Integer[] typeIds = allAdTypeIdList.toArray(new Integer[allAdTypeIdList.size()]);
        return adV2Repository.findInternalAdV2ByAdTypeIds(shopId,typeIds);
    }

    /**
     * 检查广告组是否被使用
     */
    public boolean isAvailableGroup(Integer groupId) {
        List<Integer> adV2s = adV2Repository.findAdsByGroupId(groupId);
        return CollectionUtils.isEmpty(adV2s);
    }

    /**
     * 根据广告id 批量查询广告
     * @param adIds
     * @return
     */
    public List<AdV2> findAdsByAdIds(List<Integer> adIds) {
        Integer[] adIdArr = adIds.toArray(new Integer[adIds.size()]);
        return adV2Repository.findByAdIds(adIdArr);
    }
}
