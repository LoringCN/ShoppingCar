package cn.huace.controller.ads;

import cn.huace.ads.Vo.AdsVo;
import cn.huace.ads.constant.AdsConstant;
import cn.huace.ads.service.AdsService;
import cn.huace.common.bean.HttpResult;
import cn.huace.controller.base.AdminBasicController;
import cn.huace.sys.bean.ShiroUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 广告基本信息 控制层 created on 2018-05-25
 * @author Loring
 */
@Slf4j
@RestController
@Api(value = "/admin/ads",description = "最新版广告管理后台接口")
@RequestMapping(value = "/admin/ads")
public class AdsController extends AdminBasicController{

    @Autowired
    private AdsService adsService;

    /**
     * 广告列表查询 方法
     * @param adsVo
     * @param page
     * @param rows
     * @return
     */
    @ApiOperation(value = "广告列表查询 方法")
    @RequestMapping(value = "list",method = RequestMethod.POST)
    public HttpResult list( AdsVo adsVo,
                           @RequestParam(defaultValue = "1") Integer page,
                           @RequestParam(defaultValue = "20") Integer rows){
//        log.info("*** 广告列表查询开始：list()，入参 adsVo：{}",adsVo.toString());
        log.info("*** 广告列表查询开始：list()，入参 adsVo：{}",JSONObject.fromObject(adsVo).toString());
        HttpResult httpResult = null;
        if (null == adsVo || null == adsVo.getShopId()) {
            httpResult = HttpResult.createFAIL("请选择一个商店！");
        }else {
                switch (adsVo.getCheckCode()) {
                case AdsConstant.ADS.CHECK_CODE.SHELF_:
                    if ( null == adsVo.getIsShelf() ){
                        httpResult = HttpResult.createFAIL("请选择一个广告上架状态！");
                        break;
                    }
                case AdsConstant.ADS.CHECK_CODE.TYPE_:
                    if ( null == adsVo.getType() ){
                        httpResult = HttpResult.createFAIL("请选择一个广告类型！");
                        break;
                    }
                case AdsConstant.ADS.CHECK_CODE.NAME_:
                    if (StringUtils.isBlank(adsVo.getName())){
                        httpResult = HttpResult.createFAIL("请输入搜索关键字！");
                        break;
                    }
                default:
                //默认查询有效数据
                adsVo.setIsEnabled(null!= adsVo.getIsEnabled()?adsVo.getIsEnabled():true);
                httpResult = HttpResult.createSuccess(adsService.list(adsVo,page,rows));
            }
        }
        log.info("*** 广告列表查询结束：出参 HttpResult {}",ToStringBuilder.reflectionToString(httpResult));
        return httpResult;

    }

    /**
     * 广告单个查询 方法
     * @param id
     * @return
     */
    @ApiOperation(value = "广告单个查询 方法")
    @RequestMapping(value = "findById/{id}",method = RequestMethod.GET)
    public HttpResult findById(@PathVariable("id") Integer id){
        log.info("*** 广告单个查询开始：findById()，入参 id：{}",id);
        HttpResult httpResult;
        AdsVo adsVo = adsService.findById(id);
        httpResult = HttpResult.createSuccess(adsVo);
        log.info("*** 广告单个查询结束：findById()，出参 HttpResult {}",ToStringBuilder.reflectionToString(httpResult));
        return  httpResult;
    }

    /**
     * 广告保存/更新 方法
     * @param adsVo
     * @return
     */
    @ApiOperation(value = "广告保存/更新 方法")
    @RequestMapping(value = "save",method = RequestMethod.POST)
    public HttpResult edit(AdsVo adsVo){
        log.info("*** 广告保存/更新查询开始：edit()，入参 ads：{}",adsVo.toString());
        HttpResult httpResult;
        ShiroUser user = getCurrentUser();
        if(StringUtils.isBlank(adsVo.getCreator())){
            adsVo.setCreator(user.getAccount());
        }
        if(StringUtils.isBlank(adsVo.getModifier())){
            adsVo.setModifier(user.getAccount());
        }
        //新增默认 待审核
        if(adsVo.getAuditStatus() == null){
            adsVo.setAuditStatus(AdsConstant.ADS.AUDIT_STATUS.WAITING_);
        }else if(adsVo.getAuditStatus()==AdsConstant.ADS.AUDIT_STATUS.BACK_) {
            //驳回的数据修改后，状态置为初始态
            adsVo.setAuditStatus(AdsConstant.ADS.AUDIT_STATUS.INITIAL_);
        }
        //非默认广告
        adsVo.setIsDefalut(false);
        //默认有效数据
        adsVo.setIsEnabled(null!= adsVo.getIsEnabled()?adsVo.getIsEnabled():true);

        if(adsService.edit(adsVo)) {
            httpResult = HttpResult.createSuccess("广告保存成功！");
        }else {
            httpResult = HttpResult.createFAIL("广告保存失败！");
        }
        log.info("*** 广告保存/更新查询结束：edit()，出参 HttpResult {}",ToStringBuilder.reflectionToString(httpResult));
        return httpResult;
    }

