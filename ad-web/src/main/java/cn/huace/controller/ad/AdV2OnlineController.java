package cn.huace.controller.ad;

import cn.huace.ad.entity.AdOnlineV2;
import cn.huace.ad.entity.AdTypeV2;
import cn.huace.ad.entity.AdV2;
import cn.huace.ad.service.AdOnlineV2Service;
import cn.huace.ad.service.AdTypeV2Service;
import cn.huace.ad.service.AdV2Service;
import cn.huace.ad.util.AdCodeConstants;
import cn.huace.common.bean.HttpResult;
import cn.huace.common.utils.DateUtils;
import cn.huace.controller.base.AdminBasicController;
import cn.huace.shop.device.entity.Device;
import cn.huace.shop.device.service.DeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * 线上广告入口
 * Date:2018/1/23
 */
@Slf4j
@RestController
@Api(value = "/admin/v2/ad/online",description = "新版广告管理后台接口")
@RequestMapping(value = "/admin/v2/ad/online")
public class AdV2OnlineController extends AdminBasicController{

    @Autowired
    private AdV2Service adV2Service;

    @Autowired
    private AdOnlineV2Service adOnlineV2Service;

    @Autowired
    private AdTypeV2Service adTypeV2Service;

    @Autowired
    private DeviceService deviceService;


    @ApiOperation(value = "查询线上广告列表，默认加载视频广告")
    @RequestMapping(value = "/list",method = RequestMethod.POST)
    public HttpResult listOnlineAds(
            @RequestParam Integer shopId,
            @RequestParam Integer adTypeId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer rows
    ){
        log.info("*** 查询线上广告列表：listOnlineAds()，参数：shopId = {},adTypeId = {},page = {},rows = {}",shopId,adTypeId,page,rows);
        if(StringUtils.isEmpty(shopId)){
            return HttpResult.createFAIL("请选择一个商店！");
        }
        if(StringUtils.isEmpty(adTypeId)){
            return HttpResult.createFAIL("请选择广告类型！");
        }

        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_shop.id",shopId);
        searchMap.put("EQ_type.id",adTypeId);
        //线上广告
        searchMap.put("EQ_voted", AdCodeConstants.ONLINE_AD);
        // 外部广告
        searchMap.put("EQ_flag",AdCodeConstants.OUTER_AD);
        searchMap.put("NE_status",AdCodeConstants.AdStatus.DELETED);

        Page<AdV2> onlineAdsPage = adV2Service.findOnlineAds(searchMap,page,rows);

        return HttpResult.createSuccess("查询成功！",sortAdV2Page(onlineAdsPage));
    }

    @ApiOperation(value = "搜索线上广告列表")
    @RequestMapping(value = "/search",method = RequestMethod.POST)
    public HttpResult searchOnlineAds(
            @RequestParam Integer shopId,
            @RequestParam String adName,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer rows
    ){
        log.info("*** 搜索线上广告列表：searchOnlineAds()，参数：shopId = {},adName = {},page = {},rows = {}",shopId,adName,page,rows);
        if(StringUtils.isEmpty(shopId)){
            return HttpResult.createFAIL("请选择一个商店！");
        }
        if(StringUtils.isEmpty(adName)){
            return HttpResult.createFAIL("请输入要搜索的广告名！");
        }

        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_shop.id",shopId);
        searchMap.put("LIKE_name",adName);
        //线上广告
        searchMap.put("EQ_voted", AdCodeConstants.ONLINE_AD);
        // 外部广告
        searchMap.put("EQ_flag",AdCodeConstants.OUTER_AD);
        searchMap.put("NE_status",AdCodeConstants.AdStatus.DELETED);

        Page<AdV2> onlineAdsPage = adV2Service.findOnlineAds(searchMap,page,rows);

        return HttpResult.createSuccess("搜索成功！",sortAdV2Page(onlineAdsPage));
    }

