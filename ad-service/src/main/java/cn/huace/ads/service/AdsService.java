package cn.huace.ads.service;

import cn.huace.ads.Vo.AdsVo;
import cn.huace.ads.constant.AdsConstant;
import cn.huace.ads.entity.Ads;
import cn.huace.ads.entity.AdsAuditTask;
import cn.huace.ads.entity.AdsAuditTrack;
import cn.huace.ads.repository.AdsRepository;
import cn.huace.common.service.BaseService;
import cn.huace.common.service.BizcodeService;
import cn.huace.common.utils.DateUtils;
import cn.huace.goods.entity.Goods;
import cn.huace.goods.service.GoodsService;
import cn.huace.shop.shop.entity.Shop;
import cn.huace.shop.shop.service.ShopService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 广告基本信息 服务层 created by 2018-05-25
 * @author Loring
 */
@Service
public class AdsService extends BaseService<Ads,Integer> {

    @Autowired
    private AdsRepository adsRepository;

    @Autowired
    private BizcodeService bizcodeService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private AdsAuditTaskService adsAuditTaskService;

    @Autowired
    private AdsAuditTrackService adsAuditTrackService;
    /**
     * 服务层 广告列表查询
     * @param vo
     * @param page
     * @param rows
     * @return
     */
    public Page<AdsVo> list(AdsVo vo, Integer page, Integer rows){

        Map<String,Object> searchMap = new HashMap<>();
        //商店名称
        if(vo.getShopId() != null)
        searchMap.put("EQ_shop.id",vo.getShopId());
        //广告名称 like
        if(StringUtils.isNotBlank(vo.getName()))
        searchMap.put("LIKE_name",vo.getName());
        //审核通过广告
        if(vo.getAuditStatus() != null)
        searchMap.put("EQ_auditStatus",vo.getAuditStatus());
        //广告类型
        if(vo.getType() != null)
        searchMap.put("EQ_type",vo.getType());
        //广告上架状态
        if(vo.getIsShelf() != null)
        searchMap.put("EQ_isShelf",vo.getIsShelf());
        //未删除广告 <>
        if(vo.getIsEnabled() != null)
        searchMap.put("EQ_isEnabled",vo.getIsEnabled());

        Page<Ads> pageList = findAll(searchMap,page,rows,Sort.Direction.DESC,"isDefalut","modifiedTime");

        List<AdsVo> retData = new ArrayList<AdsVo>();
        Sort sort = new Sort(Sort.Direction.DESC, "isDefalut","modifiedTime");
        Pageable pageable = new PageRequest(page, rows, sort);
        retData =  pageList.getContent().stream().map( ads -> PoToVo(ads,new AdsVo())).collect(Collectors.toList());
//        List list1_ = adList.getContent();
//        for (Object ads : list1_) {
//            AdsVo adsVo = new AdsVo();
//            BeanUtils.copyProperties(ads,adsVo);
//            retData.add(adsVo);
//        }

        Page<AdsVo> ret = new PageImpl<>(retData,null,pageList.getTotalElements());
        return ret;
    }

    /**
     * 服务层 广告单个查询
     * @param id
     * @return
     */
    public AdsVo findById(Integer id){
         Ads ads = findOne(id);
         AdsVo vo = PoToVo(ads,new AdsVo());
         if(vo.getRelationCode() == AdsConstant.ADS.RELATION_CODE.GOODS_ && StringUtils.isNotBlank(vo.getRelationExtra())){
            Goods goods = goodsService.findGoodsDetailForAdmin(Integer.parseInt(vo.getRelationExtra()));
            vo.setGoodsName(goods.getTitle());
         }
        if(vo.getRelationCode() == AdsConstant.ADS.RELATION_CODE.LBS_ && StringUtils.isNotBlank(vo.getRelationExtra())){
            String str = vo.getRelationExtra().split(",")[0];
            Goods goods = goodsService.findGoodsDetailForAdmin(Integer.parseInt(str));
            vo.setGoodsName(goods.getTitle());
        }
        return vo;
    }

    /**
     * 服务层 广告编辑保存
     * @param adsVo
     * @return
     */
    public Boolean edit(AdsVo adsVo){
        Ads retPo = save(VoToPo(adsVo,new Ads()));
        if (retPo == null){
            return false;
        }
        if(adsVo.getId() == null){
            //保存审核任务
            AdsAuditTask task = new AdsAuditTask();
            task.setShopId(retPo.getShop().getId());
            task.setAds(retPo);
            task.setAuditStatus(AdsConstant.ADS.AUDIT_STATUS.WAITING_);
            task.setCreator(retPo.getCreator());
            task.setModifier(retPo.getModifier());
            AdsAuditTask retask = adsAuditTaskService.save(task);
            if(retask == null){
                return false;
            }
            //保存轨迹
            AdsAuditTrack track = new AdsAuditTrack();
            track.setShopId(retPo.getShop().getId());
            track.setAdsAuditTask(retask);
            track.setAuditStatus(AdsConstant.ADS.AUDIT_STATUS.WAITING_);
            track.setVersionNo(0);//初始版本
            track.setCreator(retPo.getCreator());
            track.setModifier(retPo.getModifier());
            AdsAuditTrack retrack = adsAuditTrackService.save(track);
            if(retrack == null){
                return false;
            }
        }

        return true;
    }

