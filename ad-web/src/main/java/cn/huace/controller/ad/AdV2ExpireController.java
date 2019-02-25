package cn.huace.controller.ad;

import cn.huace.ad.entity.AdV2;
import cn.huace.ad.service.AdV2Service;
import cn.huace.ad.util.AdCodeConstants;
import cn.huace.common.bean.HttpResult;
import cn.huace.common.utils.DateUtils;
import cn.huace.controller.base.AdminBasicController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 *
 * Date:2018/1/24
 */
@Slf4j
@RestController
@Api(value = "/admin/v2/ad/expire",description = "V2版过期广告管理接口")
@RequestMapping(value = "/admin/v2/ad/expire")
public class AdV2ExpireController extends AdminBasicController {


    @Autowired
    private AdV2Service adV2Service;

    @ApiOperation(value = "查询所有即将过期或者已过期的广告")
    @RequestMapping(value = "/list",method = RequestMethod.POST)
    public HttpResult listExpireAds(
            @RequestParam Integer shopId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer rows
    ){
        log.info("****** 查询所有即将过期或者已过期的广告：listExpireAds(),参数：shopId = {},page = {},rows = {}",shopId,page,rows);
        if(StringUtils.isEmpty(shopId)){
            return HttpResult.createFAIL("请选择一个商店");
        }
        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_shop.id",shopId);
        //过期时间要小于当前时间加上7天
        searchMap.put("LTE_overdueTime", DateUtils.getNextDayTimes(7));
        //上架广告
        searchMap.put("EQ_status", AdCodeConstants.AdStatus.NORMAL);

        Page<AdV2> adList = adV2Service.listExpireAds(searchMap,page,rows);

        return HttpResult.createSuccess("查询成功",sortAdV2Page(adList));
    }

    @ApiOperation(value = "搜索即将过期或过期广告")
    @RequestMapping(value = "/search",method = RequestMethod.POST)
    public HttpResult searchExpireAds(
            @RequestParam Integer shopId,
            @RequestParam(name = "adName") String adName,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer rows
    ){
        log.info("****** 搜索即将过期或过期广告：searchExpireAds(),参数：shopId = {},adName = {},page = {},rows = {}",shopId,adName,page,rows);
        if(StringUtils.isEmpty(shopId)){
            return HttpResult.createFAIL("请选择一个商店！");
        }
        if(StringUtils.isEmpty(adName)){
            return HttpResult.createFAIL("请输入广告名！");
        }

        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_shop.id",shopId);
        searchMap.put("LIKE_name",adName);
        //days要小于7
        searchMap.put("LTE_overdueTime", DateUtils.getNextDayTimes(7));
        //上架广告
        searchMap.put("EQ_status", AdCodeConstants.AdStatus.NORMAL);

        Page<AdV2> adList = adV2Service.listExpireAds(searchMap,page,rows);
        return HttpResult.createSuccess("查询成功！",sortAdV2Page(adList));
    }

    @ApiOperation(value = "广告续期")
    @RequestMapping(value = "/overdue_time/extend",method = RequestMethod.POST)
    public HttpResult extendOverdueTime(
            @RequestParam Integer adId,
            @RequestParam(name = "activeTime") Date startTime,
            @RequestParam(name = "overdueTime") Date endTime
    ){
        log.info("***** 广告续期：extendOverdueTime(),参数：adId = {},activeTime = {},overdueTime = {}",adId,startTime,endTime);
        if(StringUtils.isEmpty(adId)){
            return HttpResult.createFAIL("参数【adId】不能为空！");
        }
        AdV2 adV2 = adV2Service.findOne(adId);
        if(adV2 == null){
            return HttpResult.createFAIL("无此广告！");
        }
        if(endTime == null ){
            return HttpResult.createFAIL("请输入续期时间！");
        }
        if(endTime.before(adV2.getOverdueTime())){
            return HttpResult.createFAIL("续期时间必须大于原来到期时间！");
        }
        if (startTime != null){
            adV2.setActiveTime(startTime);
        }
        adV2.setOverdueTime(endTime);
        adV2Service.save(adV2);
        return HttpResult.createSuccess("续期成功！！！");
    }


    /**
     * 对Page的内容按照字段days排序
     * @param adV2s
     * @return
     */
    private Page<AdV2> sortAdV2Page(Page<AdV2> adV2s){
        if(adV2s == null || adV2s.getContent().size() == 0){
            return new PageImpl<AdV2>(new ArrayList<>());
        }
        //1.设置广告剩余时间
        List<AdV2> list = setExpireDays(adV2s.getContent());
        //2.自定义排序，按剩余时间正序排列，即剩余时间越小排序越靠前
        List<AdV2> sortAdList = sortList(list);

        return new PageImpl<AdV2>(sortAdList,null,adV2s.getTotalElements());
    }


    /**
     *设置广告剩余时间
     * @return
     */
    private List<AdV2> setExpireDays(List<AdV2> adV2s){
        List<AdV2> expireAds = new ArrayList<>();
        for (AdV2 adV2 : adV2s){
//            DateUtils.getDays得到相差天数
            adV2.setDays(DateUtils.getDays(adV2.getOverdueTime(),new Date()));
            expireAds.add(adV2);
        }
        return expireAds;
    }

    /**
     * 即将过期的广告在前面，已经过期的广告在后面，而在即将过期的广告中，越临近过期时间的在前面
     * 在已过期的广告中过期时间越短，越靠前
     * @return
     */
    private List<AdV2> sortList(List<AdV2> adV2List){
        //自定义排序，内部广告展示在前面，外部广告按modified_time倒序
        adV2List.sort((AdV2 o1, AdV2 o2) -> {
                int o1Days = o1.getDays(),o2Days = o2.getDays();

                if(o1Days >= 0 && o2Days < 0){
                    return -1;
                }else if (o1Days < 0 && o2Days >= 0){
                    return 1;
                }else if(o1Days < 0 && o2Days < 0){
                    if(o1Days < o2Days){
                        return 1;
                    }
                    if(o1Days > o2Days){
                        return -1;
                    }
                    return 0;
                } else {
                    if(o1Days < o2Days){
                        return -1;
                    }
                    if(o1Days > o2Days){
                        return 1;
                    }
                    return 0;
                }
        });
        return adV2List;
    }

}
