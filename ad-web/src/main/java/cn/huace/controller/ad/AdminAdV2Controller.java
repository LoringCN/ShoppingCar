package cn.huace.controller.ad;

import cn.huace.ad.entity.AdAuditV2;
import cn.huace.ad.entity.AdOnlineV2;
import cn.huace.ad.entity.AdV2;
import cn.huace.ad.service.AdAuditV2Service;
import cn.huace.ad.service.AdOnlineV2Service;
import cn.huace.ad.service.AdV2Service;
import cn.huace.ad.util.AdCodeConstants;
import cn.huace.common.bean.HttpResult;
import cn.huace.controller.base.AdminBasicController;
import cn.huace.goods.entity.Goods;
import cn.huace.goods.service.GoodsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * Date:2018/1/22
 */
@Slf4j
@RestController
@Api(value = "/admin/v2/ad",description = "V2版广告管理后台接口")
@RequestMapping(value = "/admin/v2/ad")
public class AdminAdV2Controller extends AdminBasicController{

    @Autowired
    private AdV2Service adV2Service;

    @Autowired
    private AdOnlineV2Service adOnlineV2Service;

    @Autowired
    private AdAuditV2Service adAuditV2Service;


    @Autowired
    private GoodsService goodsService;

    @ApiOperation(value = "查询所有广告、根据状态查询广告、根据类型查询广告")
    @RequestMapping(value = "/list",method = RequestMethod.POST)
    public HttpResult listAds(
            @RequestParam Integer shopId,
            @RequestParam(required = false) Integer adTypeId,
            @RequestParam(required = false) Integer adStatus,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer rows
    ){
        log.info("*** 查询广告列表：listAds()，参数：shopId = {},adTypeId = {},adStatus = {}",shopId,adTypeId,adStatus);
        if(null == shopId){
            return HttpResult.createFAIL("请选择一个商店！");
        }

        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_shop.id",shopId);
        //审核通过广告
        searchMap.put("EQ_audit",AdCodeConstants.AuditStatus.SUCCESS_AUDIT);
        if (null != adTypeId) {
            searchMap.put("EQ_type.id",adTypeId);
        }
        if (null != adStatus) {
            searchMap.put("EQ_status",adStatus);
        } else {
            //未删除广告
            searchMap.put("NE_status", AdCodeConstants.AdStatus.DELETED);
        }
        Page<AdV2> adList = adV2Service.listAds(searchMap,page,rows);

        return HttpResult.createSuccess("查询成功！",sortAdV2Page(adList));
    }

    @ApiOperation(value = "搜索广告列表")
    @RequestMapping(value = "/search",method = RequestMethod.POST)
    public HttpResult searchAllAds(
            @RequestParam Integer shopId,
            @RequestParam String adName,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer rows
    ){
        log.info("*** 搜索广告列表：searchAllAds()，参数：shopId = {},adName = {},page = {},rows = {}",shopId,adName,page,rows);
        if(null == shopId){
            return HttpResult.createFAIL("请选择一个商店！");
        }
        if(StringUtils.isEmpty(adName)){
            return HttpResult.createFAIL("请输入搜索关键字！");
        }

        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_shop.id",shopId);
        searchMap.put("LIKE_name",adName);
        //未删除广告
        searchMap.put("NE_status", AdCodeConstants.AdStatus.DELETED);
        //审核通过广告
        searchMap.put("EQ_audit",AdCodeConstants.AuditStatus.SUCCESS_AUDIT);

        Page<AdV2> adList = adV2Service.listAds(searchMap,page,rows);
        return HttpResult.createSuccess("搜索成功！",sortAdV2Page(adList));
    }

//    @ApiOperation(value = "根据广告类型查询广告列表")
    @RequestMapping(value = "/type",method = RequestMethod.POST)
    public HttpResult listByAdType(
            @RequestParam Integer shopId,
            @RequestParam Integer adTypeId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer rows
    ){
        log.info("*** 根据广告类型查询广告列表：listByAdType()，参数：shopId = {},adTypeId = {},page = {},rows = {}",shopId,adTypeId,page,rows);
        if(null == shopId){
            return HttpResult.createFAIL("请选择一个商店！");
        }
        if(null == adTypeId){
            return HttpResult.createFAIL("请选择广告类型!");
        }

        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_shop.id",shopId);
        searchMap.put("EQ_type.id",adTypeId);
        //审核通过广告
        searchMap.put("EQ_audit", AdCodeConstants.AuditStatus.SUCCESS_AUDIT);
        //未删除广告
        searchMap.put("NE_status",AdCodeConstants.AdStatus.DELETED);

        Page<AdV2> adList = adV2Service.findAdsByType(searchMap,page,rows);

        return HttpResult.createSuccess("查询成功！",sortAdV2Page(adList));
    }

//    @ApiOperation(value = "根据广告状态查询广告列表")
    @RequestMapping(value = "/status",method = RequestMethod.POST)
    public HttpResult listByAdStatus(
            @RequestParam Integer shopId,
            @RequestParam Integer adStatus,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer rows
    ){
        log.info("*** 根据广告状态查询广告列表：listByAdStatus()，参数：shopId = {},adStatus = {},page = {},rows = {}",shopId,adStatus,page,rows);
        if(null == shopId){
            return HttpResult.createFAIL("请选择一个商店！");
        }
        if(null == adStatus){
            return HttpResult.createFAIL("请选择广告状态！");
        }

        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_shop.id",shopId);
        searchMap.put("EQ_status",adStatus);
        //审核通过广告
        searchMap.put("EQ_audit", AdCodeConstants.AuditStatus.SUCCESS_AUDIT);

        Page<AdV2> adList = adV2Service.findAdsByStatus(searchMap,page,rows);

        return HttpResult.createSuccess("查询成功！",sortAdV2Page(adList));
    }

