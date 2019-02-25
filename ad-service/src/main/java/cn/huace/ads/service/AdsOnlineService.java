package cn.huace.ads.service;

import cn.huace.ads.Vo.AdsOnlineVo;
import cn.huace.ads.Vo.AdsVo;
import cn.huace.ads.constant.AdsConstant;
import cn.huace.ads.entity.Ads;
import cn.huace.ads.entity.AdsOnline;
import cn.huace.ads.repository.AdsOnlineRepository;
import cn.huace.common.service.BaseService;
import cn.huace.sys.bean.ShiroUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 服务层 线上广告  created on 2018-06-04
 * @author Loring
 */
@Service
public class AdsOnlineService extends BaseService<AdsOnline,Integer> {

    @Autowired
    private AdsOnlineRepository adsOnlineRepository;
    @Autowired
    private AdsService adsService;

    /**
     * 线上广告查询列表方法
     * @param vo
     * @param page
     * @param rows
     * @return
     */
    public Page<AdsOnlineVo> list(AdsOnlineVo vo, Integer page, Integer rows){

        Map<String,Object> searchMap = new HashMap<>();
        //商店名称
        if(vo.getShopId() != null)
            searchMap.put("EQ_shopId",vo.getShopId());
        //广告类型
        if(vo.getType() != null)
            searchMap.put("EQ_type",vo.getType());
        if(vo.getRank() != null){
            searchMap.put("EQ_rank",vo.getRank());
        }else {
            searchMap.put("EQ_rank",0);
        }

        //未删除广告 <>
        if(vo.getIsEnabled() != null)
            searchMap.put("EQ_isEnabled",vo.getIsEnabled());

        Page<AdsOnline> pageList = findAll(searchMap,page,rows,Sort.Direction.DESC,"modifiedTime");
        List<AdsOnlineVo> retData = new ArrayList<AdsOnlineVo>();
//        Sort sort = new Sort(Sort.Direction.DESC, "modifiedTime");
//        Pageable pageable = new PageRequest(page, rows, sort);
        retData =  pageList.getContent().stream().map( adsOnline -> PoToVo(adsOnline,new AdsOnlineVo())).collect(Collectors.toList());
        Page<AdsOnlineVo> ret = new PageImpl<>(retData,null,pageList.getTotalElements());
        return ret;
    }

    /**
     * 线上广告查询
      * @param shopId
     * @param type
     * @param rank
     * @param page
     * @param rows
     * @return
     */
    public Page<AdsVo> listNew(Integer shopId,Integer type,String name,Integer rank , Integer page, Integer rows){

        //查出发布的线上广告
        //List<Integer> adIdList = adsOnlineRepository.findAdId(shopId,type,rank);
        //查询广告信息
        List<Ads> adsList =  adsOnlineRepository.listNew(shopId,type,name,rank);
//        Sort sort = new Sort(Sort.Direction.DESC, "modifiedTime");
////        Pageable pageable = new PageRequest(page, rows, sort);
        List<AdsVo> retData =  adsList.stream().map( ads -> adsService.PoToVo(ads,new AdsVo())).collect(Collectors.toList());
        if(adsList.size() > rows){
            if(rows * page < adsList.size()){
                retData =  retData.subList(( page - 1) * rows -1 ,rows * page - 1 );
            }else{
                retData =  retData.subList(( page - 1) * rows -1 ,retData.size() - 1);
            }
        }
        Page<AdsVo> ret = new PageImpl<>(retData,null,adsList.size());
        return ret;
    }

