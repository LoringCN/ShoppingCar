package cn.huace.controller.admin.shop;


import cn.huace.ad.entity.AdOnlineV2;
import cn.huace.ad.entity.AdV2;
import cn.huace.ad.service.AdOnlineV2Service;
import cn.huace.ad.service.AdV2Service;
import cn.huace.ad.util.AdCodeConstants;
import cn.huace.common.bean.HttpResult;
import cn.huace.common.bean.TreeBean;
import cn.huace.common.utils.redis.RedisService;
import cn.huace.controller.admin.base.AdminBasicController;
import cn.huace.goods.entity.Goods;
import cn.huace.goods.entity.GoodsCategory;
import cn.huace.goods.service.GoodsCategoryService;
import cn.huace.goods.service.GoodsService;
import cn.huace.shop.shop.entity.Shop;
import cn.huace.shop.shop.service.ShopService;
import cn.huace.sys.bean.ShiroUser;
import cn.huace.sys.entity.RegionArea;
import cn.huace.sys.entity.RegionCity;
import cn.huace.sys.entity.RegionProvince;
import cn.huace.sys.entity.SystemUser;
import cn.huace.sys.service.RegionService;
import cn.huace.sys.service.SystemUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Api(value = "/admin/shop", description = "门店管理")
@RequestMapping(value = "/admin/shop")
public class ShopController extends AdminBasicController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsCategoryService categoryService;

    @Autowired
    private AdV2Service adV2Service;

    @Autowired
    private AdOnlineV2Service adOnlineV2Service;

    @Autowired
    private SystemUserService systemUserService;

    @Autowired
    private RedisService redisService;

    private static final String COMMA_CN = "，";
    private static final String COMMA_EN = ",";

    @RequestMapping(method = RequestMethod.GET)
    public HttpResult list(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "100") Integer rows, String name, ServletRequest request) {
        Map<String, Object> searchParams = new HashedMap();
        if (!StringUtils.isBlank(name)) {
            searchParams.put("LIKE_name", name);
        }
        // *** created by Loring  start ***
        ShiroUser user = getCurrentUser();
        SystemUser systemUser = systemUserService.findOne(user.getId());
        redisService.setObjectCacheValue("cn.huace.sys.systemUser:" + user.getAccount(), systemUser, 3);
        String shopIds = systemUser.getShopIds();
        if (systemUser.getType() != 0) {
            if (StringUtils.isBlank(shopIds) && systemUser.getType() == 1) {
                return HttpResult.createFAIL("数据异常：超市普通用户的shopIds 不能为空！");
            }
            if (StringUtils.isBlank(shopIds) && systemUser.getType() == 2) {
                return HttpResult.createFAIL("数据异常：超市管理员的shopIds 不能为空！");
            }
            String[] shops = shopIds.split(",");
            if (shops.length == 1) {
                searchParams.put("EQ_id", Integer.parseInt(shopIds.trim()));
            } else {
                searchParams.put("INI_id", StringToInt(shops));
            }
        }
        // *** created by Loring   end ***

        Page<Shop> pageResult = shopService.findAll(searchParams, page, rows);

        return HttpResult.createSuccess(pageResult);
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public HttpResult listAll() {
        List<Shop> shops = shopService.findAll();
        return HttpResult.createSuccess(shops);
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public HttpResult save(@RequestParam(value = "image", required = false) MultipartFile image, Shop entity, String beginTime, String endTime) {
        if (shopService.findDulplicateName(entity.getName(), entity.getId())) {
            return HttpResult.createFAIL("【" + entity.getName() + "】名称已存在！");
        }
        //处理中文逗号
        entity.setAlarmFloor(entity.getAlarmFloor().replaceAll(COMMA_CN, COMMA_EN));
        entity.setShopFloor(entity.getShopFloor().replaceAll(COMMA_CN, COMMA_EN));
        if (entity.getAreaId() != null) {
            RegionArea area = regionService.findArea(entity.getAreaId());
            RegionCity city = regionService.findCity(area.getCityId());
            RegionProvince province = regionService.findProvince(city.getProvinceId());
            entity.setArea(area.getName());
            entity.setProvince(province.getName());
            entity.setProvinceId(province.getProvinceId());
            entity.setCity(city.getName());
            entity.setCityId(city.getCityId());
        }
        if (beginTime != null) {
            entity.setBusinessTime(beginTime + "_" + endTime);
        }
        if (entity.isNew()) {
            shopService.save(entity);
        } else {
            Shop old = shopService.findOne(entity.getId());
            old.setArea(entity.getArea());
            old.setAreaId(entity.getAreaId());
            old.setBusinessTime(entity.getBusinessTime());
            old.setProvinceId(entity.getProvinceId());
            old.setProvince(entity.getProvince());
            old.setCityId(entity.getCityId());
            old.setCity(entity.getCity());
            old.setOpenDate(entity.getOpenDate());
            old.setName(entity.getName());
            old.setAddress(entity.getAddress());
            old.setPhone(entity.getPhone());
            old.setUseFlag(entity.getUseFlag());
            old.setPerson(entity.getPerson());
            old.setRemark(entity.getRemark());
            old.setShopPhone(entity.getShopPhone());
            old.setAlarmFloor(entity.getAlarmFloor());
            old.setShopFloor(entity.getShopFloor());
            old.setSsid(entity.getSsid());
//            old.setPwd(entity.getPwd());
            old.setFmapId(entity.getFmapId());
            shopService.save(old);
        }
        return HttpResult.createSuccess("保存成功！");
    }

    @RequestMapping(value = "del", method = RequestMethod.POST)
    public HttpResult del(Integer id) {
        shopService.delete(id);
        return HttpResult.createSuccess("删除成功！");
    }

    @ApiOperation(value = "获取门店", notes = "获取门店")
    @RequestMapping(value = "find", method = RequestMethod.GET)
    public HttpResult find(Integer id) {
        Shop entity = shopService.findOne(id);
        return HttpResult.createSuccess(entity);
    }

    @ApiOperation(value = "获取门店树")
    @RequestMapping(value = "findShopTree", method = RequestMethod.GET)
    public HttpResult findShopTree(Integer id) {

        List<Shop> list = new ArrayList<Shop>();
        ShiroUser user = getCurrentUser();
        SystemUser systemUser = systemUserService.findOne(user.getId());
        redisService.setObjectCacheValue("cn.huace.sys.systemUser:" + user.getAccount(), systemUser, 3);
        String shopIds = systemUser.getShopIds();
        if (systemUser.getType() != 0) {
            if (StringUtils.isBlank(shopIds) && systemUser.getType() == 1) {
                return HttpResult.createFAIL("数据异常：超市普通用户的shopIds 不能为空！");
            }
            if (StringUtils.isBlank(shopIds) && systemUser.getType() == 2) {
                return HttpResult.createFAIL("数据异常：超市管理员的shopIds 不能为空！");
            }
            String[] shops = shopIds.split(",");
            List<Integer> ids = new ArrayList<Integer>();
            for (String shopId : shops) {
                ids.add(Integer.parseInt(shopId.trim()));
            }
            list = shopService.findAll(ids);
        } else {
            list = shopService.findAll();
        }
        List<TreeBean> tree = new ArrayList<TreeBean>();
        for (Shop role : list) {
            TreeBean treeBean = new TreeBean();
            treeBean.setId(role.getId());
            treeBean.setText(role.getName());
            tree.add(treeBean);
        }
        return HttpResult.createSuccess(tree);
    }

    /*
        sourceShop -- 提供数据的商店 sourceShop -- 数据迁往的商店
        将一个商店的数据迁往另一个商店
     */
    @RequestMapping("/transfer/{sId}/{tId}")
    public HttpResult dataTransfer(
            @PathVariable("sId") Integer sourceShopId,
            @PathVariable("tId") Integer targetShopId,
            @RequestParam(required = false, defaultValue = "true") Boolean adSwitch) {
        Shop sourceShop = shopService.findOne(sourceShopId);
        Shop targetShop = shopService.findOne(targetShopId);
        if (sourceShop == null || targetShop == null) {
            return HttpResult.createFAIL("商店Id不正确！");
        }
        log.info("***** 开始迁移数据：【{} to {}】", sourceShop.getName(), targetShop.getName());

        /*  开始商品分类迁移 */
        log.info("***** 【商品分类】迁移开始...");
        Map<String, Object> catMap = new HashMap<>();
        catMap.put("EQ_shop.id", sourceShopId);
        List<GoodsCategory> sourceCategories = categoryService.findAll(catMap);
        List<GoodsCategory> targetCategories = new ArrayList<>();
        for (GoodsCategory category : sourceCategories) {
            if (category != null) {
                GoodsCategory goodsCategory = new GoodsCategory();
                goodsCategory.setCatName(category.getCatName());
                goodsCategory.setShop(targetShop);
                goodsCategory.setSpecialMark(category.getSpecialMark());
                goodsCategory.setFlag(category.getFlag());

                targetCategories.add(goodsCategory);
            }
        }
        // 删除目标商店已存在分类
        categoryService.deleteAllByShopId(targetShop.getId());
        int categoryNum = categoryService.batchInsert(targetCategories);
        log.info("***** 【商品分类】迁移结束！num = {}", categoryNum);

        /* 构建sourceCategories和targetCategories映射 */
        Map<Integer, GoodsCategory> sourceIdAndTargetCategoryMap = new HashMap<>();
        for (GoodsCategory sourceCategory : sourceCategories) {
            for (GoodsCategory targetCategory : targetCategories) {
                if (sourceCategory.getCatName().equals(targetCategory.getCatName())) {
                    sourceIdAndTargetCategoryMap.put(sourceCategory.getId(), targetCategory);
                }
            }
        }

        /* 商品数据迁移 */
        log.info("******** 【商品数据】迁移开始...");
        Map<String, Object> searchMap = new HashMap<>();
        searchMap.put("EQ_shop.id", sourceShopId);
        List<Goods> sourceGoodsList = goodsService.findAll(searchMap);
        List<Goods> targetGoodsList = new ArrayList<>();
        for (Goods sourceGoods : sourceGoodsList) {
            Goods targetGoods = wrapperSourceGoods(sourceGoods);
            targetGoods.setShop(targetShop);
            // 将目标商品分类关联目标商品
            targetGoods.setCategory(sourceIdAndTargetCategoryMap.get(sourceGoods.getCategory().getId()));

            targetGoodsList.add(targetGoods);
        }
        List<Goods> insertTargetResult = goodsService.batchInsertGoods(targetGoodsList);
        log.info("******** 【商品数据】迁移结束！num = {}", insertTargetResult == null ? 0 : insertTargetResult.size());

        if (adSwitch) {
            /* 构建sourceGoods和targetGoods映射 */
            Map<String, String> sourceGoodsAndTargetGoodsMap = new HashMap<>();
            for (Goods sourceGoods : sourceGoodsList) {
                for (Goods targetGoods : targetGoodsList) {
                    if (sourceGoods.getBarcode().equals(targetGoods.getBarcode())
                            && sourceGoods.getTitle().equals(targetGoods.getTitle())) {
                        sourceGoodsAndTargetGoodsMap.put(String.valueOf(sourceGoods.getId()), String.valueOf(targetGoods.getId()));
                    }
                }
            }
            /* 广告数据迁移 */
            log.info("******* 【广告数据】迁移开始...");
            Map<String, Object> adSearchMap = new HashMap<>();
            adSearchMap.put("EQ_shop.id", sourceShopId);
            List<AdV2> sourceAdV2List = adV2Service.findAll(adSearchMap);
            List<AdV2> targetAdV2List = new ArrayList<>();
            for (AdV2 sourceAdV2 : sourceAdV2List) {
                AdV2 targetAdV2 = wrapperSourceAdV2(sourceAdV2);
                targetAdV2.setShop(targetShop);
                String sourceExtra = sourceAdV2.getExtra();
                if (!StringUtils.isEmpty(sourceExtra)) {
                    // 处理关联关系
                    if (sourceAdV2.getRelation().getCode() == AdCodeConstants.AdV2Relation.GOODS_RELATION) {
                        targetAdV2.setExtra(sourceGoodsAndTargetGoodsMap.get(sourceExtra));
                    }
                    if (sourceAdV2.getRelation().getCode() == AdCodeConstants.AdV2Relation.GOODS_AND_SHELF) {
                        StringBuilder extraBuilder = new StringBuilder();
                        String delimiter = ",";
                        String[] sourceGoodsIdAndSid = sourceExtra.split(delimiter);
                        if (sourceGoodsIdAndSid.length >= 2) {
                            extraBuilder.append(sourceGoodsAndTargetGoodsMap.get(sourceGoodsIdAndSid[0]));
                            for (int i = 1; i < sourceGoodsIdAndSid.length - 1; i++) {
                                extraBuilder.append(delimiter).append(sourceGoodsIdAndSid[i]);
                            }
                            extraBuilder.append(delimiter).append(sourceGoodsIdAndSid[sourceGoodsIdAndSid.length - 1]);
                        } else {
                            extraBuilder.append(sourceExtra);
                        }
                        targetAdV2.setExtra(extraBuilder.toString());
                    }
                }

                targetAdV2List.add(targetAdV2);
            }
            int adV2Result = adV2Service.batchInsert(targetAdV2List);
            log.info("******* 【广告数据】迁移结束！num = {}", adV2Result);

            /* 构建sourceAdV2和targetAdV2映射 */
            Map<Integer, Integer> sourceAndTargetAdV2IdMap = new HashMap<>();
            for (AdV2 sourceAdV2 : sourceAdV2List) {
                for (AdV2 targetAdV2 : targetAdV2List) {
                    if (sourceAdV2.getName().equals(targetAdV2.getName()) && sourceAdV2.getMd5().equals(targetAdV2.getMd5())) {
                        sourceAndTargetAdV2IdMap.put(sourceAdV2.getId(), targetAdV2.getId());
                    }
                }
            }

            /* 线上广告迁移 */
            log.info("******* 【线上广告数据】迁移开始...");
            Map<String, Object> adOnlineV2SearchMap = new HashMap<>();
            adOnlineV2SearchMap.put("EQ_shopId", sourceShopId);
            List<AdOnlineV2> sourceAdOnlineV2List = adOnlineV2Service.findAll(adOnlineV2SearchMap);
            List<AdOnlineV2> targetAdOnlineV2List = new ArrayList<>();
            for (AdOnlineV2 sourceAdOnlineV2 : sourceAdOnlineV2List) {

                AdOnlineV2 targetAdOnlineV2 = new AdOnlineV2();
                targetAdOnlineV2.setTypeId(sourceAdOnlineV2.getTypeId());
                targetAdOnlineV2.setRank(sourceAdOnlineV2.getRank());
                targetAdOnlineV2.setDevId(sourceAdOnlineV2.getDevId());
                targetAdOnlineV2.setShopId(targetShopId);
                targetAdOnlineV2.setAdId(sourceAndTargetAdV2IdMap.get(sourceAdOnlineV2.getAdId()));

                targetAdOnlineV2List.add(targetAdOnlineV2);
            }

            int adOnlineResult = adOnlineV2Service.batchInsert(targetAdOnlineV2List);
            log.info("******* 【线上广告数据】迁移结束！num = {}", adOnlineResult);
        }
        String respMsg = "从【" + sourceShop.getName() + " 】到【 " + targetShop.getName() + "】迁移数据成功！";
        return HttpResult.createSuccess(respMsg);
    }

    private AdV2 wrapperSourceAdV2(AdV2 sourceAdV2) {
        AdV2 targetAdV2 = new AdV2();
        targetAdV2.setName(sourceAdV2.getName());
        targetAdV2.setAudit(sourceAdV2.getAudit());
        targetAdV2.setDeliverMethod(sourceAdV2.getDeliverMethod());
        targetAdV2.setDescription(sourceAdV2.getDescription());
        targetAdV2.setFlag(sourceAdV2.getFlag());
        targetAdV2.setMd5(sourceAdV2.getMd5());
        targetAdV2.setUrl(sourceAdV2.getUrl());
        targetAdV2.setPrice(sourceAdV2.getPrice());
        targetAdV2.setStatus(sourceAdV2.getStatus());
        targetAdV2.setVoted(sourceAdV2.getVoted());
        targetAdV2.setGroup(sourceAdV2.getGroup());
        targetAdV2.setRelation(sourceAdV2.getRelation());
        targetAdV2.setType(sourceAdV2.getType());
        targetAdV2.setActiveTime(sourceAdV2.getActiveTime());
        targetAdV2.setOverdueTime(sourceAdV2.getOverdueTime());
        return targetAdV2;
    }

    private Goods wrapperSourceGoods(Goods sourceGoods) {
        Goods targetGoods = new Goods();
        targetGoods.setTitle(sourceGoods.getTitle());
        targetGoods.setBarcode(sourceGoods.getBarcode());
        targetGoods.setNormalPrice(sourceGoods.getNormalPrice());
        targetGoods.setPromotionPrice(sourceGoods.getPromotionPrice());
        targetGoods.setCoverImgUrl(sourceGoods.getCoverImgUrl());
        targetGoods.setDetailImgUrl(sourceGoods.getDetailImgUrl());
        targetGoods.setSid(sourceGoods.getSid());
        targetGoods.setLocation(sourceGoods.getLocation());
        targetGoods.setDescr(sourceGoods.getDescr());
        targetGoods.setType(sourceGoods.getType());
        targetGoods.setFlag(sourceGoods.getFlag());
        targetGoods.setSortNo(sourceGoods.getSortNo());
        targetGoods.setRemark(sourceGoods.getRemark());
        targetGoods.setNewRecommend(sourceGoods.getNewRecommend());
        targetGoods.setSeoTag(sourceGoods.getSeoTag());
        return targetGoods;
    }

    /*add start and stop function by Loring on 2018-05-18 15:30*/
    @ApiOperation(value = "门店启用", notes = "门店启用")
    @RequestMapping(value = "start", method = RequestMethod.POST)
    public HttpResult start(Integer id) {

        return HttpResult.createSuccess("启用成功！");
    }

    @ApiOperation(value = "门店停用", notes = "门店停用")
    @RequestMapping(value = "stop", method = RequestMethod.POST)
    public HttpResult stop(Integer id) {

        return HttpResult.createSuccess("停用成功！");
    }

    public List<Integer> StringToInt(String[] arrs) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < arrs.length; i++) {
            list.add(Integer.parseInt(arrs[i]));
        }
        return list;
    }

}