    @ApiOperation(value = "查询待关联商品列表")
    @RequestMapping(value = "/relation/goods/list",method = {RequestMethod.POST})
    public HttpResult listGoodsRelation(
            @RequestParam Integer shopId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer rows
    ){
        log.info("*** 查询待关联商品列表：listGoodsRelation()，参数：shopId = {},page = {},rows = {}",shopId,page,rows);
        if(null == shopId){
            return HttpResult.createFAIL("请选择一个商店！");
        }

        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_shop.id",shopId);
        searchMap.put("EQ_flag","1");

        Page<Goods> goodsPage = goodsService.findAllGoodsForAdmin(searchMap,page,rows);
        return HttpResult.createSuccess("查询成功！",goodsPage);
    }

    @ApiOperation(value = "搜索待关联商品列表")
    @RequestMapping(value = "/relation/goods/search",method = {RequestMethod.POST})
    public HttpResult searchGoodsRelation(
            @RequestParam Integer shopId,
            @RequestParam String goodsName,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer rows
    ){
        log.info("*** 搜索待关联商品列表：searchGoodsRelation()，参数：shopId = {},page = {},rows = {}",shopId,page,rows);
        if(null == shopId){
            return HttpResult.createFAIL("请选择一个商店！");
        }
        if(StringUtils.isEmpty(goodsName)){
            return HttpResult.createFAIL("请输入要搜索的商品名！");
        }

        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_shop.id",shopId);
        searchMap.put("LIKE_title",goodsName);
        searchMap.put("EQ_flag","1");

        Page<Goods> goodsPage = goodsService.findAllGoodsForAdmin(searchMap,page,rows);
        return HttpResult.createSuccess("搜索成功！",goodsPage);
    }

    @ApiOperation(value = "根据广告id查询广告详情")
    @RequestMapping(value = "/{id}/detail",method = RequestMethod.GET)
    public HttpResult detail(@PathVariable("id") Integer id){
        log.info("*** 查询广告详情：detail(),adId = {}",id);
        AdV2 ad = adV2Service.findOne(id);
        if (ad == null) {
            return HttpResult.createFAIL("广告不存在！");
        }
        // 取出广告关联商品名字
        if (ad.getRelation().getCode() == AdCodeConstants.AdV2Relation.GOODS_RELATION) {
            String goodId = ad.getExtra();
            setGoodsName(goodId,ad);

        }
        if (ad.getRelation().getCode() == AdCodeConstants.AdV2Relation.GOODS_AND_SHELF) {
            String extra = ad.getExtra();
            if (!StringUtils.isEmpty(extra)) {
                String[] arr = StringUtils.split(extra,",");
                log.info("******* 关联商品Id = {}",arr[0]);
                setGoodsName(arr[0],ad);
            }
        }
        return HttpResult.createSuccess("查询成功！",ad);
    }