    /**
     * 查询广告 投放设备信息
     * @param shopId
     * @param adId
     * @return
     */
    public List<String> findDevId(Integer shopId,Integer adId){
      return  adsOnlineRepository.findDevId(shopId,adId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean updateDevId(Integer adId,String [] devIds,Integer rank,ShiroUser user){
        //先删除 后插入
        Integer num_ = adsOnlineRepository.deleteByadId(adId);
        if(num_>0){
            Ads ads= adsService.findOne(adId);
            if(devIds == null || devIds.length == 0){
                ads.setDeliverScope(-1);
            }else{
                ads.setDeliverScope(1);
            }
            adsService.save(ads);
            saveOnline(ads,devIds,rank,user);
            return true;
        }else {
            return false;
        }
    }

    /**
     *  上下线 方法
     * @param adId
     * @param isVoted 1-上线，0-下线
     * @param devIds
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean updateisVoted(Integer adId,Integer isVoted,String [] devIds,Integer rank,ShiroUser user){
        // 下线 改变广告状态，删除线上广告
        Ads ads = adsService.findOne(adId);
        ads.setModifier(user.getAccount());
        if(devIds == null || devIds.length == 0){
            ads.setDeliverScope(AdsConstant.ADS.DeliverScope.DEFAULT_SCOPE);
        }else {
            ads.setDeliverScope(AdsConstant.ADS.DeliverScope.ONLY_SCOPE);
        }
        if(isVoted == 0){
            Integer num_ = adsOnlineRepository.deleteByadId(adId);
            if (num_ > 0){
                ads.setIsVoted(false);
                adsService.save(ads);
                return  true;
            }else {
                return false;
            }
        }else {
            ads.setIsVoted(true);
            adsService.save(ads);
            saveOnline(ads,devIds,rank,user);
            return true;
        }

    }

    /**
     * 查询可上线广告(不包含默认广告)
     * @param shopId
     * @param adType
     * @return
     */
    public List<AdsVo> preOnline(Integer shopId,Integer adType){
        List<Ads>  list = adsService.findByPreOnline(shopId, adType);
        return list.stream().map(ads -> adsService.PoToVo(ads,new AdsVo())).collect(Collectors.toList());
    }

    /**
     * 对象拷贝 PO TO VO
     * @param source
     * @param target
     * @return
     */
    public AdsOnlineVo PoToVo(AdsOnline source, AdsOnlineVo target ){
        BeanUtils.copyProperties(source,target);
        target.setAdsVo(adsService.PoToVo(source.getAds(),new AdsVo()));
        return target;
    }

    /**
     * 对象拷贝 VO TO PO
     * @param source
     * @param target
     * @return
     */
    public AdsOnline VoToPo(AdsOnlineVo source, AdsOnline target){
        BeanUtils.copyProperties(source,target);
        target.setAds(adsService.VoToPo(source.getAdsVo(),new Ads()));
        return target;
    }

    /**
     * 保存线上广告
     * @param ads
     * @param devIds
     */
    public void saveOnline(Ads ads,String [] devIds,Integer rank,ShiroUser user){
        if(devIds !=null &&devIds.length>0) {
            List<AdsOnline> list = new ArrayList<AdsOnline>();
            for (String dev : devIds) {
                AdsOnline adsOnline = new AdsOnline();
                adsOnline.setAds(ads);
                adsOnline.setShopId(ads.getShop().getId());
                adsOnline.setDeliverScope(AdsConstant.ADS.DeliverScope.ONLY_SCOPE);
                adsOnline.setDevId(dev);
                adsOnline.setType(ads.getType());
                adsOnline.setRank(rank);
                adsOnline.setCreator(user.getAccount());
                adsOnline.setModifier(user.getAccount());
                adsOnline.setIsEnabled(true);
                list.add(adsOnline);
            }
            batchInsert(list);
        }else {
            AdsOnline adsOnline = new AdsOnline();
            adsOnline.setAds(ads);
            adsOnline.setShopId(ads.getShop().getId());
            adsOnline.setDeliverScope(AdsConstant.ADS.DeliverScope.DEFAULT_SCOPE);
            adsOnline.setType(ads.getType());
            adsOnline.setRank(rank);
            adsOnline.setCreator(user.getAccount());
            adsOnline.setModifier(user.getAccount());
            adsOnline.setIsEnabled(true);
            save(adsOnline);
        }
    }

    /**
     * 查询设备线上广告 开机视频广告或者封面广告
     * @param shopId
     * @Param adType
     * @param devid
     * @return
     */
    public AdsOnline findNewByDevId(Integer shopId,Integer adType,String devid){
        List<AdsOnline> list = adsOnlineRepository.findByDevId(shopId, adType, devid);
        if(list == null || list.size() == 0){
            list = adsOnlineRepository.findByDefault(shopId, adType);
        }
        return list.size()> 0 ? list.get(0):null;
    }

    /**
     * 查询设备线上广告 首页轮播、新品轮播和LBS广告
     * @param shopId
     * @param adType
     * @param devid
     * @return
     */
    public List<AdsOnline> findAllByDevId(Integer shopId,Integer adType,String devid){
        return adsOnlineRepository.findLBByDevId(shopId, adType, devid);
    }
}
