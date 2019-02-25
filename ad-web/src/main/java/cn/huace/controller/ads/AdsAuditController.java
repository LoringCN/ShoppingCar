package cn.huace.controller.ads;

import cn.huace.ads.Vo.AdsAuditTaskVo;
import cn.huace.ads.constant.AdsConstant;
import cn.huace.ads.entity.AdsAuditTask;
import cn.huace.ads.service.AdsAuditTaskService;
import cn.huace.ads.service.AdsAuditTrackService;
import cn.huace.ads.service.AdsService;
import cn.huace.common.bean.HttpResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 广告审核 控制层 created on 2018-05-25
 * @author Loring
 */
@Slf4j
@RestController
@Api(value = "/admin/ads/aduit",description = "最新版广告审核后台接口")
@RequestMapping(value = "/admin/ads/aduit")
public class AdsAuditController {

    @Autowired
    private AdsAuditTaskService adsAuditTaskService;

    @Autowired
    private AdsAuditTrackService adsAuditTrackService;

    @Autowired
    private AdsService adsService;

    /**
     * 任务列表查询 方法
     * @param adsAuditTaskVo
     * @param page
     * @param rows
     * @return
     */
    @ApiOperation(value = "任务列表查询 方法")
    @RequestMapping(value = "list",method = RequestMethod.POST)
    public HttpResult list(AdsAuditTaskVo adsAuditTaskVo,
                           @RequestParam(defaultValue = "1") Integer page,
                           @RequestParam(defaultValue = "20") Integer rows){
        log.info("*** 广告列表查询开始：list()，入参 adsAuditTaskVo：{}",JSONObject.fromObject(adsAuditTaskVo).toString());
        HttpResult httpResult = null;
        if(null == adsAuditTaskVo || null == adsAuditTaskVo.getShopId()){
            httpResult = HttpResult.createFAIL("请选择一个商店！");
        }else if(adsAuditTaskVo.getCheckCode() == null ){
            //根据查询方式查询列表
            httpResult = HttpResult.createFAIL("请选择一个查询方式！");
        }else{
            //默认查询有效数据
            adsAuditTaskVo.setIsEnabled(null!= adsAuditTaskVo.getIsEnabled()?adsAuditTaskVo.getIsEnabled():true);
            Page<AdsAuditTaskVo> plist = adsAuditTaskService.list(adsAuditTaskVo,page,rows);
            httpResult =  HttpResult.createSuccess(plist);
        }

        log.info("*** 广告列表查询结束：出参 HttpResult {}",ToStringBuilder.reflectionToString(httpResult));
        return httpResult;

    }

    /**
     * 处理任务 方法
     * @param id
     * @return
     */
    @ApiOperation(value = "处理任务 方法")
    @RequestMapping(value = "predeal/{id}",method = RequestMethod.GET)
    public HttpResult predeal(@PathVariable("id") Integer id){
        log.info("*** 处理任务方法开始：predeal()，入参 id：{}",id);
        HttpResult httpResult = null;
        //更新任务状态
        AdsAuditTaskVo adsAuditTask = adsAuditTaskService.predeal(id);
//        httpResult = adsAuditTask == null?HttpResult.createFAIL("任务处理失败！"):HttpResult.createSuccess(adsAuditTask);
        if(adsAuditTask == null){
            httpResult = HttpResult.createFAIL("任务处理失败！");
        }else {
            httpResult = HttpResult.createSuccess(adsAuditTask);
        }
        log.info("*** 处理任务方法结束：predeal()，出参 HttpResult {}",ToStringBuilder.reflectionToString(httpResult));
        return  httpResult;
    }

    /**
     * 取消处理任务 方法
     * @param id
     * @return
     */
    @ApiOperation(value = "取消处理任务 方法")
    @RequestMapping(value = "cancel/{id}",method = RequestMethod.GET)
    public HttpResult cancel(@PathVariable("id") Integer id){
        log.info("*** 取消处理任务方法开始：cancel()，入参 id：{}",id);
        HttpResult httpResult = null;
        //更新任务状态
        AdsAuditTaskVo adsAuditTask = adsAuditTaskService.cancel(id);
        if(adsAuditTask == null){
            httpResult = HttpResult.createFAIL("取消任务处理失败！");
        }else {
            httpResult = HttpResult.createSuccess(adsAuditTask);
        }
        log.info("*** 取消处理任务方法结束：cancel()，出参 HttpResult {}",ToStringBuilder.reflectionToString(httpResult));
        return  httpResult;
    }

    /**
     * 任务处理提交 方法 （提交审核、审核通过、审核驳回、审核不通过）
     * @param id
     * @param auditStatus
     * @param reason
     * @return
     */
    @ApiOperation(value = "任务处理提交 方法")
    @RequestMapping(value = "deal/{id}",method = RequestMethod.POST)
    public HttpResult deal(@PathVariable("id") Integer id,
                           @RequestParam Integer auditStatus,
                           @RequestParam String reason
                           ){
        log.info("*** 任务处理提交 方法 开始：deal()，入参 id：{}",id);
        HttpResult httpResult = null;
        AdsAuditTask adsAuditTask = adsAuditTaskService.findOne(id);
//        switch (auditStatus){
//            case AdsConstant.ADS_AUDIT.AUDIT_ACTION.TO_AUDIT:
//                if(adsAuditTask.getAuditStatus() != AdsConstant.ADS.AUDIT_STATUS.INITIAL_ &&
//                        adsAuditTask.getAuditStatus() != AdsConstant.ADS.AUDIT_STATUS.BACK_)
//                    httpResult = HttpResult.createFAIL("广告当前状态不允许提交审核！");
//                break;
//            case AdsConstant.ADS_AUDIT.AUDIT_ACTION.TO_PASS:
//                if(adsAuditTask.getAuditStatus() != AdsConstant.ADS.AUDIT_STATUS.DEALING_ )
//                    httpResult = HttpResult.createFAIL("广告当前状态不允许提交审核通过！");
//                break;
//            case AdsConstant.ADS_AUDIT.AUDIT_ACTION.TO_BACK:
//                if(adsAuditTask.getAuditStatus() != AdsConstant.ADS.AUDIT_STATUS.DEALING_ )
//                    httpResult = HttpResult.createFAIL("广告当前状态不允许提交审核驳回！");
//                 break;
//            case AdsConstant.ADS_AUDIT.AUDIT_ACTION.TO_REFUSE:
//                if(adsAuditTask.getAuditStatus() != AdsConstant.ADS.AUDIT_STATUS.DEALING_ )
//                    httpResult = HttpResult.createFAIL("广告当前状态不允许提交审核拒绝！");
//                 break;
//            case AdsConstant.ADS_AUDIT.AUDIT_ACTION.TO_UPPER:
//                if(adsAuditTask.getAuditStatus() != AdsConstant.ADS.AUDIT_STATUS.DEALING_ )
//                    httpResult = HttpResult.createFAIL("广告当前状态不允许提交上级审核！");
//                break;
//            default:
//                httpResult = HttpResult.createFAIL("参数不合法，请检查参数后再提交！");
//        }
             httpResult = !adsAuditTaskService.deal(id,adsAuditTask.getAds().getId(),auditStatus,reason)?
                HttpResult.createFAIL("任务处理提交失败！"):HttpResult.createSuccess("任务处理提交成功！");
        log.info("*** 任务处理提交 方法 结束：deal()，出参 HttpResult {}",ToStringBuilder.reflectionToString(httpResult));
        return httpResult;
    }




}