    @ApiOperation(value = "查询上架广告列表")
    @RequestMapping(value = "/shelf/list",method = RequestMethod.POST)
    public HttpResult listShelfAds(
            @RequestParam Integer shopId,
            @RequestParam Integer adTypeId
    ){
        log.info("*** 查询上架广告列表：listShelfAds()，参数：shopId = {},adTypeId = {}",shopId,adTypeId);
        if(StringUtils.isEmpty(shopId)){
            return HttpResult.createFAIL("请选择一个商店！");
        }
        if(StringUtils.isEmpty(adTypeId)){
            return HttpResult.createFAIL("请先选择一个广告类型！");
        }

        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_shop.id",shopId);
        searchMap.put("EQ_type.id",adTypeId);
        searchMap.put("EQ_status",AdCodeConstants.AdStatus.NORMAL);
        searchMap.put("EQ_audit",AdCodeConstants.AuditStatus.SUCCESS_AUDIT);
        // 外部广告
        searchMap.put("EQ_flag",AdCodeConstants.OUTER_AD);
        // 排除该类型已上线广告
        searchMap.put("EQ_voted",AdCodeConstants.OFFLINE_AD);
        // 广告到期时间必须大于当前时间
        searchMap.put("GT_overdueTime",new Date());

        //根据类型查询所有已上架广告
        List<AdV2> adV2List = adV2Service.findShelfAds(searchMap);
        return HttpResult.createSuccess("查询成功！",adV2List);
    }

    @ApiOperation(value = "根据广告类型查询线上广告")
    @RequestMapping(value = "/type",method = RequestMethod.POST)
    public HttpResult listOnlineAdsByAdType(
            @RequestParam Integer shopId,
            @RequestParam Integer adTypeId,
            @RequestParam(required = false) Integer rank,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer rows
    ){
        log.info("*** 根据广告类型查询线上广告：listOnlineAdsByAdType()，参数：shopId = {},adTypeId = {},rank = {},page = {},rows = {}",shopId,adTypeId,rank,page,rows);
        if(StringUtils.isEmpty(shopId)){
            return HttpResult.createFAIL("请选择一个商店！");
        }
        if(StringUtils.isEmpty(adTypeId)){
            return HttpResult.createFAIL("请选择要查询的广告类型!");
        }

        AdTypeV2 adType = adTypeV2Service.findOne(adTypeId);
        if(adType.getCode() == AdCodeConstants.AdV2Type.TYPE_CODE_INDEX_CAROUSEL
            || adType.getCode() == AdCodeConstants.AdV2Type.TYPE_CODE_NEW_RECOMMEND_CAROUSEL){
            // 轮播广告查询必须带rank值
            if(StringUtils.isEmpty(rank)){
                return HttpResult.createFAIL("查询轮播广告，广告位置参数【rank】必须有值！");
            }
            log.info("***** 查询超市：shopId = {},广告位：rank = {}",shopId,rank);
            // 查询超市对应广告位所有线上广告id
            List<Integer> adIds = adOnlineV2Service.findAdIdsByShopIdAndRank(shopId,adTypeId,rank);
            if(!CollectionUtils.isEmpty(adIds)){
                Page<AdV2> ads =  adV2Service.findAdsByAdIds(adIds,page,rows);
                return HttpResult.createSuccess("查询成功！",sortAdV2Page(ads));
            }
            return HttpResult.createSuccess("查询成功！",new PageImpl<AdV2>(new ArrayList<>()));
        } else {
            // 非轮播广告
            // 查询超市对应广告位所有线上广告id
            List<Integer> adIds = adOnlineV2Service.findAdIdsByShopIdAndTypeId(shopId,adTypeId);
            if(!CollectionUtils.isEmpty(adIds)){
                Page<AdV2> ads =  adV2Service.findAdsByAdIds(adIds,page,rows);
                return HttpResult.createSuccess("查询成功！",sortAdV2Page(ads));
            }
            return HttpResult.createSuccess("查询成功！",new PageImpl<AdV2>(new ArrayList<>()));
        }
    }

