package cn.huace.controller.statis;

import cn.huace.common.bean.HttpResult;
import cn.huace.common.utils.redis.RedisService;
import cn.huace.config.Config;
import cn.huace.controller.base.AdminBasicController;
import cn.huace.goods.entity.Goods;
import cn.huace.goods.service.GoodsService;
import cn.huace.shop.shop.service.ShopService;
import cn.huace.statis.core.StatisEntrance;
import cn.huace.statis.search.entity.StatisSearch;
import cn.huace.statis.search.service.StatisSearchService;
import cn.huace.sys.bean.ShiroUser;
import cn.huace.sys.entity.SystemUser;
import cn.huace.sys.service.SystemUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

/**
 * Created by yld on 2017/12/25.
 */
@Slf4j
@RestController
@Api(value = "/admin/search", description = "商品搜索统计")
@RequestMapping(value = "/admin/search")
public class SearchController extends AdminBasicController {
    @Autowired
    private StatisSearchService statisSearchService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private StatisEntrance statisEntrance;
    @Autowired
    private Config config;
    @Autowired
    private RedisService redisService;
    @Autowired
    private SystemUserService systemUserService;

    /*
        statisDate格式：yyyy-MM-dd
     */
    @RequestMapping(value = "/manual/statis", method = {RequestMethod.GET, RequestMethod.POST})
    public HttpResult manualTriggerStatis(String statisDate) {
        if (StringUtils.isEmpty(statisDate)) {
            return HttpResult.createFAIL("要统计的日期不能为空！");
        }
        String statLogPath = config.getStatFilePath();
        try {
            statisEntrance.doStatis(statisDate, statLogPath);
        } catch (IOException e) {
            log.error("****** 手动触发统计失败！");
            return HttpResult.createFAIL("手动触发统计失败！");
        }
        return HttpResult.createSuccess("手动触发统计成功！");
    }

    @ApiOperation(value = "/list", notes = "搜索统计列表")
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public HttpResult list(
            Integer shopId, String keyword, Date startTime, Date endTime,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer rows) {
        log.info("******** 调用搜索统计接口，参数：shopId = {},keyword = {},startTime = {},endTime = {}", shopId, keyword, startTime, endTime);
        Map<String, Object> searchMap = new HashMap<String, Object>();
        if (!StringUtils.isEmpty(shopId)) {
            searchMap.put("EQ_shopId", shopId);
        } else {
            ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
            SystemUser systemUser = systemUserService.findOne(user.getId());
            if (systemUser.getType() != 0) {
                String shopIds = systemUser.getShopIds();
                if (shopIds.split(",").length == 1) {
                    searchMap.put("EQ_shopId", Integer.parseInt(shopIds.trim()));
                } else {
                    searchMap.put("EQ_shopId", Integer.parseInt(shopIds.split(",")[0]));
                }
            }
        }
        if (!StringUtils.isEmpty(keyword)) {
            searchMap.put("LIKE_searchKey", keyword);
        }
        if (startTime != null) {
            searchMap.put("GTE_searchTime", startTime);
        }
        if (endTime != null) {
            searchMap.put("LTE_searchTime", endTime);
        }
        Page<StatisSearch> searchPage = statisSearchService.list(searchMap, page, rows);
        if (searchPage.getSize() != 0) {
            for (StatisSearch statisSearch : searchPage) {
                statisSearch.setShop(shopService.findOne(statisSearch.getShopId()));
                String relGoods = statisSearch.getRelGoods();
                if (!StringUtils.isEmpty(relGoods)) {
                    String[] goodsIdArr = relGoods.split(",");
                    List<Integer> ids = new ArrayList<>();
                    for (String goodsId : goodsIdArr) {
                        if (!StringUtils.isEmpty(goodsId)) {
                            ids.add(Integer.parseInt(goodsId));
                        }
                    }
                    List<Goods> goods = goodsService.findAll(ids);
                    if (goods.size() != 0) {
                        Map<String, Integer> catMap = categoryCountMap(goods);
                        Collection<Integer> countCollection = catMap.values();
                        List<Integer> countList = Arrays.asList(countCollection.toArray(new Integer[countCollection.size()]));
                        Collections.sort(countList);
                        Integer maxCount = countList.get(countList.size() - 1);
                        StringBuilder builder = new StringBuilder();
                        for (Map.Entry<String, Integer> entry : catMap.entrySet()) {
                            Integer value = entry.getValue();
                            if (value.equals(maxCount)) {
                                builder.append(entry.getKey()).append(",");
                            }
                        }
                        statisSearch.setCategory(builder.toString().substring(0, builder.lastIndexOf(",")));
                    }
                }
            }
        }
        return HttpResult.createSuccess("查询成功！", searchPage);
    }

    private Map<String, Integer> categoryCountMap(List<Goods> goodsList) {
        Map<String, Integer> catMap = new HashMap<>();
        for (Goods goods : goodsList) {
            Integer count = catMap.get(goods.getCategory().getCatName());
            if (count != null) {
                count++;
                catMap.put(goods.getCategory().getCatName(), count);
            } else {
                catMap.put(goods.getCategory().getCatName(), 1);
            }
        }
        return catMap;
    }
}
