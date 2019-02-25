package cn.huace.ads.service;

import cn.huace.ads.Vo.AdsAuditTaskVo;
import cn.huace.ads.Vo.AdsVo;
import cn.huace.ads.constant.AdsConstant;
import cn.huace.ads.entity.Ads;
import cn.huace.ads.entity.AdsAuditTask;
import cn.huace.ads.entity.AdsAuditTrack;
import cn.huace.ads.repository.AdsAuditTaskRepository;
import cn.huace.common.service.BaseService;
import cn.huace.shop.shop.entity.Shop;
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
 * 广告审核 服务层 created by 2018-05-29
 * @author Loring
 */
@Service
public class AdsAuditTaskService extends BaseService<AdsAuditTask,Integer> {

    @Autowired
    private AdsAuditTaskRepository adsAuditTaskRepository;

    @Autowired
    private AdsService adsService;

    @Autowired
    private AdsAuditTrackService adsAuditTrackService;

    /**
     * 任务 分页查询
     * @param vo
     * @param page
     * @param rows
     * @return
     */
    public Page<AdsAuditTaskVo> list(AdsAuditTaskVo vo, Integer page, Integer rows){

        Map<String,Object> searchMap = new HashMap<>();
        if(vo.getShopId() != null)
            searchMap.put("EQ_shopId",vo.getShopId());
        if(vo.getAdsVo() !=null && StringUtils.isNotBlank(vo.getAdsVo().getName()))
            searchMap.put("LIKE_ads.name",vo.getAdsVo().getName());
        //根据不同的查询方式拼接不同的查询条件
        switch (vo.getCheckCode()){
            case AdsConstant.ADS_AUDIT.CHECK_CODE.ALL_:
                break;
            case AdsConstant.ADS_AUDIT.CHECK_CODE.WAITING_DEALING_:
                searchMap.put("IN_auditStatus",AdsConstant.ADS.AUDIT_STATUS.WAITING_+","+AdsConstant.ADS.AUDIT_STATUS.DEALING_);
                break;
            case AdsConstant.ADS_AUDIT.CHECK_CODE.INITIAL_BACK_:
                searchMap.put("IN_auditStatus",AdsConstant.ADS.AUDIT_STATUS.INITIAL_+","+AdsConstant.ADS.AUDIT_STATUS.BACK_);
                break;
            case AdsConstant.ADS_AUDIT.CHECK_CODE.REFUSE_9:
                searchMap.put("EQ_auditStatus",AdsConstant.ADS.AUDIT_STATUS.REFUSE_);
                break;
        }
        if(vo.getIsEnabled() != null)
        searchMap.put("EQ_isEnabled",vo.getIsEnabled());

        Page<AdsAuditTask> pageList = findAll(searchMap,page,rows,Sort.Direction.DESC,"modifiedTime");
        List<AdsAuditTaskVo> retData = new ArrayList<AdsAuditTaskVo>();
        Sort sort = new Sort(Sort.Direction.DESC, "modifiedTime");
        Pageable pageable = new PageRequest(page, rows, sort);
        retData =  pageList.getContent().stream().map( adsAuditTask -> PoToVo(adsAuditTask,new AdsAuditTaskVo())).collect(Collectors.toList());
        Page<AdsAuditTaskVo> ret = new PageImpl<>(retData,null,pageList.getTotalElements());
        return ret;
    }

    /**
     * 任务 处理方法
     * @param id
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AdsAuditTaskVo predeal(Integer id){
        AdsAuditTask adsAuditTask = findOne(id);
        if (adsAuditTask.getAuditStatus() != AdsConstant.ADS.AUDIT_STATUS.WAITING_){
            return null;
        }else {
            adsAuditTask.setAuditStatus(AdsConstant.ADS.AUDIT_STATUS.DEALING_);
            adsAuditTask = save(adsAuditTask);
            Ads ads = adsAuditTask.getAds();
            ads.setAuditStatus(AdsConstant.ADS.AUDIT_STATUS.DEALING_);
            adsService.save(ads);
        }
        return PoToVo(adsAuditTask,new AdsAuditTaskVo());
    }

    /**
     * 取消任务 处理方法
     * @param id
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AdsAuditTaskVo cancel(Integer id){
        AdsAuditTask adsAuditTask = findOne(id);
        if (adsAuditTask.getAuditStatus() != AdsConstant.ADS.AUDIT_STATUS.DEALING_){
            return null;
        }else {
            adsAuditTask.setAuditStatus(AdsConstant.ADS.AUDIT_STATUS.WAITING_);
            adsAuditTask = save(adsAuditTask);
            Ads ads = adsAuditTask.getAds();
            ads.setAuditStatus(AdsConstant.ADS.AUDIT_STATUS.WAITING_);
            adsService.save(ads);
        }
        return PoToVo(adsAuditTask,new AdsAuditTaskVo());
    }

    /**
     * 任务 处理提交 方法
     * @param id
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean deal(Integer id,Integer adId,Integer auditStatus, String reason){
        /**
         * 1、更新审核任务
         * 2、更新广告状态
         * 3、新增审核轨迹
         */
        adsAuditTaskRepository.updateAuditStatus(auditStatus,id,reason);
        Integer shelf = null;
        if(auditStatus == AdsConstant.ADS.AUDIT_STATUS.PASS_){
            shelf = AdsConstant.ADS.IS_SHELF.INVAILD_;
        }
        adsService.updateAuditStatus(auditStatus,adId,reason,shelf);
        AdsAuditTrack adsAuditTrack1 = adsAuditTrackService.findByMaxTaskId(id);
        int versionNo_ = adsAuditTrack1.getVersionNo();
        AdsAuditTrack adsAuditTrack = new AdsAuditTrack();
//        BeanUtils.copyProperties(adsAuditTrack1,adsAuditTrack);
        adsAuditTrack.setShopId(adsAuditTrack1.getShopId());
        adsAuditTrack.setAdsAuditTask(adsAuditTrack1.getAdsAuditTask());
        adsAuditTrack.setAuditStatus(auditStatus);
        adsAuditTrack.setReason(reason);
        Date date_ = new Date();
        adsAuditTrack.setVersionNo(versionNo_+1);
        adsAuditTrack.setCreator(adsAuditTrack1.getCreator());
        adsAuditTrack.setCreatedTime(date_);
        adsAuditTrack.setModifier(adsAuditTrack1.getModifier());
        adsAuditTrack.setModifiedTime(date_);
        adsAuditTrack.setIsEnabled(true);
        adsAuditTrackService.save(adsAuditTrack);
        return true;
    }

    /**
     * 对象拷贝 PO TO VO
     * @param source
     * @param target
     * @return
     */
    public AdsAuditTaskVo PoToVo(AdsAuditTask source, AdsAuditTaskVo target ){
        BeanUtils.copyProperties(source,target);
        target.setId(source.getId());
        target.setAdsVo(adsService.PoToVo(source.getAds(),new AdsVo()));
        return target;
    }

    /**
     * 对象拷贝 VO TO PO
     * @param source
     * @param target
     * @return
     */
    public AdsAuditTask VoToPo(AdsAuditTaskVo source, AdsAuditTask target){
        BeanUtils.copyProperties(source,target);
        target.setAds(adsService.VoToPo(source.getAdsVo(),new Ads()));
        return target;
    }

    /**
     * 关闭任务
     * @param adId
     * @return
     */
//    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean deleteByAdId(Integer adId){
        return  adsAuditTaskRepository.deleteByAdId(adId)>0?true:false;
    }
}
