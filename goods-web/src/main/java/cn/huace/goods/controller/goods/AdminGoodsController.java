package cn.huace.goods.controller.goods;

import cn.huace.common.bean.HttpResult;
import cn.huace.common.config.SystemConfig;
import cn.huace.common.utils.Contants;
import cn.huace.common.utils.FileUtil;
import cn.huace.common.utils.PostObjectToOss;
import cn.huace.common.utils.excel.ExcelUtil;
import cn.huace.common.utils.redis.RedisService;
import cn.huace.goods.entity.Goods;
import cn.huace.goods.entity.GoodsCategory;
import cn.huace.goods.entity.GoodsRenewal;
import cn.huace.goods.enums.GoodsType;
import cn.huace.goods.service.*;
import cn.huace.shop.shop.entity.Shop;
import cn.huace.shop.shop.service.ShopService;
import cn.huace.sys.bean.ShiroUser;
import cn.huace.sys.entity.SystemUser;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.Row;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yld on 2017/5/9.
 */
@RestController
@Api(value = "/admin/product", description = "商品管理")
@RequestMapping("/admin/product")
public class AdminGoodsController {

    private static Logger logger = LoggerFactory.getLogger(AdminGoodsController.class);

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private SearchGoodsService searchGoodsService;

    @Autowired
    private GoodsCategoryService categoryService;

    @Autowired
    private ThemeService themeService;

    @Autowired
    private ThemeTypeService themeTypeService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private GoodsRenewalService goodsRenewalService;

    @Autowired
    private GoodsCategoryService goodsCategoryService;

    /**
     * 批量导入商品默认显示位置
     */
    private static final Integer DEFAULT_SORTNO = 5;

    //缓存失败商品key
    private static final String FAIL_GOODS_REDIS_KEY = "goods:fail:";
    //存放失败商品的redis库,存放在第二个库
    private static final Integer FAIL_GOODS_REDIS_DBINDEX = 1;

