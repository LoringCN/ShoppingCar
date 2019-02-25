package cn.huace.controller.ad;

import cn.huace.ad.entity.AdAuditV2;
import cn.huace.ad.entity.AdV2;
import cn.huace.ad.service.AdAuditV2Service;
import cn.huace.ad.service.AdV2Service;
import cn.huace.ad.util.AdCodeConstants;
import cn.huace.common.bean.HttpResult;
import cn.huace.controller.base.AdminBasicController;
import cn.huace.sys.bean.ShiroUser;
import cn.huace.sys.entity.SystemUser;
import cn.huace.sys.service.SystemUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

/**
 * 广告审核
 * Date:2018/1/24
 */
@Slf4j
@RestController
@Api(value = "/admin/v2/ad/audit",description = "新版广告管理后台接口")
@RequestMapping(value = "/admin/v2/ad/audit")
public class AdV2AuditController extends AdminBasicController{

    @Autowired
    private AdV2Service adV2Service;

    @Autowired
    private AdAuditV2Service adAuditV2Service;

    @Autowired
    private SystemUserService systemUserService;

    @ApiOperation(value = "查询待审核或审核失败广告列表")
    @RequestMapping(value = "/list",method = RequestMethod.POST)
    public HttpResult listToAuditAds(
            @RequestParam Integer shopId,
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer rows
    ){
        log.info("*** 查询待审核或审核失败广告列表：listToAuditAds()，参数：shopId = {},auditStatus = {},page = {},rows = {}",shopId,auditStatus,page,rows);
        if(StringUtils.isEmpty(shopId)){
            return HttpResult.createFAIL("请选择一个商店！");
        }
        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_shop.id",shopId);
        if (!StringUtils.isEmpty(auditStatus)) {
            searchMap.put("EQ_audit",auditStatus);
        }
        Page<AdV2> toAuditAds = adV2Service.listToAuditOrFailureAuditAds(searchMap,page,rows);

        return HttpResult.createSuccess("查询成功！",toAuditAds);
    }

    @ApiOperation(value = "搜索待审核或审核失败广告列表")
    @RequestMapping(value = "/search",method = RequestMethod.POST)
    public HttpResult searchToAuditAds(
            @RequestParam Integer shopId,
            @RequestParam String adName,
            @RequestParam(required = false) Integer auditStatus,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer rows
    ){
        log.info("*** 搜索待审核或审核失败广告：searchToAuditAds()，参数：shopId = {},adName = {},auditStatus = {},page = {},rows = {}",shopId,adName,auditStatus,page,rows);
        if (StringUtils.isEmpty(shopId)) {
            return HttpResult.createFAIL("请选择一个商店！");
        }
        if (StringUtils.isEmpty(adName)) {
            return HttpResult.createFAIL("请输入要搜索的广告名！");
        }
        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_shop.id",shopId);
        searchMap.put("LIKE_name",adName);
        if (!StringUtils.isEmpty(auditStatus)) {
            searchMap.put("EQ_audit",auditStatus);
        }

        Page<AdV2> toAuditAds = adV2Service.listToAuditOrFailureAuditAds(searchMap,page,rows);

        return HttpResult.createSuccess("查询成功！",toAuditAds);
    }

    @ApiOperation(value = "查询审核失败原因")
    @RequestMapping(value = "/failure/reason",method = {RequestMethod.GET,RequestMethod.POST})
    public HttpResult auditFailureReason(@RequestParam Integer adId){
        log.info("****** 查询审核失败原因:auditFailureReason(),参数：adId = {}",adId);
        // 查询该广告所有审核失败记录
        List<AdAuditV2> AdAuditV2s = adAuditV2Service.findAuditFailureList(adId);
        if(!CollectionUtils.isEmpty(AdAuditV2s)){
            // 查询所有用户信息
            Set<Integer> userIds = new HashSet<>();
            for (AdAuditV2 audit : AdAuditV2s) {
                userIds.add(audit.getUserId());
            }
            List<SystemUser> userList = systemUserService.findAll(new ArrayList<>(userIds));
            for (SystemUser user : userList) {
                for (AdAuditV2 audit : AdAuditV2s) {
                    if(user.getId().intValue() == audit.getUserId().intValue()){
                        audit.setUser(user);
                    }
                }
            }
        }

        return HttpResult.createSuccess("查询成功！",AdAuditV2s);
    }

    @ApiOperation(value = "审核通过")
    @RequestMapping(value = "/success",method = RequestMethod.GET)
    public HttpResult auditSuccess(@RequestParam Integer adId){
        log.info("******* 审核通过：auditSuccess(),参数：adId = {}",adId);
        if(StringUtils.isEmpty(adId)){
            return HttpResult.createFAIL("参数【adId】不能为空！");
        }

        AdV2 ad = adV2Service.findOne(adId);
        if(ad == null){
            return HttpResult.createFAIL("广告不存在！");
        }
        int shopId = ad.getShop().getId();
        Byte auditStatus = new Byte(String.valueOf(AdCodeConstants.AuditStatus.SUCCESS_AUDIT));
        //更新ad审核状态
        ad.setAudit(auditStatus);
        //上架
        ad.setStatus(new Byte(String.valueOf(AdCodeConstants.AdStatus.NORMAL)));

        AdV2 result = adV2Service.save(ad);
        if(result == null){
            return HttpResult.createFAIL("操作失败！");
        }

        //记录审核日志
        AdAuditV2 entity = create(shopId,adId,auditStatus,null);
        AdAuditV2 auditResult = adAuditV2Service.save(entity);
        if(auditResult != null){
            log.info("********* 记录广告【审核通过】日志成功！adId = {}",adId);
        }

        return HttpResult.createSuccess("审核通过！");
    }