    @ApiOperation(value = "保存、修改广告")
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public HttpResult save( AdV2 adV2,@RequestParam String adPrice){
        log.info("*** 添加、修改广告：save()");
        // 广告价格转为分
        if (!StringUtils.isEmpty(adPrice)) {
            adV2.setPrice(new BigDecimal(adPrice).movePointRight(2).intValue());
        }
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

        switch (adCheck(adV2)){
            case 1:
                return HttpResult.createFAIL("广告校验失败！");//   break;
            case 2:
                return HttpResult.createFAIL("广告对象为空不能保存！");//  break;
            case 3:
                return HttpResult.createFAIL("LBS广告关联关系必须为【商品和货架】");//  break;
            case 4:
                return HttpResult.createFAIL("LBS广告关联关系必须选择 商品和货架");//  break;
        }


        if(adV2.isNew()){
            //检查广告是否存在
            boolean isExistAd = adV2Service.isExist(adV2.getShop().getId(),adV2.getName());
            if(isExistAd){
                return HttpResult.createFAIL("广告名不能重复！");
            }
            if(StringUtils.isEmpty(adV2.getExtra()) || "null".equalsIgnoreCase(adV2.getExtra())){
                adV2.setExtra(null);
            }
            //添加进入待审核状态
            adV2.setAudit(new Byte(String.valueOf(AdCodeConstants.AuditStatus.TO_AUDIT)));
            //默认下线状态
            adV2.setVoted(AdCodeConstants.OFFLINE_AD);
            //默认下架状态
            adV2.setStatus(new Byte(String.valueOf(AdCodeConstants.AdStatus.UNSHELF)));

            AdV2 result = adV2Service.save(adV2);
            if(result != null){
                //添加审核记录
                AdAuditV2 audit = new AdAuditV2();
                audit.setAdId(result.getId());
                audit.setShopId(result.getShop().getId());
                audit.setUserId(getCurrentUser().getId());
                audit.setStatus(new Byte(String.valueOf(AdCodeConstants.AuditStatus.TO_AUDIT)));

                adAuditV2Service.save(audit);

                return HttpResult.createSuccess("添加广告成功！");
            }
        }else{
            AdV2 old = adV2Service.findOne(adV2.getId());
            // 已上线正式广告不能修改为默认广告，防止已上线正式广告被修改
            if (old.getVoted() && adV2.getFlag()) {
                return HttpResult.createFAIL("已上线广告不能修改为默认广告！");
            }
            old.setUrl(StringUtils.isEmpty(adV2.getUrl())?old.getUrl():adV2.getUrl());
            old.setMd5(StringUtils.isEmpty(adV2.getMd5())?old.getMd5():adV2.getMd5());
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
            if(result != null){
                return HttpResult.createSuccess("修改广告成功！");
            }
        }
        return HttpResult.createFAIL("操作失败！");
    }

    @ApiOperation(value = "删除广告")
    @RequestMapping(value = "/{id}/delete",method = RequestMethod.GET)
    public HttpResult delete(@PathVariable("id") Integer id){
        log.info("*** 删除广告：delete()，参数：adId = {}",id);
        AdV2 ad = adV2Service.findOne(id);
        if(ad == null){
            return HttpResult.createFAIL("广告不存在！");
        }
        //删除前检查广告是否为上线广告，如果是必须先进行下线操作，然后才能删除
        if(ad.getVoted()){
            return HttpResult.createFAIL("请先下线该广告，然后再删除！");
        }
        ad.setStatus(new Byte(String.valueOf(AdCodeConstants.AdStatus.DELETED)));

        AdV2 result = adV2Service.save(ad);
        if(result == null){
            return HttpResult.createFAIL("删除广告失败!");
        }
        return HttpResult.createSuccess("删除成功！");
    }