    @ApiOperation(value = "所有商品、促销商品、预促销商品和新品举荐商品")
    @RequestMapping(value = "/list", method = {RequestMethod.POST})
    public HttpResult findAllGoodsForAdmin(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "shopId", required = false) Integer shopId,
            @RequestParam(name = "categoryId", required = false) Integer categoryId,
            @RequestParam(name = "newRecommend", required = false) Integer newRecommend,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "barcode", required = false) String barcode,
            @RequestParam(defaultValue = "1", name = "page") Integer pageNo,
            @RequestParam(defaultValue = "20", name = "rows") Integer pageSize) {

        logger.info("**** 开始调用方法：findAllGoods(),参数={shopId:" + shopId +"barcode:"+ barcode + " pageNo:" + pageNo + " pageSize:" + pageSize + " type:" + type + " newRecommend=" + newRecommend + "}");
        Map<String, Object> searchMap = new HashMap<String, Object>();

        if (StringUtils.isEmpty(shopId)) {
            ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
            SystemUser systemUser = (SystemUser) redisService.getObjectCacheValue("cn.huace.sys.systemUser:" + user.getAccount(), 3);
            if (systemUser.getType() != 0) {
                String shopIds = systemUser.getShopIds();
                String[] shops = shopIds.split(",");
                if (shopIds.split(",").length == 1) {
                    searchMap.put("EQ_shop.id", Integer.parseInt(shopIds.trim()));
                } else {
                    searchMap.put("INI_shop.id", StringToInt(shops));
                }
            }
        } else {
            searchMap.put("EQ_shop.id", shopId);
        }

        if(!StringUtils.isEmpty(barcode)){
            searchMap.put("RLIKE_barcode", barcode);
        }

        if (!StringUtils.isEmpty(keyword)) {
            searchMap.put("LIKE_title", keyword);
        }
        if (!StringUtils.isEmpty(categoryId)) {
            searchMap.put("EQ_category.id", categoryId);
        }
        if (!StringUtils.isEmpty(newRecommend)) {
//            searchMap.put("NE_type", GoodsType.OTHER.getValue());
            searchMap.put("EQ_newRecommend", newRecommend);
        }
        searchMap.put("EQ_flag", "1");

        String promotionGoodsListType = GoodsType.PROMOTION.getValue();
        String prePromotionGoodsListType = "prepromotion";
        Date date = new Date();
        if (promotionGoodsListType.equalsIgnoreCase(type)) {
            //促销商品列表
//            searchMap.put("EQ_type", promotionGoodsListType);
            searchMap.put("LTE_promotionStartDate",date);
            searchMap.put("GTE_promotionEndDate",date);
        } else if (prePromotionGoodsListType.equalsIgnoreCase(type)) {
            //预促销商品列表
//            searchMap.put("EQ_type", GoodsType.NORMAL.getValue());
//            searchMap.put("ISNOTNULL_promotionPrice", "IS NOT NULL");
            searchMap.put("GTE_promotionStartDate",date);
        }

        Page<Goods> goods = goodsService.findAllGoodsForAdmin(searchMap, pageNo, pageSize);

        return HttpResult.createSuccess("查询成功！", goods);
    }

    @ApiOperation(value = "所有促销商品")
    @RequestMapping(value = "/promotion/list", method = RequestMethod.POST)
    public HttpResult findAllPromotionGoodsForAdmin(
            @RequestParam(name = "shopId", required = false) Integer shopId,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "1", name = "page") Integer pageNo,
            @RequestParam(defaultValue = "20", name = "rows") Integer pageSize
    ) {
        logger.info("***** 开始调用方法：findAllPromotionGoods(),参数={shopId=" + shopId + " pageNo=" + pageNo + " pageSize=" + pageSize + "}");

        Map<String, Object> searchMap = Maps.newHashMap();
        searchMap.put("EQ_shop.id", shopId);
        searchMap.put("EQ_type", GoodsType.PROMOTION.getValue());
        if (!StringUtils.isEmpty(keyword)) {
            searchMap.put("LIKE_title", keyword);
        }
        searchMap.put("EQ_flag", "1");

        Page<Goods> promotionGoods = goodsService.findAllGoodsForAdmin(searchMap, pageNo, pageSize);
//        if(promotionGoods.getTotalElements() == 0){
//            return HttpResult.createFAIL("没有促销商品！");
//        }
        return HttpResult.createSuccess("查询成功！", promotionGoods);
    }

    @ApiOperation(value = "商品详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public HttpResult getGoodsDetailForAdmin(@PathVariable(name = "id", required = true) Integer id) {
        logger.info("***** 开始调用方法:getGoodsDetail(),参数={id:" + id + "}");
        if (id == null) {
            return HttpResult.createFAIL("参数:商品id不能为空");
        }
        Goods goodsDetail = goodsService.findGoodsDetailForAdmin(id);
        return HttpResult.createSuccess("查询成功！", goodsDetail);
    }

    @ApiOperation(value = "保存商品")
    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    public HttpResult saveGoods(
            Goods goods,
            @RequestParam(name = "promotion_price", required = false) String promotionPrice,
            @RequestParam(name = "normal_price", required = false) String normalPrice,
            @RequestParam(name = "goodsSmallImg", required = false) MultipartFile goodsSmallImg,
            @RequestParam(name = "goodsBigImg", required = false) MultipartFile goodsBigImg
    ) {
        logger.info("***** 开始调用方法：saveGoods()");
        if (goods.getShop() == null) {
            return HttpResult.createFAIL("超市不能为空，请先选择超市！");
        }
//        String catId = goodsWrapper.getCatId();
        if (goods.getCategory() == null) {
            return HttpResult.createFAIL("商品分类不能为空！");
        }
        //上传商品图片
        String coverImgUrl = PostObjectToOss.postFile(goodsSmallImg, Contants.GOODS_IMG_TEMP_FOLDER);
        if (StringUtils.isEmpty(coverImgUrl)) {
            logger.info("****** 上传商品小图失败！");
            return HttpResult.createFAIL("上传商品小图失败，请重试！");
        }
        String detailImgUrl = PostObjectToOss.postFile(goodsBigImg, Contants.GOODS_IMG_TEMP_FOLDER);
        if (StringUtils.isEmpty(detailImgUrl)) {
            logger.info("****** 上传商品大图失败！");
            return HttpResult.createFAIL("上传商品大图失败，请重试！");
        }
        logger.info("**** 商品小图url：{},商品大图url：{}", coverImgUrl, detailImgUrl);
        //检查商品是否已存在，依据：超市ID+条形码(由于我方没有条形码数据，因此使用：超市ID+商品名替代)
//        boolean isExist = goodsService.isExistGoods(Integer.parseInt(shopId),goodsWrapper.getTitle());
//        if(isExist){
//           return HttpResult.createFAIL("此商品已经存在，请求重复添加！");
//        }
        goods.setCoverImgUrl(coverImgUrl);
        goods.setDetailImgUrl(detailImgUrl);
        if (!StringUtils.isEmpty(normalPrice)) {
            goods.setNormalPrice(new BigDecimal(normalPrice).movePointRight(2).intValue());
        }
        if (!StringUtils.isEmpty(promotionPrice)) {
            goods.setPromotionPrice(new BigDecimal(promotionPrice).movePointRight(2).intValue());
        }
        goods.setFlag("1");

        Goods saveResult = goodsService.save(goods);
        if (saveResult == null) {
            return HttpResult.createFAIL("添加商品失败！");
        }
        //添加商品数据至Solr
//        try {
//            searchGoodsService.save(saveResult);
//        }catch (Exception e){
//            logger.error("**** 添加商品文档至solr出错！errMsg: "+ e);
//        }

        return HttpResult.createSuccess("添加商品成功！");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public HttpResult updateGoods(
            Goods goods,
            @RequestParam(name = "promotion_price", required = false) String promotionPrice,
            @RequestParam(name = "normal_price", required = false) String normalPrice,
            @RequestParam(name = "goodsSmallImg", required = false) MultipartFile goodsSmallImg,
            @RequestParam(name = "goodsBigImg", required = false) MultipartFile goodsBigImg) {
        logger.info("***** 开始调用方法：updateGoods()");

        Integer goodsId = goods.getId();
        String coverImgUrl;
        String detailImgUrl;
        String descr = goods.getDescr();
        String location = goods.getLocation();
        String sid = goods.getSid();
        String barcode = goods.getBarcode();
        logger.info("****** goods参数:goodsId:" + goodsId + " descr:" + descr + " location:" + location + " sid:" + sid + " " + " barcode:" + barcode);
        if (StringUtils.isEmpty(barcode)) {
            return HttpResult.createFAIL("商品条码不能为空！");
        }
        if (goodsSmallImg != null && !goodsSmallImg.isEmpty()) {
            coverImgUrl = PostObjectToOss.postFile(goodsSmallImg, Contants.GOODS_IMG_TEMP_FOLDER);
            if (StringUtils.isEmpty(coverImgUrl)) {
                logger.info("****** 上传商品小图失败！");
                return HttpResult.createFAIL("上传商品小图失败，请重试！");
            }
        } else {
            if (goods.getCoverImgUrl() != null)
                coverImgUrl = goods.getCoverImgUrl();
            else
                return HttpResult.createFAIL("图片问题");
        }
        if (goodsBigImg != null && !goodsBigImg.isEmpty()) {
            detailImgUrl = PostObjectToOss.postFile(goodsBigImg, Contants.GOODS_IMG_TEMP_FOLDER);
            if (StringUtils.isEmpty(detailImgUrl)) {
                logger.info("****** 上传商品大图失败！");
                return HttpResult.createFAIL("上传商品大图失败，请重试！");
            }
        } else {
            if (goods.getDetailImgUrl() != null)
                detailImgUrl = goods.getDetailImgUrl();
            else
                return HttpResult.createFAIL("图片问题");
        }
        if (descr == null) {
            descr = "";
        }
        if (location == null) {
            location = "";
        }
        if (sid == null) {
            sid = "";
        }
        Integer i = goodsService.updateGoodsByBarcode(coverImgUrl, detailImgUrl, sid, location, descr, goodsId);
        if (i > 0)
            return HttpResult.createSuccess("更新成功");
        else
            return HttpResult.createFAIL("更新失败");
    }

//    以前的更新商品的接口，现在废弃了
//    @ApiOperation(value = "更新商品数据")
//    @RequestMapping(value = "/update", method = RequestMethod.POST)
//    public HttpResult updateGoods(
//            Goods goods,
//            @RequestParam(name = "promotion_price", required = false) String promotionPrice,
//            @RequestParam(name = "normal_price", required = false) String normalPrice,
//            @RequestParam(name = "goodsSmallImg", required = false) MultipartFile goodsSmallImg,
//            @RequestParam(name = "goodsBigImg", required = false) MultipartFile goodsBigImg
//    ) {
//        logger.info("***** 开始调用方法：updateGoods()");
//        if (goods.getShop() == null) {
//            return HttpResult.createFAIL("超市不能为空，请先选择超市！");
//        }
//        if (goods.getCategory() == null) {
//            return HttpResult.createFAIL("商品分类不能为空！");
//        }
//        Goods oldGoods = goodsService.findOne(goods.getId());
//        String oldGoodsType = oldGoods.getType();
//
//        oldGoods.setTitle(goods.getTitle());
//        oldGoods.setShop(goods.getShop());
//        oldGoods.setBarcode(goods.getBarcode());
//        oldGoods.setSortNo(goods.getSortNo());
//        oldGoods.setType(goods.getType());
//        if (!StringUtils.isEmpty(normalPrice)) {
//            oldGoods.setNormalPrice(new BigDecimal(normalPrice).movePointRight(2).intValue());
//        } else {
//            oldGoods.setNormalPrice(null);
//        }
//        if (!StringUtils.isEmpty(promotionPrice)) {
//            oldGoods.setPromotionPrice(new BigDecimal(promotionPrice).movePointRight(2).intValue());
//        } else {
//            oldGoods.setPromotionPrice(null);
//        }
//        oldGoods.setSid(goods.getSid());
//        oldGoods.setCategory(goods.getCategory());
//        oldGoods.setNewRecommend(goods.getNewRecommend());
//        if (goodsSmallImg != null && !goodsSmallImg.isEmpty()) {
//            String coverImgUrl = PostObjectToOss.postFile(goodsSmallImg, Contants.GOODS_IMG_TEMP_FOLDER);
//            if (StringUtils.isEmpty(coverImgUrl)) {
//                logger.info("****** 上传商品小图失败！");
//                return HttpResult.createFAIL("上传商品小图失败，请重试！");
//            } else {
//                oldGoods.setCoverImgUrl(coverImgUrl);
//            }
//        }
//        if (goodsBigImg != null && !goodsBigImg.isEmpty()) {
//            String detailImgUrl = PostObjectToOss.postFile(goodsBigImg, Contants.GOODS_IMG_TEMP_FOLDER);
//            if (StringUtils.isEmpty(detailImgUrl)) {
//                logger.info("****** 上传商品大图失败！");
//                return HttpResult.createFAIL("上传商品大图失败，请重试！");
//            } else {
//                oldGoods.setDetailImgUrl(detailImgUrl);
//            }
//        }
//        oldGoods.setDescr(goods.getDescr());
//        oldGoods.setLocation(goods.getLocation());
//        oldGoods.setSeoTag(goods.getSeoTag());
//        oldGoods.setRemark(goods.getRemark());
//
//        Goods updateResult = goodsService.save(oldGoods);
//        if (updateResult == null) {
//            return HttpResult.createFAIL("更新失败！");
//        }
//
//        //根据商品类型决定是否需要从特价主题信息中删除该商品
//        if (GoodsType.PROMOTION.getValue().equals(oldGoodsType) && !oldGoodsType.equals(updateResult.getType())) {
//            /*
//              1.修改前为特价商品，则该商品可能已关联在特价主题tag上
//              2.修改后该商品类型由特价变为normal，若该商品修改前已被关联到特价主题上
//                则需要将其从特价主题tag中移除
//             */
//            ThemeType themeType = themeTypeService.findOneThemeTypeByName("特价主题");
//            if (themeType != null) {
//                Boolean result = themeService.removePromotionThemeTagRelationGoods(oldGoods.getShop().getId(), themeType.getId(), updateResult.getId());
//                if (result) {
//                    logger.info("***** 成功解除《特价主题》tag与已修改商品关联！");
//                }
//            }
//        }
//        //更新Solr对应商品数据
////        try {
////            searchGoodsService.save(updateResult);
////        }catch (Exception e){
////            logger.error("***** 更新solr文档出错！errMsg: "+ e);
////        }
//
//        return HttpResult.createSuccess("更新成功！");
//    }

    @ApiOperation(value = "批量删除商品")
    @RequestMapping(value = "/del", method = {RequestMethod.GET})
    public HttpResult delGoodsBatch(Integer[] ids) {
        if (ids.length == 0) {
            return HttpResult.createFAIL("请先选择要删除的商品！");
        }
        List<Integer> idList = Arrays.asList(ids);
        logger.info("**** 批量删除商品ID = " + idList);

        List<Goods> oldGoodsList = goodsService.findAll(idList);
        boolean result = goodsService.batchDeleteGoods(oldGoodsList);
        if (!result) {
            return HttpResult.createFAIL("删除失败！");
        }
        //删除对应主题tag关联商品
        Set<Integer> shopIdSet = new HashSet<>();
        for (Goods goods : oldGoodsList) {
            Integer shopId = goods.getShop().getId();
            shopIdSet.add(shopId);
        }
        Integer[] shopIdArr = shopIdSet.toArray(new Integer[shopIdSet.size()]);
        //解除主题tag与删除商品的关联
        themeService.removeThemeTagRelationGoods(idList, shopIdArr);

        //删除Solr对应商品文档
//        try {
//            List<String> documentIds = new ArrayList<>();
//            for (Integer id:idList){
//                documentIds.add(String.valueOf(id));
//            }
//            searchGoodsService.deleteBatch(documentIds);
//            logger.info("**** 删除solr中商品Id: "+ documentIds);
//        }catch (Exception e){
//            logger.error("**** 删除solr文档出错！errMsg: "+ e);
//        }

        return HttpResult.createSuccess("删除成功！");
    }

    /**
     * 批量添加商品excel数据
     *
     * @param goodsExcelFile 导入的Excel文件
     * @param shopId         商店id
     * @param isCover        是否导入新的特价信息
     * @return
     */
    @ApiOperation(value = "批量添加商品excel数据")
    @RequestMapping(value = "/batch/add", method = RequestMethod.POST)
    public HttpResult uploadExcelAndAddGoodsBatch(
            @RequestParam(name = "excel") MultipartFile goodsExcelFile,
            Integer shopId,
            @RequestParam(defaultValue = "0") Integer isCover
    ) {
        logger.info("***　调用方法uploadExcelFile(),参数：shopId={},isCover ={}", shopId, isCover);
        long startTime = System.currentTimeMillis();
        if (isCover == 1) {
            if (goodsService.cleanPromotion(shopId) == 0) {
                logger.info("清除商品历史特价信息数量为0");
            }
        }

        //封装所有excel商品数据
        List<Goods> goodsList = new ArrayList<Goods>();
        //批量导入数据结果
        Map<String, Object> resultDataMap = new HashMap<String, Object>();
        try {
            String excelFilePath = FileUtil.saveFile(goodsExcelFile);
            ExcelUtil excelUtil = new ExcelUtil();
            List<Row> rowList = excelUtil.readExcel(excelFilePath);
            if (rowList.size() <= 1) {//只有标题栏或者什么都没有
                return HttpResult.createFAIL("excel文件没有数据！");
            }
            //删除上传的excel文件
            FileUtil.delFile(excelFilePath);

            //查询超市信息
            Shop shop = shopService.findOne(shopId);
            List<GoodsCategory> categories = categoryService.findAllCategoryAvailable();
            //将商品分类信息转化成Map
            Map<String, GoodsCategory> categoryMap = convertGoodsCategoryList2Map(categories);
            for (int i = 1; i < rowList.size(); i++) {
                Row row = rowList.get(i);
                Goods goods = excelRowToGoods(row, excelUtil, shop, categoryMap);
                if (goods == null) {
                    continue;
                }
                goodsList.add(goods);
            }
            // 获取所有上传商品信息条形码
            List<String> barcodeList = new ArrayList<>();
            for (Goods goods : goodsList) {
                if (!StringUtils.isEmpty(goods.getBarcode())) {
                    barcodeList.add(goods.getBarcode());
                }
            }
            // 根据条形码查询已存在商品信息
            List<Goods> existGoodsList = findAllExistGoods(shopId, barcodeList);
            List<Goods> excludeGoodsList = new ArrayList<>();
            // 被排除上传商品数据
            if (!CollectionUtils.isEmpty(existGoodsList)) {
                excludeGoodsList = getExcludeGoodsFromGoodsList(existGoodsList, goodsList);
            }
            // 已存在商品不需要插入，直接更新即可
            goodsList.removeAll(excludeGoodsList);
            //批量插入不存在商品数据
            List<Goods> batchResult = goodsService.batchInsertGoods(goodsList);
            if (batchResult == null || batchResult.size() == 0) {
//                return HttpResult.createFAIL("批量导入商品数据失败！");
                logger.info("无新商品导入！");
                batchResult = new ArrayList<Goods>();
            }
            //模拟失败数据
//            List<Goods> failureGoodsList = goodsService.findAll(Arrays.asList(new Integer[]{338}));
            // 将已存在商品数据添加到batchResult，以便后面上传商品图片
            batchResult.addAll(excludeGoodsList);
            Map<String, List<Goods>> ossResultMap = postImgToOSS(batchResult);

            //存放上传成功商品数据
            List<Goods> successGoodsList = ossResultMap.get("successGoodsList");
            //失败商品数据
            List<Goods> failureGoodsList = ossResultMap.get("failureGoodsList");

            int successGoodsNum = 0;
            if (successGoodsList.size() > 0) {
                //更新上传成功商品数据
                successGoodsNum = goodsService.batchUpdate(successGoodsList);
                logger.info("成功导入商品数据: " + successGoodsNum);
            }
            //返回批量操作结果
            resultDataMap.put("totalNum", batchResult.size());
            resultDataMap.put("successNum", successGoodsNum);
            resultDataMap.put("failureNum", failureGoodsList.size());

            long endTime = System.currentTimeMillis();
            logger.info("导入商品用时:" + (endTime - startTime) / 60000.0 + "分钟");
            logger.info("导入商品结果：total=" + batchResult.size() + " success=" + successGoodsNum + " failure=" + failureGoodsList.size());

            //批量索引商品数据至Solr
            if (!CollectionUtils.isEmpty(successGoodsList) && successGoodsNum == successGoodsList.size()) {
                //索引数据不能影响导入操作
                try {
                    searchGoodsService.addBatch(successGoodsList);
                } catch (Exception e) {
                    logger.error("*** 索引数据至solr出错！errMsg:" + e);
                }
            }
            //失败数据缓存至redis
            if (!CollectionUtils.isEmpty(failureGoodsList)) {
                logger.info("***** 缓存失败商品数据：{}", failureGoodsList);
                redisService.setObjectCacheValue(FAIL_GOODS_REDIS_KEY + shopId, failureGoodsList, FAIL_GOODS_REDIS_DBINDEX);
            }
        } catch (Exception e) {
            logger.error("*** 读取excel文件出错error:" + e.toString(), e);
            return HttpResult.createFAIL("导入商品失败！");
        }
        return HttpResult.createSuccess("批量导入商品数据成功！", resultDataMap);
    }

    @ApiOperation(value = "批量导入商品图片和定位信息")
    @RequestMapping(value = "/batch/upPicSid", method = RequestMethod.POST)
    public HttpResult upPictrueAndSid(
            @RequestParam(name = "excel") MultipartFile goodsExcelFile,
            Integer shopId
    ) {
        /**
         * 注意：先上传图片信息至服务器
         * 步骤：
         * 1、读取excle表格
         * 2、根据条码查询goodsRenewal表 组装goods对象，
         * 当status = 0 新增 goods ;当 status =1 ，属于重复导入 update goods。
         */
        logger.info("批量导入商品图片和定位信息接口 shopId="+shopId);
        if(shopId == null){
            return HttpResult.createFAIL("请选择商店！");
        }

        //00112 -- 深圳沙井天虹
        //02402 -- 天虹北京新奥
        String oldShopId = null;
        if (shopId == 15) {
            oldShopId = "00112";
        } else if (shopId == 25) {
            oldShopId = "00112";
        }
        String excelFilePath = FileUtil.saveFile(goodsExcelFile);
        ExcelUtil excelUtil = new ExcelUtil();
        List<Row> rowList = null;
        //批量导入数据结果
        Map<String, Object> resultDataMap = new HashMap<String, Object>();
        try {
            rowList = excelUtil.readExcel(excelFilePath);
            if (rowList.size() <= 1) {//只有标题栏或者什么都没有
                return HttpResult.createFAIL("excel文件没有数据！");
            }
            //删除上传的excel文件
            FileUtil.delFile(excelFilePath);

            //查询超市信息
            Shop shop = shopService.findOne(shopId);

            List<Goods> insertList = new ArrayList<>();
            List<Goods> updateList = new ArrayList<>();
            List<Goods> batchResult = new ArrayList<>();
            for (int i = 1; i < rowList.size(); i++) {
                Row row = rowList.get(i);
                //条码
                String barcode = excelUtil.getCellValue(row.getCell(0));
                if (barcode == null || barcode.trim().length() <= 0) {
                    return null;
                }
                Goods goods = new Goods();
                goods.setCoverImgUrl(excelUtil.getCellValue(row.getCell(1)));//商品小图
                goods.setDetailImgUrl(excelUtil.getCellValue(row.getCell(2)));//商品大图
                goods.setDescr(excelUtil.getCellValue(row.getCell(3)));//商品特征
                goods.setSid(excelUtil.getCellValue(row.getCell(4)));//商品位置
                goods.setLocation(excelUtil.getCellValue(row.getCell(5)));//货架信息

                GoodsRenewal goodsRenewal = goodsRenewalService.findByBarcodeAndShopId(barcode, oldShopId);
                if(goodsRenewal == null){
                    return HttpResult.createFAIL("接口表暂无该条码商品！");
                }
                goods.setBarcode(goodsRenewal.getBarcode());
                goods.setTitle(goodsRenewal.getTitle());
                Double price = goodsRenewal.getPrice() * 100;
                goods.setPrice(price);
                goods.setNormalPrice(price.intValue());
                //会员价
                goods.setMemberPrice(goodsRenewal.getMemberPrice() * 100);
                //促销原价
                goods.setPromotionalPrice(goodsRenewal.getPromotionalPrice() * 100);
                //促销价
                goods.setPromotionalSalePrice(goodsRenewal.getPromotionalSalePrice() * 100);
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                if (org.apache.commons.lang3.StringUtils.isNotBlank(goodsRenewal.getPromotionStartDate())) {
                    goods.setPromotionStartDate(sDateFormat.parse(goodsRenewal.getPromotionStartDate()));
                }
                if (org.apache.commons.lang3.StringUtils.isNotBlank(goodsRenewal.getPromotionEndDate())) {
                    goods.setPromotionEndDate(sDateFormat.parse(goodsRenewal.getPromotionEndDate()));
                }
                if ("880201,880202,880203,880205,880206,880207,880208,880301, 880302,880303,880304,990231,990307".contains(goodsRenewal.getSubCategoryId())) {
                    goods.setCategory(goodsCategoryService.findByCatName("生鲜熟食", shopId));
                } else if ("880209,880401,880402,880405,880416,880601,990215".contains(goodsRenewal.getSubCategoryId())) {
                    goods.setCategory(goodsCategoryService.findByCatName("粮油副食", shopId));
                } else if ("880701,880702".contains(goodsRenewal.getSubCategoryId())) {
                    goods.setCategory(goodsCategoryService.findByCatName("低温日配", shopId));
                } else if ("880307,880406,880409,880410,880411,880603,990223,990225".contains(goodsRenewal.getSubCategoryId())) {
                    goods.setCategory(goodsCategoryService.findByCatName("休闲食品", shopId));
                } else if ("880407,880408,880602,990218,990220".contains(goodsRenewal.getSubCategoryId())) {
                    goods.setCategory(goodsCategoryService.findByCatName("酒水饮料", shopId));
                } else if ("880403,880414,990203".contains(goodsRenewal.getSubCategoryId())) {
                    goods.setCategory(goodsCategoryService.findByCatName("冲饮保健", shopId));
                } else if ("880412,880413,880415,990217".contains(goodsRenewal.getSubCategoryId())) {
                    goods.setCategory(goodsCategoryService.findByCatName("婴幼宠", shopId));
                } else if ("880501,880502,880503,880504,880505,880506,880507,880509,881001,880525".contains(goodsRenewal.getSubCategoryId())) {
                    goods.setCategory(goodsCategoryService.findByCatName("个人护理", shopId));
                } else if ("880510,880511,880512,880513,880514,880518,880519,880521,880522,880523,880524,990104,990211".contains(goodsRenewal.getSubCategoryId())) {
                    goods.setCategory(goodsCategoryService.findByCatName("家居厨房", shopId));
                    //                        }else if("0,880111,880299,880417,881603,881605".contains(goodsRenewal.getSubCategoryId())){
                    //                            goods.setCategory(goodsCategoryService.findByCatName("其他",shopId));
                } else if ("881701,990216".contains(goodsRenewal.getSubCategoryId())) {
                    goods.setCategory(null);//(物料)不显示
                } else {
                    goods.setCategory(goodsCategoryService.findByCatName("其他", shopId));
                }

                //品牌
                goods.setBrandName(goodsRenewal.getBrandName());
                //备注
                String remark = "规格:" + goodsRenewal.getSpecification() +
                        "\n品牌:" + goodsRenewal.getBrandName() +
                        "\n产地:" + goodsRenewal.getOrigin() +
                        "\n单位:" + goodsRenewal.getUnitName() +
                        "\n详情:" + (org.apache.commons.lang3.StringUtils.isBlank(goodsRenewal.getDescription()) ? "见商品详情" : goodsRenewal.getDescription());
                goods.setDescr(remark);
                goods.setStock(goodsRenewal.getStock());
                //商店
                goods.setShop(shop);

                goods.setFlag("1");
                //导入数据默认显示位置
                goods.setSortNo(DEFAULT_SORTNO);
                //默认不作新品举荐
                goods.setNewRecommend("0");

                if (goodsRenewal.getStatus() == 0) {
                    //新增
                    insertList.add(goods);
                    goodsRenewal.setStatus(1);
                    goodsRenewalService.save(goodsRenewal);
                } else {
                    //更新
                    Goods temp = goodsService.findByBarcodeAndShopId(barcode, shopId);
                    goods.setId(temp.getId());
                    updateList.add(goods);
                }

            }
            if (insertList.size() > 0) {
                batchResult = goodsService.batchInsertGoods(insertList);
            }
            if (updateList.size() > 0) {
                batchResult.addAll(updateList);
            }

            Map<String, List<Goods>> ossResultMap = postImgToOSS(batchResult);
            //存放上传成功商品数据
            List<Goods> successGoodsList = ossResultMap.get("successGoodsList");
            //失败商品数据
            List<Goods> failureGoodsList = ossResultMap.get("failureGoodsList");

            int successGoodsNum = 0;
            if (successGoodsList.size() > 0) {
                //更新上传成功商品数据
                successGoodsNum = goodsService.batchUpdate(successGoodsList);
                logger.info("成功导入商品数据: " + successGoodsNum);
            }


            //返回批量操作结果
            resultDataMap.put("totalNum", batchResult.size());
            resultDataMap.put("successNum", successGoodsNum);
            resultDataMap.put("failureNum", failureGoodsList.size());

            //批量索引商品数据至Solr
            if (!CollectionUtils.isEmpty(successGoodsList) && successGoodsNum == successGoodsList.size()) {
                //索引数据不能影响导入操作
                try {
                    searchGoodsService.addBatch(successGoodsList);
                } catch (Exception e) {
                    logger.error("*** 索引数据至solr出错！errMsg:" + e);
                }
            }
            //失败数据缓存至redis
            if (!CollectionUtils.isEmpty(failureGoodsList)) {
                logger.info("***** 缓存失败商品数据：{}", failureGoodsList);
                redisService.setObjectCacheValue(FAIL_GOODS_REDIS_KEY + shopId, failureGoodsList, FAIL_GOODS_REDIS_DBINDEX);
            }

        } catch (Exception e) {
            logger.error("接口异常", e.getMessage());
            return HttpResult.createFAIL("导入商品失败！");
        }
        return HttpResult.createSuccess("批量导入商品图片位置数据成功！", resultDataMap);

    }


    @ApiOperation(value = "批量处理上传图片失败商品数据")
    @RequestMapping(value = "/handle/failGoods/{shopId}", method = RequestMethod.GET)
    public HttpResult handleFailGoodsImg(@PathVariable("shopId") Integer shopId) {
        logger.info("******** 开始调用方法: handleFailGoodsImg()处理上传失败图片！,参数shopId：{}", shopId);

        Map<String, Object> resultDataMap = new HashMap<String, Object>();
        //从缓存中取出失败数据
        @SuppressWarnings("unchecked")
        List<Goods> cacheFailureGoodsList = (List<Goods>) redisService.getObjectCacheValue(FAIL_GOODS_REDIS_KEY + shopId, FAIL_GOODS_REDIS_DBINDEX);
        if (cacheFailureGoodsList == null || cacheFailureGoodsList.size() == 0) {
            return HttpResult.createFAIL("没有失败商品数据，请先导入商品数据！");
        }
        Map<String, List<Goods>> ossResultMap = postImgToOSS(cacheFailureGoodsList);
        //上传成功商品数据
        List<Goods> successGoodsList = ossResultMap.get("successGoodsList");
        //失败商品数
        List<Goods> failureGoodsList = ossResultMap.get("failureGoodsList");
        if (CollectionUtils.isEmpty(failureGoodsList)) {
            //全部续传成功，删除缓存数据
            redisService.delBykey(FAIL_GOODS_REDIS_DBINDEX, FAIL_GOODS_REDIS_KEY + shopId);
            logger.info("**** 删除缓存失败商品数据成功！");
        } else {
            logger.info("***** 缓存失败商品数据：{}", failureGoodsList);
            //还有失败数据，更新缓存
            redisService.setObjectCacheValue(FAIL_GOODS_REDIS_KEY + shopId, failureGoodsList, FAIL_GOODS_REDIS_DBINDEX);
        }
        if (successGoodsList.size() > 0) {
            //更新上传成功商品数据
            int successGoodsNum = goodsService.batchUpdate(successGoodsList);
            logger.info("重新导入商品数据: " + successGoodsNum);

            //返回批量操作结果
            resultDataMap.put("totalNum", cacheFailureGoodsList.size());
            resultDataMap.put("successNum", successGoodsNum);

            resultDataMap.put("failureNum", failureGoodsList.size());
            logger.info("续传商品结果：total=" + (failureGoodsList.size() + successGoodsNum) + " success=" + successGoodsNum + " failure=" + failureGoodsList.size());

            try {
                //批量索引商品数据至Solr
                searchGoodsService.addBatch(successGoodsList);
            } catch (Exception e) {
                logger.error("*** 索引数据至solr出错！errMsg: " + e);
            }
        } else {
            return HttpResult.createFAIL("续传失败，请联系管理员！");
        }
        return HttpResult.createSuccess("续传成功！", resultDataMap);
    }

    @ApiOperation(value = "查询主题关联商品")
    @RequestMapping(value = "/tag", method = {RequestMethod.GET, RequestMethod.POST})
    public HttpResult findGoodsByThemeTag(
            @RequestParam(name = "tag[]", required = false) Integer[] ids,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "rows", defaultValue = "20") Integer pageSize
    ) {
        logger.info("***** 查询主题关联商品信息，参数:{ids= " + Arrays.asList(ids) + " keyword= " + keyword + " pageNo= " + pageNo + " pageSize= " + pageSize + "}");
        if (ids.length == 0) {
            return HttpResult.createFAIL("没有和主题关联的商品");
        }
        keyword = StringUtils.isEmpty(keyword) ? "%" : keyword;
        Page<Goods> goods = goodsService.findThemeGoods(ids, keyword, pageNo, pageSize);
        return HttpResult.createSuccess("查询成功!", goods);
    }

    @ApiOperation(value = "批量将商品从新品举荐中移除")
    @RequestMapping(value = "/removeNewRcommend", method = RequestMethod.GET)
    public HttpResult removeNewRcommend(Integer[] ids) {
        logger.info("***** 批量删除新品举荐：removeNewRcommend()");
        if (ids.length == 0) {
            return HttpResult.createFAIL("请先选择要删除的商品！");
        }
        List<Integer> idList = Arrays.asList(ids);
        logger.info("**** 批量移除新品举荐商品ID = " + idList);

        List<Goods> oldGoodsList = goodsService.findAll(idList);
        Boolean result = goodsService.batchRemoveNewRecommendGoods(oldGoodsList);
        if (!result) {
            HttpResult.createFAIL("移除新品举荐商品失败！");
        }
        return HttpResult.createSuccess("移除成功！");
    }

    @ApiOperation(value = "批量添加商品至新品举荐列表")
    @RequestMapping(value = "/addNewRcommend", method = RequestMethod.POST)
    public HttpResult addGoodsToNewRcommend(
            @RequestParam(name = "shopId") Integer shopId,
            @RequestParam(name = "goodsIds") Integer[] ids
    ) {
        logger.info("***** 批量添加新品举荐：addGoodsToNewRcommend(),参数={shopId= " + shopId + " goodsIds= " + Arrays.asList(ids) + "}");
        if (ids.length == 0) {
            return HttpResult.createFAIL("请至少选择一个要添加到新品举荐列表的商品");
        }
        List<Goods> goods = goodsService.findNewRecommendGoodsByGoodsIdList(shopId, ids);
        Boolean result = goodsService.batchAddNewRecommendGoods(goods);
        if (!result) {
            return HttpResult.createFAIL("添加商品至新品举荐失败！");
        }
        return HttpResult.createSuccess("添加成功！");
    }

    @ApiOperation(value = "导出需要填写Sid和location的商品数据")
    @RequestMapping(value = "/location/export", method = RequestMethod.GET)
    public void exportExcel(Integer shopId, HttpServletResponse response) {
        logger.info("******* 开始下载需要填写Sid和location的商品数据,参数：shopId={}", shopId);
        List<Goods> goodsList = goodsService.findMapGoods(shopId);
        //设置excel标题
        List<String> header = new ArrayList<>();
        header.add("商品编号");
        header.add("所属超市");
        header.add("商品名称");
        header.add("条形码");
        header.add("货架地图编号");
        header.add("商品货架信息");
        header.add("SEO标签");

        List<Map<String, String>> data = new ArrayList<>();
        for (int i = 0; i < goodsList.size(); i++) {//行数据
            Map<String, String> row = new HashMap<>();
            Goods goods = goodsList.get(i);
            row.put("var0", String.valueOf(goods.getId()));
            row.put("var1", goods.getShop().getName());
            row.put("var2", goods.getTitle());
            row.put("var3", goods.getBarcode());
            row.put("var4", goods.getSid());
            row.put("var5", goods.getLocation());
            row.put("var6", goods.getSeoTag());
            data.add(row);
        }
        String fileName = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".xls";
        ExcelUtil excelUtil = new ExcelUtil();
        excelUtil.exportExcel(header, data, fileName, response);
    }

    @ApiOperation(value = "上传填写的Sid和location的商品数据")
    @RequestMapping(value = "/location/update", method = RequestMethod.POST)
    public HttpResult updateSidAndLocation(
            Integer shopId, @RequestParam("excel") MultipartFile dataFile
    ) {
        if (StringUtils.isEmpty(shopId)) {
            return HttpResult.createFAIL("请选择商店！");
        }
        //将上传的excel文件保存至临时目录
        String excelPath = FileUtil.saveFile(dataFile);
        //读取excel
        ExcelUtil excelUtil = new ExcelUtil();
        try {

            Map<String, Row> rowMap = new HashMap<>();
            List<Row> rowList = excelUtil.readExcel(excelPath);
            if (rowList.size() <= 1) {//只有标题栏或者什么都没有
                return HttpResult.createFAIL("excel文件没有数据！");
            }
            //获取所有商品ID
            List<Integer> idList = new ArrayList<>();

            for (int i = 1; i < rowList.size(); i++) {//去除标题行
                int id = Integer.parseInt(excelUtil.getCellValue(rowList.get(i).getCell(0)));
                idList.add(id);
                rowMap.put(shopId + "_" + id, rowList.get(i));
            }
            List<Goods> goodsList = goodsService.findAll(idList);
            //检查选择商店是否正确
            Integer id = goodsList.get(0).getShop().getId();
            if (!String.valueOf(shopId).equals(id.toString())) {
                return HttpResult.createFAIL("请选择正确的商店！");
            }
            for (Goods goods : goodsList) {
                Integer gId = goods.getId();
                Row row = rowMap.get(shopId + "_" + gId);
                String barCode = excelUtil.getCellValue(row.getCell(3));
                if (!StringUtils.isEmpty(barCode)) {
                    goods.setBarcode(barCode);
                }
                String sId = excelUtil.getCellValue(row.getCell(4));
                if (!StringUtils.isEmpty(sId)) {
                    goods.setSid(sId);
                }
                String location = excelUtil.getCellValue(row.getCell(5));
                if (!StringUtils.isEmpty(location)) {
                    goods.setLocation(location);
                }
                String seoTag = excelUtil.getCellValue(row.getCell(6));
                if (!StringUtils.isEmpty(seoTag)) {
                    goods.setSeoTag(seoTag);
                }

            }
            //批量更新
            List<Goods> result = goodsService.batchUpdateGoods(goodsList);
            if (result == null) {
                return HttpResult.createFAIL("导入数据失败！");
            }
        } catch (IOException e) {
            logger.error("***** 读取excel文件出错！", e);
            return HttpResult.createFAIL("读取文件出错，请稍后重试！");
        } catch (Exception e) {
            logger.error("添加Sid、location出错！", e);
        } finally {
            //删除临时文件
            FileUtil.delFile(excelPath);
        }
        return HttpResult.createSuccess("导入数据成功！");
    }

    @ApiOperation(value = "所有商品")
    @RequestMapping(value = "/findAllGoods", method = {RequestMethod.POST})
    public HttpResult findAllGoods(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "8") Integer rows,
            @RequestParam(name = "shopId", required = false) Integer shopId,
            @RequestParam(name = "title", required = false) String title) {
        logger.info("**** 开始调用方法：findAllGoods(),参数={shopId:" + shopId + " pageNum:" + page + " pageSize:" + rows + "}");
        if (shopId == null) {
            return HttpResult.createFAIL("非法接口访问！");
        }
        Map<String, Object> searchMap = new HashMap<String, Object>();
        searchMap.put("EQ_shop.id", shopId);
        if (!StringUtils.isEmpty(title)) {
            searchMap.put("LIKE_title", title);
        }
        searchMap.put("EQ_flag", "1");
        Page<Goods> goods = goodsService.findAll(searchMap, page, rows);
        return HttpResult.createSuccess("查询成功！", goods);
    }

    /**
     * 将excel数据转成商品实体
     */
    private Goods excelRowToGoods(Row row, ExcelUtil excelUtil, Shop shop, Map<String, GoodsCategory> categoryMap) {
        String goodsName = excelUtil.getCellValue(row.getCell(0));
        if (goodsName == null || goodsName.trim().length() <= 0) {
            return null;
        }
        Goods goods = new Goods();
        goods.setShop(shop);//超市
        goods.setTitle(goodsName);//商品名
        //商品分类
        String categoryName = excelUtil.getCellValue(row.getCell(1));
        if (!StringUtils.isEmpty(categoryName)) {
            goods.setCategory(matchGoodsCategoryByCategoryName(shop.getId() + "_" + categoryName.trim(), categoryMap));
        }

        goods.setBarcode(excelUtil.getCellValue(row.getCell(2)).trim());//商品条形码

        String normalPriceValue = excelUtil.getCellValue(row.getCell(3)).trim();
        if (!StringUtils.isEmpty(normalPriceValue)) {
            Integer normalPrice = new BigDecimal(normalPriceValue).movePointRight(2).intValue();
            goods.setNormalPrice(normalPrice);//商品原价
        }


        String promotionPriceValue = excelUtil.getCellValue(row.getCell(4)).trim();
        if (!StringUtils.isEmpty(promotionPriceValue.trim())) {
            Integer promotionPrice = new BigDecimal(promotionPriceValue).movePointRight(2).intValue();
            goods.setPromotionPrice(promotionPrice);//促销价
        }

        goods.setCoverImgUrl(excelUtil.getCellValue(row.getCell(5)));//商品小图
        goods.setDetailImgUrl(excelUtil.getCellValue(row.getCell(6)));//商品大图
        goods.setDescr(excelUtil.getCellValue(row.getCell(7)));//商品特征
        goods.setSid(excelUtil.getCellValue(row.getCell(8)));//商品位置
        goods.setLocation(excelUtil.getCellValue(row.getCell(9)));//货架信息
        goods.setSeoTag(excelUtil.getCellValue(row.getCell(10)));//SEO标签
        goods.setRemark(excelUtil.getCellValue(row.getCell(11)));//备注信息

        String goodsType
                = StringUtils.isEmpty(promotionPriceValue) ? GoodsType.NORMAL.getValue() : GoodsType.PROMOTION.getValue();
        goods.setType(goodsType);
        goods.setFlag("1");
        //导入数据默认显示位置
        goods.setSortNo(DEFAULT_SORTNO);
        //默认不作新品举荐
        goods.setNewRecommend("0");

        return goods;
    }

    /**
     * 根据商品分类名称对上传数据进行匹配
     */
    private GoodsCategory matchGoodsCategoryByCategoryName(String catName, Map<String, GoodsCategory> categoryMap) {
        GoodsCategory goodsCategory = null;

        if (!StringUtils.isEmpty(catName)) {
            goodsCategory = categoryMap.get(catName.trim());
            if (goodsCategory != null) {//匹配失败,直接返回null
                logger.info("**** 匹配成功！catName = " + catName);
            } else {
                goodsCategory = null;
            }
        } else {
            //为空时，默认未知分类
            goodsCategory = null;
        }
        return goodsCategory;
    }

    /**
     * 将商品分类集合数据转化为以:
     * key -- shopId_catName --确保唯一性
     * value -- GoodsCategory
     * 格式Map集合，方便解析excel时进行匹配
     */
    private Map<String, GoodsCategory> convertGoodsCategoryList2Map(List<GoodsCategory> categories) {
        Map<String, GoodsCategory> categoryMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(categories)) {
            for (GoodsCategory category : categories) {
                String key = category.getShop().getId() + "_" + category.getCatName();
                categoryMap.put(key, category);
            }
        }
        return categoryMap;
    }

    /**
     * 上传图片OSS服务器，返回上传成功商品数据，保存上传失败商品数据
     *
     * @param batchInsertGoodsList 待更新商品数据
     * @return
     */
    private Map<String, List<Goods>> postImgToOSS(List<Goods> batchInsertGoodsList) {
        Map<String, List<Goods>> oosResultMap = new HashMap<String, List<Goods>>();
        //缺少商品图片或excel填写图片名与商品图片文件名不匹配商品
//        List<Goods> noImgGoodsList = new ArrayList<Goods>();
        //成功同步商品图片至OSS
        List<Goods> successGoodsList = new ArrayList<Goods>();
        //失败商品
        List<Goods> failureGoodsList = new ArrayList<>();

        for (int i = 0; i < batchInsertGoodsList.size(); i++) {
            Goods goods = batchInsertGoodsList.get(i);
            //获取图片名称
            String coverImg = goods.getCoverImgUrl();
            String detailImg = goods.getDetailImgUrl();
            if (StringUtils.isEmpty(coverImg) || StringUtils.isEmpty(detailImg)) {
                addGoodsToList(failureGoodsList, goods);
            } else if ((coverImg.startsWith("http") || coverImg.startsWith("https")) &&
                    (detailImg.startsWith("http") || detailImg.startsWith("https"))) {
                //完整图片路径则不需要上传,直接使用给定路径
                addGoodsToList(successGoodsList, goods);
            } else {
                //上传商品小图
                String ossCoverImg = uploadAndReturnResult(coverImg, goods, failureGoodsList);
                if (!StringUtils.isEmpty(ossCoverImg)) {
                    //上传大图
                    String ossDetailImg = uploadAndReturnResult(detailImg, goods, failureGoodsList);
                    if (!StringUtils.isEmpty(ossDetailImg)) {
                        //只有两张图片同时上传成功，才算成功，否则失败
                        goods.setCoverImgUrl(ossCoverImg);
                        goods.setDetailImgUrl(ossDetailImg);
                        addGoodsToList(successGoodsList, goods);
                    } else {
                        addGoodsToList(failureGoodsList, goods);
                    }
                } else {
                    /*
                        excel中图片名称和商品图片文件名匹配，但由于网络等不可控原因导致
                        图片没有被同步到阿里OSS服务器的商品数据
                        解决办法：
                         1. 通过多次续传完成失败图片上传
                         2. 通过编辑手动上传至OSS
                     */
                    addGoodsToList(failureGoodsList, goods);
                }
            }
        }
        oosResultMap.put("successGoodsList", successGoodsList);
        oosResultMap.put("failureGoodsList", failureGoodsList);

        return oosResultMap;
    }

    /**
     * 因为两张图片需要分开上传，所以存在一张成功一张失败的情况，
     * 为了避免在list中出现重复的Goods数据,并且不改动后续代码逻辑，
     * 所以做此不转换，模拟Set功能。最终效果是: 若果第一张图片上传
     * 失败，那么该商品两张图片都需要重传，如果第一张成功，第二张失败，
     * 那么第二张图片需要通过后台<b color="red">编辑商品</b>单独上传。
     */
    private void addGoodsToList(List<Goods> goodsList, Goods goods) {
        if (!goodsList.contains(goods)) {
            goodsList.add(goods);
        }
    }

    /**
     * @param imgName          需要上传的图片名
     * @param goods            图片所属商品
     * @param failureGoodsList 上传图片失败商品集合
     * @return 返回上传结果，成功：true,失败：false
     */
    private String uploadAndReturnResult(
            String imgName, Goods goods, List<Goods> failureGoodsList
    ) {
        if (!imgName.startsWith("http") && !imgName.startsWith("https")) {
            //创建图片文件
            File imgFile = createImgFile(imgName);
            //上传图片
            return uploadImgToOss(imgFile, goods, failureGoodsList);
        } else {
            //全路径资源图片直接返回URL
            return imgName;
        }
    }

    private File createImgFile(String imgName) {
        String imgFilePrefix = SystemConfig.getInstance().getFileTemp();
        return new File(imgFilePrefix + imgName);
    }

    private String uploadImgToOss(
            File file, Goods goods, List<Goods> failureGoodsList
    ) {
        String ossImgUrl = null;
        if (file.exists()) { //商品图片存在
            //上传图片到OSS服务器
            ossImgUrl = PostObjectToOss.PostObject(Contants.GOODS_IMG_TEMP_FOLDER, file.getAbsolutePath());
            if (StringUtils.isEmpty(ossImgUrl)) {
                //打印日志，方便定位问题
                logger.info("******** 图片：{},上传失败！！", file.getAbsolutePath());
            }
        } else {
            logger.info("******* 图片文件不存在！filePath ={}", file.getAbsolutePath());
            addGoodsToList(failureGoodsList, goods);
        }
        return ossImgUrl;
    }

    /**
     * 查询所有已存在商品，避免重复添加
     *
     * @param shopId
     * @param barcodeList
     * @return
     */
    private List<Goods> findAllExistGoods(Integer shopId, List<String> barcodeList) {
        List<String> subBarcodeList;
        List<Goods> existGoodsList = new ArrayList<>();
        // 循环查询计数器
        int count = 0;
        int maxCondition = 900;
        do {
            count++;
            // in()条件最大数量不能超过1000
            if (barcodeList.size() / maxCondition != 0) {
                subBarcodeList = barcodeList.subList(0, maxCondition);
                List<Goods> goodsList = goodsService.findGoodsInBarcodes(subBarcodeList.toArray(new String[maxCondition]), shopId);
                existGoodsList.addAll(goodsList);
                barcodeList.removeAll(subBarcodeList);
            } else {
                List<Goods> goodsList = goodsService.findGoodsInBarcodes(barcodeList.toArray(new String[barcodeList.size()]), shopId);
                existGoodsList.addAll(goodsList);
                barcodeList.clear();
            }
        } while (barcodeList.size() > 0);

        return existGoodsList;
    }

    /**
     * 从上传的商品数据中排除已有商品数据,并返回
     *
     * @param existGoodsList
     * @param goodsList
     * @return
     */
    private List<Goods> getExcludeGoodsFromGoodsList(List<Goods> existGoodsList, List<Goods> goodsList) {
        List<Goods> exclude = new ArrayList<>();
        for (Goods existGoods : existGoodsList) {
            for (Goods goods : goodsList) {
                if (existGoods.getBarcode().equals(goods.getBarcode())) {
                    goods.setId(existGoods.getId());
                    goods.setCreatedTime(existGoods.getCreatedTime());
                    exclude.add(goods);
                }
            }
        }
        return exclude;
    }

    public List<Integer> StringToInt(String[] arrs) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < arrs.length; i++) {
            list.add(Integer.parseInt(arrs[i]));
        }
        return list;
    }
}