    @ApiOperation(value = "审核未通过")
    @RequestMapping(value = "/failure",method = RequestMethod.POST)
    public HttpResult auditFailure(
            @RequestParam Integer adId,
            @RequestParam String reason
    ){
        log.info("******* 审核未通过：auditFailure(),参数：adId = {}，reason = {}",adId,reason);
        if(StringUtils.isEmpty(adId)){
            return HttpResult.createFAIL("参数【adId】不能为空！");
        }
        if(StringUtils.isEmpty(reason)){
            return HttpResult.createFAIL("必须填写【审核不通过】原因！");
        }

        AdV2 ad = adV2Service.findOne(adId);
        if(ad == null){
            return HttpResult.createFAIL("广告不存在！");
        }
        int shopId = ad.getShop().getId();
        Byte auditStatus = new Byte(String.valueOf(AdCodeConstants.AuditStatus.FAILURE_AUDIT));
        //更新ad状态
        ad.setAudit(auditStatus);

        AdV2 result = adV2Service.save(ad);
        if(result == null){
            return HttpResult.createFAIL("操作失败！");
        }

        //记录审核日志
        AdAuditV2 entity = create(shopId,adId,auditStatus,reason);
        AdAuditV2 auditResult = adAuditV2Service.save(entity);
        if(auditResult != null){
            log.info("********* 记录广告【审核不通过】日志成功！adId = {}",adId);
        }
        return HttpResult.createSuccess("操作成功！");
    }

    @ApiOperation(value = "重新提交审核")
    @RequestMapping(value = "/retry",method = RequestMethod.GET)
    public HttpResult retryAudit(@RequestParam Integer adId){
        log.info("******* 重新提交审核：retryAudit(),参数：adId = {}",adId);
        AdV2 ad = adV2Service.findOne(adId);
        if(ad == null){
            return HttpResult.createFAIL("广告不存在！");
        }
        int shopId = ad.getShop().getId();
        Byte auditStatus = new Byte(String.valueOf(AdCodeConstants.AuditStatus.TO_AUDIT));
        //更新广告
        ad.setAudit(auditStatus);

        AdV2 result = adV2Service.save(ad);
        if(result == null){
            return HttpResult.createFAIL("提交失败");
        }

        //记录审核日志
        AdAuditV2 audit = create(shopId,adId,auditStatus,null);
        AdAuditV2 auditResult = adAuditV2Service.save(audit);
        if(auditResult != null){
            log.info("********* 记录广告【重新提交审核】日志成功！adId = {}",adId);
        }
        return HttpResult.createSuccess("重新提交审核成功！");
    }

    @ApiOperation(value = "修改待审核广告")
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public HttpResult auditUpdate( AdV2 adV2,@RequestParam String adPrice ) {
        log.info("***** 修改待审核广告：auditUpdate(),参数：adId = {}",adV2.getId());
        AdV2 old = adV2Service.findOne(adV2.getId());
        // 检查LBS广告关联关系是否为：商品和货架
        if (adV2.getType().getCode() == AdCodeConstants.AdV2Type.TYPE_CODE_LBS
                && adV2.getRelation().getCode() != AdCodeConstants.AdV2Relation.GOODS_AND_SHELF) {
            return HttpResult.createFAIL("LBS广告关联关系必须为【商品和货架】");
        }
        // 其他广告关联关系不能为：商品和货架
        if (adV2.getType().getCode() != AdCodeConstants.AdV2Type.TYPE_CODE_LBS
                && adV2.getRelation().getCode() == AdCodeConstants.AdV2Relation.GOODS_AND_SHELF) {
            return HttpResult.createFAIL("广告类型：【"+adV2.getType().getName()+"】关联关系不能为【商品和货架】");
        }
        // 广告价格转为分
        if (!StringUtils.isEmpty(adPrice)) {
            adV2.setPrice(new BigDecimal(adPrice).movePointRight(2).intValue());
        }
        old.setUrl(StringUtils.isEmpty(adV2.getUrl())?old.getUrl():adV2.getUrl());
        old.setMd5(StringUtils.isEmpty(adV2.getMd5())?old.getMd5():adV2.getMd5());
        //此处编辑，广告审核状态自动改为重新审核
        old.setAudit(new Byte(String.valueOf(AdCodeConstants.AuditStatus.RE_AUDIT)));
        old.setName(adV2.getName());
        old.setType(adV2.getType());
        old.setRelation(adV2.getRelation());
        old.setActiveTime(adV2.getActiveTime());
        old.setOverdueTime(adV2.getOverdueTime());
        old.setDescription(adV2.getDescription());
        old.setGroup(adV2.getGroup());
        old.setPrice(adV2.getPrice());
        //保证无关联时，extra字段都为null，即可以通过 IS NULL全部能查到，而不用考虑"null",""情况
        if(StringUtils.isEmpty(adV2.getExtra()) || "null".equalsIgnoreCase(adV2.getExtra())){
            adV2.setExtra(null);
        }
        old.setExtra(adV2.getExtra());
        old.setFlag(adV2.getFlag());

        AdV2 result = adV2Service.save(old);
        if(result == null){
            return HttpResult.createFAIL("修改失败！");
        }

        return HttpResult.createSuccess("修改成功！");
    }

    private AdAuditV2 create(Integer shopId,Integer adId,Byte status,String reason){
        AdAuditV2 entity = new AdAuditV2();
        ShiroUser user = getCurrentUser();
        entity.setUserId(user.getId());
        entity.setShopId(shopId);
        entity.setAdId(adId);
        entity.setStatus(status);
        if(!StringUtils.isEmpty(reason)){
            entity.setReason(reason);
        }
        return entity;
    }
}