    @ApiOperation(value = "下架广告")
    @RequestMapping(value = "/{id}/unShelf",method = RequestMethod.GET)
    public HttpResult unShelf(@PathVariable("id") Integer id){
        log.info("*** unShelf()，参数：adId = {}",id);
        AdV2 ad = adV2Service.findOne(id);
        if(ad == null){
            return HttpResult.createFAIL("广告不存在，下架失败！");
        }
        if(ad.getVoted()){
            return HttpResult.createFAIL("请先下线该广告，然后再下架！");
        }
        if(AdCodeConstants.AdStatus.UNSHELF == ad.getStatus().intValue()){
            return HttpResult.createFAIL("该广告已被下架，请勿重复操作！");
        }
        ad.setStatus(new Byte(String.valueOf(AdCodeConstants.AdStatus.UNSHELF)));

        AdV2 result = adV2Service.save(ad);
        if(result == null){
            return HttpResult.createFAIL("下架广告失败，请联系管理员！");
        }
        //删除该广告上线记录
        List<AdOnlineV2> adOnlineV2List = adOnlineV2Service.findByAdIdAndShopId(id,ad.getShop().getId());
        if(adOnlineV2List != null){
            adOnlineV2Service.delete(adOnlineV2List);
        }
        return HttpResult.createSuccess("下架广告成功！");
    }
    @ApiOperation(value = "上架广告")
    @RequestMapping(value = "/{id}/shelf",method = RequestMethod.GET)
    public HttpResult shelf(@PathVariable("id") Integer id) {
        log.info("*** 上架广告：shelf()，参数：adId = {}", id);
        AdV2 ad = adV2Service.findOne(id);
        if(ad == null){
            return HttpResult.createFAIL("广告不存在，上架失败！");
        }
        //审核通过前，不能上架
        if(ad.getAudit().intValue() != AdCodeConstants.AuditStatus.SUCCESS_AUDIT){
            return HttpResult.createFAIL("广告上架前，必须先审核通过");
        }
        ad.setStatus(new Byte(String.valueOf(AdCodeConstants.AdStatus.NORMAL)));

        AdV2 result = adV2Service.save(ad);
        if(result == null){
            return HttpResult.createFAIL("上架失败！");
        }
        return HttpResult.createSuccess("上架成功！");
    }

    private void setGoodsName(String goodId, AdV2 ad) {
        if (!StringUtils.isEmpty(goodId)) {
            Goods goods = goodsService.findGoodsDetailForAdmin(Integer.parseInt(goodId));
            if (goods != null) {
                ad.setGoodsName(goods.getTitle());
            }
        }
    }
    /**
     * 自定义排序：
     * 1.内部广告展示在前面，外部广告在后
     * 2.同顺序按最新修改时间modifiedTime倒序，实现最新修改显示在前面
     * @param adV2Page 待排序数据
     * @return
     */
    private Page<AdV2> sortAdV2Page(Page<AdV2> adV2Page){
        if(adV2Page == null || adV2Page.getContent().size() == 0){
            return adV2Page;
        }
        //adV2List查询时已拍过序，因此直接使用sort排序会报错
        List<AdV2> adV2List = adV2Page.getContent();
        List<AdV2> sortList = new ArrayList<>(adV2List);
        //自定义排序，内部广告展示在前面，外部广告按modified_time倒序
        sortList.sort((AdV2 o1, AdV2 o2) -> {
                if(o1.getFlag() && !o2.getFlag()){
                    return -1;
                }else if (!o1.getFlag() && o2.getFlag()){
                    return 1;
                }else {
                    //时间倒序
                    long olModifiedTime = o1.getModifiedTime().getTime();
                    long o2ModifiedTime = o2.getModifiedTime().getTime();
                    if(olModifiedTime > o2ModifiedTime){
                        return -1;
                    }
                    if(olModifiedTime < o2ModifiedTime){
                        return 1;
                    }
                    return 0;
                }
        });

        return new PageImpl<AdV2>(sortList,null,adV2Page.getTotalElements());
    }

    /**
     * 广告校验 方法
     * @param adV2
     * @return
     */
    private Integer adCheck(AdV2 adV2){
        /**
         * 校验步骤：
         *  1、必填项校验 暂时先不加
         *  2、逻辑校验
         *     a、LBS广告必须关联货架和商品
         *     b、关联货架和商品是，关联值须正确。
         */
        if(null != adV2){
            if(adV2.getType().getCode() == AdCodeConstants.AdV2Type.TYPE_CODE_LBS){
                if(adV2.getRelation().getCode() == AdCodeConstants.AdV2Relation.GOODS_AND_SHELF){
                    String[] arr;
                    if(StringUtils.isNotBlank(adV2.getExtra())){
                        arr = adV2.getExtra().split(",");
                    }else {
                        arr = null;
                    }
                    if(arr.length == 2 && StringUtils.isNotBlank(arr[0]) && StringUtils.isNotBlank(arr[1])){
                        return 0;
                        //校验通过
                    }else{
                     //  return HttpResult.createFAIL("LBS广告关联关系必须选择 商品和货架 ");
                        return 4;
                    }
                }else{
//                    return HttpResult.createFAIL("LBS广告关联关系必须为【商品和货架】");
                    return 3;
                }
            }else {
                //广告校验通过！
                return 0;
            }
        }else{
//            return HttpResult.createFAIL("对象为空不能保存！");
              return 2;
        }

    }

}