    /**
     * 广告删除 方法
     * @param id
     * @return
     */
    @ApiOperation(value = "广告删除 方法")
    @RequestMapping(value = "/delete/{id}")
    public HttpResult delete(@PathVariable("id") Integer id){
        log.info("*** 广告删除开始：delete()，入参 id：{}",id);
        HttpResult httpResult;
//        AdsVo adsVo = adsService.findById(id);
        Boolean flag  = adsService.deleteById(id);
        if (flag){
//            httpResult = HttpResult.createSuccess("对象删除成功!",adsVo);
            httpResult = HttpResult.createSuccess("对象删除成功!");
        }else {
//            httpResult = HttpResult.createFAIL("对象删除失败!",adsVo);
            httpResult = HttpResult.createFAIL("对象删除失败!");
        }
        log.info("*** 广告删除结束：delete()，出参 HttpResult {}",ToStringBuilder.reflectionToString(httpResult));
        return  httpResult;
    }

    @ApiOperation(value = "广告上架or下架 方法")
    @RequestMapping(value = "/{shelf}/{id}",method = RequestMethod.GET)
    public HttpResult shelf( @PathVariable("shelf") String shelf, @PathVariable("id") Integer id){
        log.info("*** 广告上架or下架开始: shelf(),入参：shelf = {},id={}",shelf,id);
        HttpResult httpResult = null;
        if(null == id){
            httpResult = HttpResult.createFAIL("请选择广告！");
        }else {
            AdsVo adsVo = adsService.findById(id);
            if(null == adsVo || !adsVo.getIsEnabled()) {
                httpResult = HttpResult.createFAIL("广告不存在，请重试！");
            }else if (adsVo.getAuditStatus() != AdsConstant.ADS.AUDIT_STATUS.PASS_ ){
                httpResult = HttpResult.createFAIL("广告未审核通过，操作失败，请重试！");
            }else {
               switch (shelf){
                   case AdsConstant.ADS.ON_SHELF:
                       if(adsVo.getIsShelf() == AdsConstant.ADS.IS_SHELF.VAILD_){
                           httpResult = HttpResult.createFAIL("该广告已上架，请重复操作！");
                       }else {
                           if(adsService.updateShelf(AdsConstant.ADS.IS_SHELF.VAILD_,id)){
                               httpResult = HttpResult.createSuccess("广告上架成功！");
                           }else {
                               httpResult = HttpResult.createFAIL("广告上架失败！");
                           }
                       }
                       break;
                   case AdsConstant.ADS.UN_SHELF:
                       if(adsVo.getIsShelf() == AdsConstant.ADS.IS_SHELF.INVAILD_){
                           httpResult = HttpResult.createFAIL("该广告已下架，请重复操作！");
                       }else {
                           if(adsService.updateShelf(AdsConstant.ADS.IS_SHELF.INVAILD_,id)){
                               httpResult = HttpResult.createSuccess("广告下架成功！");
                           }else {
                               httpResult = HttpResult.createFAIL("广告下架失败！");
                           }
                       }
                       break;
                   default:
                       httpResult = HttpResult.createFAIL("参数不合法！");
                       break;
               }
            }

        }
        log.info("*** 广告上架or下架结束: shelf(),出参 HttpResult {}",ToStringBuilder.reflectionToString(httpResult));
        return httpResult;

    }

    /**
     * 提交 待审核 方法
     * @param id
     * @return
     */
    @ApiOperation(value = "提交 待审核 方法")
    @RequestMapping(value = "/toAudit/{id}")
    public HttpResult toAudit(@PathVariable("id") Integer id){
        log.info("*** 广告删除开始：toAudit()，入参 id：{}",id);
        HttpResult httpResult;
        if(adsService.toAudit(id)){
            httpResult = HttpResult.createSuccess("提交审核成功！");
        }else {
            httpResult = HttpResult.createSuccess("提交审核失败！");
        }
        log.info("*** 广告删除结束：toAudit()， 出参 HttpResult {}",ToStringBuilder.reflectionToString(httpResult));
        return httpResult;
    }

    /**
     * 广告过期列表查询
     * @param shopId
     * @param page
     * @param rows
     * @return
     */
    @ApiOperation(value = "广告过期列表查询 方法")
    @RequestMapping(value = "renewalList",method = RequestMethod.POST)
    public HttpResult renewalList( Integer shopId,String name,
                            @RequestParam(defaultValue = "1") Integer page,
                            @RequestParam(defaultValue = "20") Integer rows){
        log.info("*** 广告过期列表查询开始：renewalList()，入参 shopId：{},page:{},rows:{}",shopId,page,rows);
        HttpResult httpResult = null;
        if (null == shopId ) {
            httpResult = HttpResult.createFAIL("请选择一个商店！");
        }else {
            httpResult = HttpResult.createSuccess(adsService.renewalList(shopId, name,page, rows));
        }
        log.info("*** 广告过期列表查询结束：renewalList(),出参 HttpResult {}",ToStringBuilder.reflectionToString(httpResult));
        return httpResult;

    }

    @ApiOperation(value = "广告续期")
    @RequestMapping(value = "/renewal",method = RequestMethod.POST)
    public HttpResult renewal(@RequestParam Integer adId,@RequestParam(name = "expiryTime") Date expiryTime){
        log.info("*** 广告续期 开始：renewal(),入参：adId:{},endTime:{}",adId,expiryTime);
        HttpResult httpResult = null;
        if(adsService.renewal(adId, expiryTime)){
            httpResult = HttpResult.createSuccess("续期成功！");
        }else {
            httpResult = HttpResult.createFAIL("续期失败！");
        }
        log.info("*** 广告续期 结束：renewal(),出参 HttpResult {}",ToStringBuilder.reflectionToString(httpResult));
        return httpResult;
    }

}