    /**
     * 服务层 广告删除方法
     * @param id
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean deleteById(Integer id){
       Boolean returnFlag = false;
       Boolean flag = adsRepository.findEnabledById(id);
       if (flag){
           returnFlag = adsRepository.updateEnalbedById(!flag,id ) > 0 ? true:false;
           adsAuditTaskService.deleteByAdId(id);
       }
       return returnFlag;
    }

    /**
     * 服务层 广告更新上架状态
     * @param id
     * @param flag_
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean updateShelf(Integer flag_,Integer id){
        return  adsRepository.updateShelfById(flag_,id) > 0 ? true:false;
    }

    /**
     * 更新 广告审核状态
     * @param auditStatus_
     * @param id_
     * @param reason_
     * @return
     */
    public Boolean updateAuditStatus(Integer auditStatus_,Integer id_,String reason_,Integer shelf){
        return  adsRepository.updateAuditStatus(auditStatus_,id_,reason_,shelf) > 0 ? true:false;
    }

    /**
     * 查询默认广告
     * @param shopId
     * @param adType
     * @return
     */
    public List<Ads> findByDefalut (Integer shopId,Integer adType){
        return adsRepository.findByDefalut(shopId, adType);
    }

    /**
     * 查询可上线广告
     * @param shopId
     * @param adType
     * @return
     */
    public List<Ads> findByPreOnline(Integer shopId,Integer adType){
        return adsRepository.findByPreOnline(shopId, adType);
    }

    /**
     * 提交 审核
     * @param id
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean toAudit(Integer id){
        Ads ads = findOne(id);
        if(ads == null || ads.getAuditStatus() != AdsConstant.ADS.AUDIT_STATUS.INITIAL_){
            return false;
        }
        AdsAuditTask task = new AdsAuditTask();
        task.setShopId(ads.getShop().getId());
        task.setAds(ads);
        task.setAuditStatus(AdsConstant.ADS.AUDIT_STATUS.WAITING_);
        task.setCreator(ads.getCreator());
        task.setModifier(ads.getModifier());
        AdsAuditTask retask = adsAuditTaskService.save(task);
        if(retask == null){
            return false;
        }
        //保存轨迹
        AdsAuditTrack track = new AdsAuditTrack();
        track.setShopId(ads.getShop().getId());
        track.setAdsAuditTask(retask);
        track.setAuditStatus(AdsConstant.ADS.AUDIT_STATUS.WAITING_);
        track.setVersionNo(0);//初始版本
        track.setCreator(ads.getCreator());
        track.setModifier(ads.getModifier());
        AdsAuditTrack retrack = adsAuditTrackService.save(track);
        if(retrack == null){
            return false;
        }
        return updateAuditStatus(AdsConstant.ADS.AUDIT_STATUS.WAITING_,id,null,null);
    }

    /**
     * 到期广告查询
     * @param shopId
     * @param name
     * @param page
     * @param rows
     * @return
     */
    public Page<AdsVo> renewalList(Integer shopId,String name,Integer page, Integer rows){

        Map<String,Object> searchMap = new HashMap<>();
        //商店名称
        if(shopId != null)
            searchMap.put("EQ_shop.id",shopId);
        //广告名称 like
        if(StringUtils.isNotBlank(name))
            searchMap.put("LIKE_name",name);
        //固定设置7天
        searchMap.put("LTE_expiryTime",DateUtils.getNextDayTimes(7));
        searchMap.put("EQ_isEnabled",true);

        Page<Ads> pageList = findAll(searchMap,page,rows,Sort.Direction.DESC,"isDefalut","modifiedTime");

        List<AdsVo> retData = new ArrayList<AdsVo>();
        retData =  pageList.getContent().stream().map( ads -> PoToVo(ads,new AdsVo())).collect(Collectors.toList());
        Page<AdsVo> ret = new PageImpl<>(retData,null,pageList.getTotalElements());
        return ret;
    }

    /**
     * 广告续期
     * @param adId
     * @param expiryTime
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean renewal(Integer adId,Date expiryTime){
      return adsRepository.renewal(adId,expiryTime)>0?true:false;
    }

    /**
     * 对象拷贝 PO TO VO
     * @param source
     * @param target
     * @return
     */
    public AdsVo PoToVo(Ads source, AdsVo target ){
        BeanUtils.copyProperties(source,target);
        target.setId(source.getId());
        target.setTypeName(
            bizcodeService.findByTypeAndCode("adType",source.getType().toString()).getCodeName()
        );
        target.setAuditName(
            bizcodeService.findByTypeAndCode("auditStatus",source.getAuditStatus().toString()).getCodeName()
        );
        target.setOwnerName(
            bizcodeService.findByTypeAndCode("adOwner",source.getOwnerCode().toString()).getCodeName()
        );
        target.setRelationName(
            bizcodeService.findByTypeAndCode("adRelation",source.getRelationCode().toString()).getCodeName()
        );
        target.setShopId(source.getShop().getId());
        target.setShopName(source.getShop().getName());

        if(source.getRelationCode() == AdsConstant.ADS.RELATION_CODE.GOODS_ && StringUtils.isNotBlank(source.getRelationExtra())){
            Goods goods = goodsService.findGoodsDetailForAdmin(Integer.parseInt(source.getRelationExtra()));
            target.setGoodsName(goods.getTitle());
        }
        if(source.getRelationCode() == AdsConstant.ADS.RELATION_CODE.LBS_ && StringUtils.isNotBlank(source.getRelationExtra())){
            String str = source.getRelationExtra().split(",")[0];
            Goods goods = goodsService.findGoodsDetailForAdmin(Integer.parseInt(str));
            target.setGoodsName(goods.getTitle());
        }

        return target;
    }

    /**
     * 对象拷贝 VO TO PO
     * @param source
     * @param target
     * @return
     */
    public Ads VoToPo(AdsVo source, Ads target){
        BeanUtils.copyProperties(source,target);
        if(null != source.getId()){
            target.setId(source.getId());
        }
        Shop shop = shopService.findOne(source.getShopId());
        target.setShop(shop);
        return target;
    }

}