    @ApiOperation(value = "查询投放设备列表")
    @RequestMapping(value = "/device/list",method = {RequestMethod.GET,RequestMethod.POST})
    public HttpResult listDevices(@RequestParam Integer adId){
        log.info("******* 查询投放设备列表：listDevices(),参数：adId = {}",adId);
        AdV2 ad = adV2Service.findOne(adId);
        if(ad == null){
            return HttpResult.createFAIL("广告不存在！");
        }

        int shopId = ad.getShop().getId();
        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_shop.id",shopId);
        //查询该商店所有设备
        List<Device> allDevices = deviceService.findAll(searchMap);

        List<Integer> devIdList = adOnlineV2Service.findDevIdsByShopIdAndAdId(shopId,adId);
        List<Device> selectedDevices = new ArrayList<>();
        if(devIdList != null && !devIdList.isEmpty()){
            //查询该广告已选投放设备列表
            selectedDevices = deviceService.findAll(devIdList);
        }
        //将已选设备从所有列表中移除
        allDevices.removeAll(selectedDevices);
        //封装返回数据
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("allDevice",allDevices);
        resultMap.put("selectedDevice",selectedDevices);

        return HttpResult.createSuccess("查询成功！",resultMap);
    }

    @ApiOperation(value = "上线广告")
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public HttpResult saveOnlineAd(
            @RequestParam Integer adId,
            @RequestParam Integer[] devIds,
            @RequestParam(required = false,defaultValue = "0") Integer rank
    ){
        log.info("*** 上线广告：saveOnlineAd()，参数：adId = {},devIdList = {},rank = {}",adId,Arrays.asList(devIds),rank);
        if(StringUtils.isEmpty(adId)){
            return HttpResult.createFAIL("请选择一个广告！");
        }
        if(devIds.length == 0 ){
            return HttpResult.createFAIL("请选择广告投放范围！");
        }

        AdV2 ad = adV2Service.findOne(adId);
        if(ad == null){
            return HttpResult.createFAIL("广告不存在！");
        }
        int shopId = ad.getShop().getId();
        int typeId = ad.getType().getId();

        if (devIds[0] == -1) {
            Boolean hasDefaultAd = checkDefaultAdOnlineV2(shopId,typeId,rank,devIds[0],ad);
            if (hasDefaultAd) {
                return HttpResult.createFAIL("该广告位只能有一个默认广告！");
            }
        }

        //新增线上广告
        List<AdOnlineV2> onlineAdList = handleDevIds(Arrays.asList(devIds),shopId,adId,typeId,rank);
        //删除该广告已存在记录
        List<AdOnlineV2> old = adOnlineV2Service.findByAdIdAndShopId(adId,shopId);
        if(old != null && !old.isEmpty()){
            adOnlineV2Service.batchDelete(old);
        }
        Boolean result = adOnlineV2Service.batchSave(onlineAdList);
        if (!result) {
            log.info("***** 上线广告失败！");
            return HttpResult.createFAIL("上线失败！");
        }
        //更新ad上线状态及投放方式
        int deliverMethod =
                (devIds[0] == -1) ? AdCodeConstants.DeliverMethod.DELIVER_DEFAULT:AdCodeConstants.DeliverMethod.DELIVER_DEVICE;
        ad.setDeliverMethod(new Byte(String.valueOf(deliverMethod)));
        ad.setVoted(AdCodeConstants.ONLINE_AD);
        AdV2 updateResult = adV2Service.save(ad);
        if(updateResult != null){
            log.info("******* 更新广告voted状态为【线上】,adId = {},deliverMethod = {}",adId,deliverMethod);
        }
        return HttpResult.createSuccess("上线成功！");
    }

    @ApiOperation(value = "修改线上广告")
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public HttpResult updateOnlineAd(
            @RequestParam Integer adId,
            @RequestParam Integer[] devIds,
            @RequestParam(required = false,defaultValue = "0") Integer rank
    ){
        log.info("*** 修改线上广告：updateOnlineAd()，参数：adId = {},devIdList = {},rank = {}",adId,Arrays.asList(devIds),rank);
        if(StringUtils.isEmpty(adId)){
            return HttpResult.createFAIL("请选择一个广告！");
        }
        if(devIds.length == 0 ){
            return HttpResult.createFAIL("请选择广告投放范围！");
        }

        AdV2 ad = adV2Service.findOne(adId);
        if(ad == null){
            return HttpResult.createFAIL("广告不存在！");
        }
        int shopId = ad.getShop().getId();
        int typeId = ad.getType().getId();

        if (devIds[0] == -1) {
            Boolean hasDefaultAd = checkDefaultAdOnlineV2(shopId,typeId,rank,devIds[0],ad);
            if (hasDefaultAd) {
                return HttpResult.createFAIL("该广告位只能有一个默认广告！");
            }
        }

        //修改线上广告
        List<AdOnlineV2> onlineAdList = handleDevIds(Arrays.asList(devIds),shopId,adId,typeId,rank);
        //删除该广告已存在记录
        List<AdOnlineV2> old = adOnlineV2Service.findByAdIdAndShopId(adId,shopId);
        if(old != null && !old.isEmpty()){
            adOnlineV2Service.batchDelete(old);
        }
        Boolean result = adOnlineV2Service.batchSave(onlineAdList);
        if (!result) {
            log.info("***** 修改线上广告失败！");
            return HttpResult.createFAIL("修改失败！");
        }
        //更新广告投放方式
        int deliverMethod =
                (devIds[0] == -1) ? AdCodeConstants.DeliverMethod.DELIVER_DEFAULT:AdCodeConstants.DeliverMethod.DELIVER_DEVICE;
        ad.setDeliverMethod(new Byte(String.valueOf(deliverMethod)));
        adV2Service.save(ad);

        return HttpResult.createSuccess("修改成功！");
    }

    @ApiOperation(value = "下线线上广告")
    @RequestMapping(value = "/{id}/off",method = RequestMethod.GET)
    public HttpResult offlineAd(@PathVariable("id") Integer id){
        log.info("******　下线线上广告：offlineAd(),参数：adId = {}",id);
        AdV2 ad = adV2Service.findOne(id);
        if(ad == null){
            return HttpResult.createFAIL("广告不存在！");
        }
        if(!ad.getVoted()){
            return HttpResult.createFAIL("该广告已被下线，请勿重复操作！");
        }
        ad.setVoted(AdCodeConstants.OFFLINE_AD);
        //投放方式清空
        ad.setDeliverMethod(null);

        AdV2 result = adV2Service.save(ad);
        if(result == null){
            return HttpResult.createFAIL("下线广告失败，请联系管理员！");
        }
        //删除该广告上线记录
        List<AdOnlineV2> adOnlineV2List = adOnlineV2Service.findByAdIdAndShopId(id,ad.getShop().getId());
        if(adOnlineV2List != null){
            adOnlineV2Service.delete(adOnlineV2List);
        }
        return HttpResult.createSuccess("下线广告成功！");
    }

    private Boolean checkDefaultAdOnlineV2(int shopId, int typeId, Integer rank,int defaultDevId,AdV2 ad) {
        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_shopId",shopId);
        searchMap.put("EQ_typeId",typeId);
        searchMap.put("EQ_rank",rank);
        searchMap.put("EQ_devId",defaultDevId);
        // 根据typeId检查是否已有该类型默认广告，保证除了轮播广告位外其他广告位只能存在一个默认投放范围线上广告
        return adOnlineV2Service.checkDefaultAdOnlineV2(searchMap,ad);
    }
    /**
     * 将devIds转化成AdOnlineV2列表
     */
    private List<AdOnlineV2> handleDevIds(List<Integer> devIdList,Integer shopId,Integer adId,Integer typeId,Integer rank){
        List<AdOnlineV2> entityList = new ArrayList<>();
        int first = devIdList.get(0);
        if(first == -1){
            AdOnlineV2 entity = createAdOnlineV2(shopId,adId,first,typeId,rank);
            entityList.add(entity);

            return entityList;
        }else{
            //投放范围：指定设备
            for(Integer devId:devIdList){
                if(StringUtils.isEmpty(devId)){
                    continue;
                }
                AdOnlineV2 entity = createAdOnlineV2(shopId,adId,devId,typeId,rank);
                entityList.add(entity);
            }
            return entityList;
        }
    }

    /**
     * 生成线上广告实体
     */
    private AdOnlineV2 createAdOnlineV2(Integer shopId,Integer adId,Integer devId,Integer typeId,Integer rank){
        AdOnlineV2 entity = new AdOnlineV2();
        entity.setShopId(shopId);
        entity.setAdId(adId);
        entity.setDevId(devId);
        entity.setRank(rank);
        entity.setTypeId(typeId);
        return entity;
    }

    /**
     *
     */
    private Page<AdV2> sortAdV2Page(Page<AdV2> adV2s){
        if(adV2s == null || adV2s.getContent().size() == 0){
            return adV2s;
        }
        List<AdV2> onlineAds = setAdTimeAvailable(adV2s.getContent());
        List<AdV2> sortList = sortList(onlineAds);
        return new PageImpl<AdV2>(sortList,null,adV2s.getTotalElements());
    }

    /**
     * 设置广告剩余时间
     */
    private List<AdV2> setAdTimeAvailable(List<AdV2> adV2List){
        List<AdV2> onlineAds = new ArrayList<>();
        for(AdV2 ad : adV2List){
            int availableTime = DateUtils.getDays(ad.getOverdueTime(),new Date());
            ad.setDays(availableTime);
            onlineAds.add(ad);
        }
        return onlineAds;
    }

    /**
     * 自定义排序：默认在前，部分设备在后，再按广告到期时间正序，最后按修改时间倒序
     */
    private List<AdV2> sortList(List<AdV2> adV2List){
        adV2List.sort((AdV2 o1,AdV2 o2) -> {
            int o1DeliverMethod = o1.getDeliverMethod();
            int o2DeliverMethod = o2.getDeliverMethod();

            // 默认投放方式在前，指定设备在后
            if(o1DeliverMethod == -1 && o2DeliverMethod != -1){
                return -1;
            }else if(o1DeliverMethod != -1 && o2DeliverMethod == -1){
                return 1;
            }else {
                int o1Day = o1.getDays();
                int o2Day = o2.getDays();

                // 广告剩余时间正序
                if(o1Day > o2Day){
                    return 1;
                }else if(o1Day < o2Day){
                    return -1;
                }else {
                    long olModifiedTime = o1.getModifiedTime().getTime();
                    long o2ModifiedTime = o2.getModifiedTime().getTime();
                    // 广告修改时间倒序
                    if(olModifiedTime > o2ModifiedTime){
                        return -1;
                    }
                    if(olModifiedTime < o2ModifiedTime){
                        return 1;
                    }
                    return 0;
                }
            }
        });
        return adV2List;
    }

    @ApiOperation(value = "查询线上LBS广告")
    @RequestMapping(value = "/LBSOnline",method = RequestMethod.POST)
    public HttpResult findLBSsOnline(
            @RequestParam Integer shopId  ) {
        log.info("查询商店线上定位广告，shopId:"+shopId);
        if (StringUtils.isEmpty(shopId)) {
            return HttpResult.createFAIL("请选择一个商店！");
        }
        List<Integer> adIds = adOnlineV2Service.findAdIdsByShopIdAndTypeId(shopId, AdCodeConstants.AdV2Type.TYPE_CODE_LBS);
        if (!CollectionUtils.isEmpty(adIds)) {
            List<AdV2> ads = adV2Service.findAdsByAdIds(adIds);
            //设置剩余有效天数
            List<AdV2> onlineAds = setAdTimeAvailable(ads);
            //排序
            List<AdV2> sortList = sortList(onlineAds);
            return HttpResult.createSuccess("查询成功！", sortList);
        }else {
            return HttpResult.createSuccess("查询成功！无定位广告上线！");
        }

    }

}
