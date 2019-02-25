package cn.huace.controller.goods;

import java.util.*;
import java.util.stream.Collectors;

import cn.huace.ad.entity.Ad;
import cn.huace.ad.service.AdService;
import cn.huace.goods.document.SearchGoods;
import cn.huace.goods.entity.*;
import cn.huace.goods.enums.GoodsType;
import cn.huace.goods.enums.TempGoodsEnum;
import cn.huace.goods.enums.TempGoodsEnum.TempGoods;
import cn.huace.goods.service.*;
import cn.huace.statis.utils.StatContants;
import cn.huace.statis.utils.StatUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import cn.huace.common.bean.HttpResult;
import cn.huace.controller.base.BaseFrontController;
import cn.huace.goods.config.GoodsConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;


/**
 *  商品管理
 * Created by yld on 2017/5/16.
 */
@Slf4j
@RestController
@Api(value = "/product",description = "商品管理")
@RequestMapping("/product")
public class GoodsController extends BaseFrontController{
    @Autowired
    private GoodsService goodsService;

    @Autowired
    private AdService adService;

    @Autowired
    private SearchGoodsService searchGoodsService;

    @Autowired
    private GoodsCategoryService categoryService;

    @Autowired
    private ThemeService themeService;

    @Autowired
    private ThemeTypeService themeTypeService;

    @Autowired
    private GoodsRenewalService goodsRenewalService;

    @Autowired
    private GoodsClassLocService goodsClassLocService;

    //状态标识位
    private static final String FLAG = "1";
    //特价商品主题类型
    private static final String PROMOTION_THEMETYPE_NAME ="特价主题";

    @ApiOperation(value = "所有商品")
    @RequestMapping(value="/list",method = {RequestMethod.POST})
    public HttpResult findAllGoods(
            HttpServletRequest request,Integer shopId,Integer categoryId,
            @RequestParam(defaultValue = "1",name = "pageNum")Integer pageNum,
            @RequestParam(defaultValue = "20",name = "pageSize")Integer pageSize){

        log.info("**** 开始调用方法：findAllGoods(),参数={shopId:"+shopId+" categoryId:"+categoryId+" pageNum:"+pageNum+" pageSize:"+pageSize+"}");
        shopId = findShopId(request);
        if(shopId == null){
            return HttpResult.createFAIL("非法接口访问！");
        }
        log.info("****** findAllGoods()超市ID："+shopId);
        Map<String,Object> searchMap = new HashMap<>();
        if(!StringUtils.isEmpty(shopId)){
            searchMap.put("EQ_shop.id",shopId);
        }
        if(!StringUtils.isEmpty(categoryId)){
            searchMap.put("EQ_category.id",categoryId);
        }
//        searchMap.put("NE_type",GoodsType.OTHER.getValue());
        searchMap.put("EQ_flag","1");
        //屏蔽货架ID为空的
        searchMap.put("NE_sid"," ");


        Page<Goods> goods = goodsService.findAllGoodsForApp(searchMap,pageNum,pageSize);

        /*//临时屏蔽普通商品价格 add by Loring 2018/08/24 start
        List<Goods> listTemp = goods.getContent();
        listTemp.forEach(goodsTemp -> {
            if(goodsTemp.getNewRecommend().equals("0") && goodsTemp.getType().equals("normal")){
                goodsTemp.setNormalPrice(null);
                goodsTemp.setPromotionPrice(null);
            }
        });
        goods = new PageImpl<>(listTemp,null,goods.getTotalElements());
        //临时屏蔽普通商品价格 add by Loring 2018/08/24 end*/

        return HttpResult.createSuccess("查询成功！",convertGoodsToGoodsListOV(goods.getContent()),goods.getTotalElements());
    }

    @ApiOperation(value = "所有促销商品")
    @RequestMapping(value = "/promotion/list",method = RequestMethod.POST)
    public HttpResult findAllPromotionGoods(
            HttpServletRequest request,Integer shopId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize
    ){
        log.info("***** 开始调用方法：findAllPromotionGoods(),参数={shopId="+shopId+" pageNum="+pageNum+" pageSize="+pageSize+"}");
        shopId = findShopId(request);
        if(shopId == null){
            return HttpResult.createFAIL("非法接口访问！");
        }
        log.info("****** findAllPromotionGoods()超市ID："+shopId);
        Page<Goods> promotionGoods = goodsService.findAllPromotionGoodsForApp(shopId,pageNum,pageSize);

        return HttpResult.createSuccess("查询成功！",convertGoodsToGoodsListOV(promotionGoods.getContent()),promotionGoods.getTotalElements());
    }

    @ApiOperation(value = "商品详情")
    @RequestMapping(value="/detail",method = RequestMethod.POST)
    public HttpResult getGoodsDetail(HttpServletRequest request,Integer shopId,Integer id){
        log.info("***** 开始调用方法:getGoodsDetail(),参数={shopId:"+shopId+" id:"+id+"}");
        shopId = findShopId(request);
        if (shopId == null){
            return HttpResult.createFAIL("非法接口访问！");
        }
        log.info("****** getGoodsDetail()超市ID："+shopId);
        if(id == null){
            return HttpResult.createFAIL("参数:商品id不能为空");
        }
        Goods goodsDetail = goodsService.findGoodsDetailForApp(id,shopId);
        return HttpResult.createSuccess("查询成功！",convertGoodsToGoodsDetailOV(goodsDetail),goodsDetail==null?0:1);
    }

    @ApiOperation(value = "搜索商品列表")
    @RequestMapping(value = "/search/list",method = RequestMethod.POST)
    public HttpResult searchGoods(
            HttpServletRequest request,Integer shopId,
            @RequestParam(name = "key",required = false) String goodsName,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "100") Integer pageSize){
        if(goodsName.length()>=20){
            goodsName =  goodsName.substring(0,20);
        }
        log.info("***** 开始调用方法:searchGoods(),参数={ key:"+goodsName+"}");
        shopId = findShopId(request);
        if(shopId == null){
            return HttpResult.createFAIL("非法接口访问！");
        }
        log.info("****** searchGoods()超市ID："+shopId);
        //goodsName为空时提示输入关键字
        if(StringUtils.isEmpty(goodsName)){
            return HttpResult.createFAIL("请输入关键字！");
        }
        Page<SearchGoods> searchResult = searchGoodsService.searchGoodsByShopId(shopId,goodsName,pageNum,pageSize);
      
        //搜索引擎没有查到数据 进行数据库查询 
        if(null == searchResult || searchResult.getSize() == 0 || searchResult.getContent().size()==0 || searchResult.getTotalElements() == 0){
        	log.info("***********"+searchResult.getTotalElements()+"*********进行数据库查询");
        	//进行数据库查询
        	Page<Goods> goodsResult = goodsService.searchGoodsForApp(shopId,goodsName,pageNum,pageSize);
        	
        	PageRequest pageRequest = new PageRequest(pageNum,pageSize,Sort.Direction.DESC,"modifiedTime");
        	
        	List<SearchGoods> retData =  goodsResult.getContent().stream().map( goods -> GoodsToSearchGoods(goods,new SearchGoods())).collect(Collectors.toList());
    		
        	searchResult = new PageImpl<>(retData,pageRequest,retData.size());
        	
        	log.info("***********"+searchResult.getContent().size()+"*********进行数据库查询结束");
        }
        

        /*//临时屏蔽普通商品价格 add by Loring 2018/08/24 start
        List<SearchGoods> listTemp = searchResult.getContent();
        listTemp.forEach(goodsTemp -> {
            if(goodsTemp.getNewRecommend().equals("0") && goodsTemp.getType().equals("normal")){
                goodsTemp.setNormalPrice(null);
                goodsTemp.setPromotionPrice(null);
            }
        });
        searchResult = new PageImpl<>(listTemp,null,searchResult.getTotalElements());
        //临时屏蔽普通商品价格 add by Loring 2018/08/24 end*/

        List<String> args = new ArrayList<>();
        args.add(StatContants.STAT_SHOP_ID+StatContants.STAT_COLON+shopId);
        args.add(StatContants.SEARCH_KEY.KEYWORD+StatContants.STAT_COLON+goodsName);
        args.add(StatContants.SEARCH_KEY.RESULT+StatContants.STAT_COLON+searchResult.getContent().size());
        args.add(StatContants.SEARCH_KEY.REL_GOODS+StatContants.STAT_COLON+getRelGoods(searchResult.getContent()));
        StatUtils.stat(StatContants.STAT_TYPE_SEARCH,args.toArray(new String[args.size()]));

        return HttpResult.createSuccess("搜索成功！",convertSearchGoodsToGoodsListOV(searchResult.getContent(),goodsName),searchResult.getTotalElements());
    }
  
    
    @ApiOperation(value = "举荐商品")
    @RequestMapping(value = "/recommend",method = RequestMethod.POST)
    public HttpResult recommendGoods(
            HttpServletRequest request,Integer shopId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize){
        log.info("***** 开始调用方法:recommendGoods(),参数={shopId="+shopId+" pageNum="+pageNum+" pageSize="+pageSize+"}");
        shopId = findShopId(request);
        if(shopId == null){
            return HttpResult.createFAIL("非法接口访问！");
        }
        log.info("****** recommendGoods()超市ID："+shopId);
        List<Integer> idListAll = goodsService.findAllGoodsId(shopId);
        if(CollectionUtils.isEmpty(idListAll)){
            return HttpResult.createSuccess("该超市没有商品！");
        }

        List<Integer> recommnedIds = new ArrayList<Integer>();
        Integer recommendSize= Integer.parseInt(GoodsConfig.getInstance().getRecommendGoodsSize());

        if(idListAll.size() > recommendSize ){
            Integer minIndex = 0;
            Integer maxIndex = idListAll.size();

            //随机生成举荐商品数量下标值
            List<Integer> indexList = generateRandomGoodsIdArray(minIndex,maxIndex,recommendSize);
            for(int i = 0;i< indexList.size();i++){
                recommnedIds.add(idListAll.get(indexList.get(i)));
            }
        }else{
            //商品总数小于举荐商品数
            recommnedIds.addAll(idListAll);
        }

        log.info("**** 举荐商品ID={"+recommnedIds+"}");
//        List<String> recommnedIdsStr = new ArrayList<String>();
//        for(Integer id:recommnedIds){
//            recommnedIdsStr.add(id+"");
//        }
        Integer[] ids = recommnedIds.toArray(new Integer[recommnedIds.size()]);
        //查询举荐商品
        Page<Goods> recommendGoods = goodsService.findRecommendGoodsForApp(shopId,ids,pageNum,pageSize);
//        Page<SearchGoods> recommendGoods = searchGoodsService.searchGoodsByIds(recommnedIdsStr,pageNum,pageSize);
        return HttpResult.createSuccess("查询成功！",convertGoodsToGoodsListOV(recommendGoods.getContent()));
    }

    @ApiOperation(value = "根据商品条形码查询商品")
    @RequestMapping(value="/bar_code",method = RequestMethod.POST)
    public HttpResult findGoodsByBarcode(HttpServletRequest request,String barCode,Integer shopId){
        shopId = findShopId(request);
        if(shopId == null){
            return HttpResult.createFAIL("非法接口访问！");
        }
        log.info("****** findGoodsByBarcode()超市ID："+shopId);
        if(StringUtils.isEmpty(barCode)){
            return HttpResult.createFAIL("商品条形码不能为空!");
        }
        //判断商品条码是否为生鲜区打称商品
        TempGoods tempGoods = null;
        String sourceBarcode = barCode;
        if(TempGoodsEnum.isTempGoods(barCode)){
        	tempGoods = TempGoodsEnum.getTempGoods(barCode);
        	barCode = tempGoods.getCode();
        }
        
        Goods goods = goodsService.findGoodsByBarcodeForApp(barCode,shopId);
        BarcodeGoodsDetailOV barcodeGoodsDetailOV = new BarcodeGoodsDetailOV();
        if(goods != null){
            barcodeGoodsDetailOV.setId(goods.getId());
            barcodeGoodsDetailOV.setTitle(goods.getTitle());
            String imgUrl
                    = StringUtils.isEmpty(goods.getCoverImgUrl())?goods.getDetailImgUrl():goods.getCoverImgUrl();
            barcodeGoodsDetailOV.setUrl(imgUrl);
            if(null==tempGoods){
            	barcodeGoodsDetailOV.setNormalPrice(goods.getNormalPrice());
            	barcodeGoodsDetailOV.setPromotionPrice(goods.getPromotionPrice());
            	barcodeGoodsDetailOV.setPrice(goods.getPrice());
            	barcodeGoodsDetailOV.setMemberPrice(goods.getMemberPrice());
            	barcodeGoodsDetailOV.setPromotionalPrice(goods.getPromotionalPrice());
            	barcodeGoodsDetailOV.setPromotionalSalePrice(goods.getPromotionalSalePrice());
            }else{//临时打称商品价格
            	barcodeGoodsDetailOV.setNormalPrice(tempGoods.getPrice().intValue());
            	barcodeGoodsDetailOV.setPromotionPrice(tempGoods.getPrice().intValue());
            	barcodeGoodsDetailOV.setPrice(tempGoods.getPrice());
            	barcodeGoodsDetailOV.setMemberPrice(tempGoods.getPrice());
            	barcodeGoodsDetailOV.setPromotionalPrice(tempGoods.getPrice());
            	barcodeGoodsDetailOV.setPromotionalSalePrice(tempGoods.getPrice());
            }
            
            barcodeGoodsDetailOV.setSid(goods.getSid());
            barcodeGoodsDetailOV.setLocation(goods.getLocation());
            barcodeGoodsDetailOV.setDesc(goods.getDescr());

            barcodeGoodsDetailOV.setPromotionStartDate(goods.getPromotionStartDate());
            barcodeGoodsDetailOV.setPromotionEndDate(goods.getPromotionEndDate());

            barcodeGoodsDetailOV.setBarcode(null==tempGoods?goods.getBarcode():sourceBarcode);

            return HttpResult.createSuccess("查询成功！",barcodeGoodsDetailOV);
        }

        return HttpResult.createFAIL("该商品暂未入库，如有需要，请联系店面人员",null);
    }

    @ApiOperation(value = "根据多个商品条形码查询商品")
    @RequestMapping(value="/bar_codes",method = RequestMethod.POST)
    public HttpResult findGoodsByBarcodes(HttpServletRequest request,String barCodes){
        Integer shopId = findShopId(request);
        if(shopId == null){
            return HttpResult.createFAIL("非法接口访问！");
        }
        log.info("****** findGoodsByBarcodes()超市ID："+shopId +",条形码："+barCodes);
       List<Goods> list = goodsService.findGoodsInBarcodes(barCodes.split(","),shopId);
       List retList = list.stream().map(goods -> PoToVo(goods,new BarcodeGoodsDetailOV())).collect(Collectors.toList());
       return HttpResult.createSuccess("查询成功！",retList);
    }

    public BarcodeGoodsDetailOV PoToVo (Goods source, BarcodeGoodsDetailOV target){
        target.setId(source.getId());
        target.setTitle(source.getTitle());
        String imgUrl
                = StringUtils.isEmpty(source.getCoverImgUrl())?source.getDetailImgUrl():source.getCoverImgUrl();
        target.setUrl(imgUrl);
        target.setNormalPrice(source.getNormalPrice());
        target.setPromotionPrice(source.getPromotionPrice());
        target.setSid(source.getSid());
        target.setLocation(source.getLocation());
        target.setDesc(source.getDescr());

        target.setPrice(source.getPrice());
        target.setMemberPrice(source.getMemberPrice());
        target.setPromotionalPrice(source.getPromotionalPrice());
        target.setPromotionalSalePrice(source.getPromotionalSalePrice());
        target.setPromotionStartDate(source.getPromotionStartDate());
        target.setPromotionEndDate(source.getPromotionEndDate());

        target.setBarcode(source.getBarcode());

        return target;
    }

    @ApiOperation(value = "根据shopId查询该超市下商品分类信息")
    @RequestMapping(value="/category",method = RequestMethod.POST)
    public HttpResult findCategories(
            HttpServletRequest request,
            @RequestParam(name = "shopId",required = false) Integer shopId
    ){
        shopId = findShopId(request);
        if(shopId == null){
            return HttpResult.createFAIL("非法接口访问！");
        }
        log.info("****** findCategories()超市ID："+shopId);
        List<GoodsCategory> categories = categoryService.findGoodsCategoryByShopIdForApp(shopId);
        return HttpResult.createSuccess("查询成功！",convertCategoryListToCategoryOV(categories));
    }
    @ApiOperation(value = "查询特价主题及关联商品列表")
    @RequestMapping(value="/promotion/theme",method = {RequestMethod.POST})
    public HttpResult findThemeGoods(
        HttpServletRequest request,
        @RequestParam(name = "shopId",required = false) Integer shopId,
        @RequestParam(defaultValue = "1",name = "pageNum")Integer pageNum,
        @RequestParam(defaultValue = "20",name = "pageSize")Integer pageSize
    ){
        log.info("**** 开始调用方法：findThemeGoods(),参数= shopId:{},pageNum:{},pageSize:{}",shopId,pageNum,pageSize);
        shopId = findShopId(request);
        if(shopId == null){
            return HttpResult.createFAIL("非法接口访问！");
        }
        log.info("****** findThemeGoods()超市ID："+shopId);
        Map<String,Object> result = new HashMap<>();
        ThemeType themeType = themeTypeService.findOneThemeTypeByName(PROMOTION_THEMETYPE_NAME);
        if(themeType == null || !FLAG.equals(themeType.getFlag())){
            return HttpResult.createFAIL("特价商品主题类型不存在！");
        }
        Integer themeTypeId = themeType.getId();
        //查询上架特价主题
        Theme theme = themeService.findOnlineThemeByShopIdAndThemeTypeId(shopId,themeTypeId);
        if(theme == null || !FLAG.equals(themeType.getFlag())){
            return HttpResult.createFAIL("该超市不存在特价商品主题！");
        }
        List<Integer> idList = new ArrayList<>();
        Page<Goods> goods = null;
        //获取主题关联商品Id
        String tag = theme.getTag();

        if(!StringUtils.isEmpty(tag)){
            String[] ids = tag.split(",");
            for(String id:ids){
                if(StringUtils.isEmpty(id)){
                    continue;
                }
                idList.add(Integer.parseInt(id));
            }
            Integer[] idsArr = idList.toArray(new Integer[idList.size()]);
            goods = goodsService.findThemeGoods(idsArr,"",pageNum,pageSize);

            /*//临时屏蔽普通商品价格 add by Loring 2018/08/24 start
            List<Goods> listTemp = goods.getContent();
            listTemp.forEach(goodsTemp -> {
                if(goodsTemp.getNewRecommend().equals("0") && goodsTemp.getType().equals("normal")){
                    goodsTemp.setNormalPrice(null);
                    goodsTemp.setPromotionPrice(null);
                }
            });
            goods = new PageImpl<>(listTemp,null,goods.getTotalElements());
            //临时屏蔽普通商品价格 add by Loring 2018/08/24 end*/
        }
        ThemeOV themeOV = new ThemeOV();
        themeOV.setId(theme.getId());
        themeOV.setCoverImg(theme.getCoverImg());

        result.put("theme",themeOV);
        result.put("goods",convertGoodsToGoodsListOV(goods != null?goods.getContent():null));

        return HttpResult.createSuccess("查询成功！",result);
    }
    @ApiOperation(value = "查询特价商品")
    @RequestMapping(value="/promotion/goods",method = {RequestMethod.POST})
    public HttpResult findPormotionGoods(
            HttpServletRequest request,
            @RequestParam(name = "shopId",required = false) Integer shopId,
            @RequestParam(defaultValue = "1",name = "pageNum")Integer pageNum,
            @RequestParam(defaultValue = "20",name = "pageSize")Integer pageSize){
            log.info("**** 开始调用方法：findPormotionGoods(),参数= shopId:{},pageNum:{},pageSize:{}",shopId,pageNum,pageSize);
            shopId = findShopId(request);
            if(shopId == null){
                return HttpResult.createFAIL("非法接口访问！");
            }
            log.info("****** findPormotionGoods()超市ID："+shopId);
//            Date date = new Date();
//            Map<String,Object> searchMap = new HashMap<>();
//            searchMap.put("EQ_shop.id",shopId);
//            searchMap.put("LTE_promotionStartDate",date);
//            searchMap.put("GTE_promotionEndDate",date);
//            searchMap.put("EQ_flag","1");
//            searchMap.put("NE_sid"," ");
//            Page<Goods> goodsList = goodsService.findAll(searchMap,pageNum,pageSize, Sort.Direction.DESC,"coverImgUrl");
            List<Goods> goodsList =  goodsService.findByPromotionGoods(shopId,pageNum,pageSize);

            List<GoodsListOV> list1 =convertGoodsToGoodsListOV(goodsList != null?goodsList:null);



//            Map<String,Object> searchMap2 = new HashMap<>();
//            searchMap2.put("EQ_shop.id",shopId);
//            searchMap2.put("LTE_promotionStartDate",date);
//            searchMap2.put("GTE_promotionEndDate",date);
//            searchMap2.put("EQ_flag","1");
//            searchMap2.put("EQ_sid"," ");
//
//            Page<Goods> goodsList2 = goodsService.findAll(searchMap2,pageNum,pageSize, Sort.Direction.DESC,"coverImgUrl");
//            List<GoodsListOV> list2 =convertGoodsToGoodsListOV(goodsList2 != null?goodsList2.getContent():null);

            Map<String,Object> result = new HashMap<>();
////            result.put("goods",convertGoodsToGoodsListOV(goodsList != null?goodsList.getContent():null));
//            if(list1 != null && goodsList.getContent().size() < pageNum * pageSize){
//                list1.addAll(list2);
//            }

            result.put("goods",list1);

        return HttpResult.createSuccess("查询成功！",result);
    }


    @ApiOperation(value = "根据themeId查询主题及关联商品列表")
    @RequestMapping(value="/theme",method = {RequestMethod.POST})
    public HttpResult findThemeGoods(
            HttpServletRequest request,
            @RequestParam(name = "shopId",required = false) Integer shopId,
            @RequestParam(name = "themeId") Integer themeId,
            @RequestParam(defaultValue = "1",name = "pageNum")Integer pageNum,
            @RequestParam(defaultValue = "20",name = "pageSize")Integer pageSize
    ){
        log.info("**** 开始调用方法：findThemeGoods(),参数={shopId:"+shopId+" themeId:"+themeId);
        shopId = findShopId(request);
        if(shopId == null){
            return HttpResult.createFAIL("非法接口访问！");
        }
        log.info("****** findThemeGoods()超市ID："+shopId);
        if(StringUtils.isEmpty(themeId)){
            return HttpResult.createFAIL("主题为空！");
        }
        Map<String,Object> result = new HashMap<>();
        Theme theme = themeService.findOne(themeId);
        List<Integer> idList = new ArrayList<>();

        if(theme != null && FLAG.equals(theme.getFlag())){
            //获取主题关联商品Id
            String tag = theme.getTag();

            if(!StringUtils.isEmpty(tag)){
                String[] ids = tag.split(",");
                for(String id:ids){
                    if(StringUtils.isEmpty(id)){
                        continue;
                    }
                    idList.add(Integer.parseInt(id));
                }
            }
            Integer[] ids = idList.toArray(new Integer[idList.size()]);
            Page<Goods> goods = null;
            if(ids.length > 0){
                goods = goodsService.findThemeGoods(ids,"",pageNum,pageSize);

                /*//临时屏蔽普通商品价格 add by Loring 2018/08/24 start
                List<Goods> listTemp = goods.getContent();
                listTemp.forEach(goodsTemp -> {
                    if(goodsTemp.getNewRecommend().equals("0") && goodsTemp.getType().equals("normal")){
                        goodsTemp.setNormalPrice(null);
                        goodsTemp.setPromotionPrice(null);
                    }
                });
                goods = new PageImpl<>(listTemp,null,goods.getTotalElements());
                //临时屏蔽普通商品价格 add by Loring 2018/08/24 end*/
            }
            ThemeOV themeOV = new ThemeOV();
            themeOV.setId(theme.getId());
            themeOV.setCoverImg(theme.getCoverImg());

            result.put("theme",themeOV);
            result.put("goods",convertGoodsToGoodsListOV(goods!=null?goods.getContent():null));
        }else {
            return HttpResult.createFAIL("该主题不存在或已被删除！");
        }
        return HttpResult.createSuccess("查询成功！",result);
    }
    @ApiOperation(value = "新品举荐商品列表接口")
    @RequestMapping(value="/new/recommend",method = {RequestMethod.POST})
    public HttpResult findNewGoodsRecommend(
         HttpServletRequest request,
         @RequestParam(name = "shopId",required = false) Integer shopId,
         @RequestParam(defaultValue = "1",name = "pageNum")Integer pageNum,
         @RequestParam(defaultValue = "20",name = "pageSize")Integer pageSize
    ){
        log.info("**** 开始查询新品举荐商品列表：findNewGoodsRecommend(),参数={shopId:"+shopId+"}");
        shopId = findShopId(request);
        if(shopId == null){
            return HttpResult.createFAIL("非法接口访问！");
        }
        log.info("****** findNewGoodsRecommend()超市ID："+shopId);
        Map<String,Object> resultMap = new HashMap<>();
        //查询广告
        List<Ad> ads = adService.findReccAdList(shopId);

        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("EQ_shop.id",shopId);
        searchMap.put("EQ_newRecommend","1");
        searchMap.put("EQ_flag","1");

        Page<Goods> goods = goodsService.findAll(searchMap,pageNum,pageSize, Sort.Direction.ASC,"modifiedTime");

        List<GoodsListOV> goodsListOVList = convertGoodsToGoodsListOV(goods.getContent());
        resultMap.put("goods",goodsListOVList);
        resultMap.put("ads",ads);

        return HttpResult.createSuccess("查询成功！",resultMap);
    }

    /**
     * 封装分类信息给app端
     * @param categories
     * @return
     */
    private List<CategoryOV> convertCategoryListToCategoryOV(List<GoodsCategory> categories){
        List<CategoryOV> categoryOVList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(categories)){
            for(GoodsCategory category:categories){
                CategoryOV categoryOV = new CategoryOV();
                categoryOV.setId(category.getId());
                categoryOV.setName(category.getCatName());
                categoryOVList.add(categoryOV);
            }
        }
        return categoryOVList;
    }
    /**
     *  封装商品详情数据给app端
     * @param goods
     * @return
     */
    private GoodsDetailOV convertGoodsToGoodsDetailOV(Goods goods){
        GoodsDetailOV goodsDetailOV = new GoodsDetailOV();
        if(goods != null){
            if(org.apache.commons.lang3.StringUtils.isNotBlank(goods.getSid())){
                goodsDetailOV.setSid(goods.getSid());
            }else {
                /*未填写货架编码的商品，采用原始三级分类近视值的默认货架编码*/
                //1、根据barcode shopId 查询 goods_renewal 的 classification_id
                Integer shopId = goods.getShop().getId();
                String oldShopId = null;
                //00112 -- 深圳沙井天虹
                //02402 -- 天虹北京新奥
                if(shopId.equals(15)){
                    oldShopId = "00112";
                }else if(goods.getShop().getName().equals("天虹北京新奥")){
                    oldShopId = "02402";
                }
                GoodsRenewal goodsRenewal = goodsRenewalService.findByBarcodeAndShopId(goods.getBarcode(),oldShopId);
                if(goodsRenewal != null) {
                    //2、根据classification_id shopId 查询 goods_classification_location 的sid
                    Integer classificationId = Integer.parseInt(goodsRenewal.getClassificationId());
                    GoodsClassLoc goodsClassLoc = goodsClassLocService.findByclassIdAndShopId(classificationId, shopId);
                    //3、setSid
                    goodsDetailOV.setSid(goodsClassLoc.getSid());
                }else {
                    log.info("goods_renewal 数据缺失！barcode:"+goods.getBarcode()+"; shopId:"+goods.getShop().getId());
                }
            }
            goodsDetailOV.setLocation(goods.getLocation());
            String imgUrl
                    = StringUtils.isEmpty(goods.getDetailImgUrl())?goods.getCoverImgUrl():goods.getDetailImgUrl();
            goodsDetailOV.setUrl(imgUrl);//小图改成大图
            goodsDetailOV.setDesc(goods.getDescr());
        }
        return goodsDetailOV;
    }

    /**
     * 封装查询数据给app端
     * @return
     */
    private List<GoodsListOV> convertGoodsToGoodsListOV(List<Goods> goodsList){
        List<GoodsListOV> goodsListOVS = new ArrayList<GoodsListOV>();
        if(!CollectionUtils.isEmpty(goodsList)){
            for(Goods goods:goodsList){
                GoodsListOV goodsListOV = new GoodsListOV();
                if(goods != null ){
                    goodsListOV.setId(goods.getId());
                    //判断商品类型
                    if(GoodsType.PROMOTION.getValue().equalsIgnoreCase(goods.getType()) && goods.getPromotionPrice() != null){
                        goodsListOV.setPromotionPrice(goods.getPromotionPrice());
                    }
                    //价格分
                    goodsListOV.setNormalPrice(goods.getNormalPrice());
                    String imgUrl
                            = StringUtils.isEmpty(goods.getCoverImgUrl())?goods.getDetailImgUrl():goods.getCoverImgUrl();
                    goodsListOV.setUrl(imgUrl);
                    goodsListOV.setTitle(goods.getTitle());

                    goodsListOV.setPrice(goods.getPrice());
                    goodsListOV.setMemberPrice(goods.getMemberPrice());
                    goodsListOV.setPromotionalPrice(goods.getPromotionalPrice());
                    goodsListOV.setPromotionalSalePrice(goods.getPromotionalSalePrice());
                    goodsListOV.setPromotionStartDate(goods.getPromotionStartDate());
                    goodsListOV.setPromotionEndDate(goods.getPromotionEndDate());

                    goodsListOVS.add(goodsListOV);
                }
            }
        }else {
            goodsListOVS = null;
        }
        return goodsListOVS;
    }
    /**
     * 封装查询数据给app端
     * @return
     */
    private List<GoodsListOV> convertSearchGoodsToGoodsListOV(List<SearchGoods> searchGoods,String keyword){
        List<GoodsListOV> goodsListOVS = new ArrayList<GoodsListOV>();
        if(!CollectionUtils.isEmpty(searchGoods)){
            for(SearchGoods goods:searchGoods){
                GoodsListOV goodsListOV = new GoodsListOV();
                if(goods != null){
                    goodsListOV.setId(Integer.parseInt(goods.getId()));
                    //价格分
                    goodsListOV.setNormalPrice(goods.getNormalPrice());
                    if(GoodsType.PROMOTION.getValue().equalsIgnoreCase(goods.getType()) && goods.getPromotionPrice() != null){
                        goodsListOV.setPromotionPrice(goods.getPromotionPrice());
                    }
                    String imgUrl
                            = StringUtils.isEmpty(goods.getCoverImgUrl())?goods.getDetailImgUrl():goods.getCoverImgUrl();
                    goodsListOV.setUrl(imgUrl);
                    goodsListOV.setTitle(goods.getTitle());
                    //新添加字段
                    goodsListOV.setPrice(goods.getPrice());
                    goodsListOV.setMemberPrice(goods.getMemberPrice());
                    goodsListOV.setPromotionalPrice(goods.getPromotionalPrice());
                    goodsListOV.setPromotionalSalePrice(goods.getPromotionalSalePrice());
                    goodsListOV.setPromotionStartDate(goods.getPromotionStartDate());
                    goodsListOV.setPromotionEndDate(goods.getPromotionEndDate());
                    
                }
                goodsListOVS.add(goodsListOV);
            }
            goodsListOVS.sort(new Comparator<GoodsListOV>() {
                @Override
                public int compare(GoodsListOV o1, GoodsListOV o2) {
                    if(o1.getTitle().contains(keyword) && !o2.getTitle().contains(keyword)){
                        return -1;
                    }else if(!o1.getTitle().contains(keyword) && o2.getTitle().contains(keyword)){
                        return 1;
                    }
                    return 0;
                }
            });
        }
        return goodsListOVS;
    }

    /**
     *  随机生成指定数量的下标值
     * @param minIndex 随机数起始值
     * @param maxIndex 随机数结束值
     * @param size 生成随机数个数
     * @return
     */
    private List<Integer> generateRandomGoodsIdArray(int minIndex,int maxIndex,int size){
        List<Integer> indexList = new ArrayList<Integer>();
        //随机生成三个不重复的index
        while (true){
            //随机生成0~maxIndex - 1 之间随机整数
            int randomId = RandomUtils.nextInt(minIndex,maxIndex);
            if(indexList.size() == 0){
                indexList.add(randomId);
            }else{
                //控制index数量
                if(indexList.size() == size){
                    break;
                }else{
                    //判断index是否已存在
                    if(!indexList.contains(randomId)){
                        indexList.add(randomId);
                    }
                }
            }
        }
        return indexList;
    }
    @SuppressWarnings({"ResultOfMethodCallIgnored", "StringConcatenationInsideStringBufferAppend"})
    private String getRelGoods(List<SearchGoods> goodsList){
        StringBuilder relGoodsBuilder = new StringBuilder();
        if(goodsList.size() > 0){
            for(int i=0;i<goodsList.size();i++){
                if(i != goodsList.size() - 1){
                    relGoodsBuilder.append(goodsList.get(i).getId()+",");
                }else{
                    relGoodsBuilder.append(goodsList.get(i).getId());
                }
            }
        }
        return relGoodsBuilder.toString();
    }

    /**
     * 获取根据商品id获取商品信息
     * @param productId
     * @return
     */
    @ApiOperation(value = "获取根据商品id获取商品信息")
    @RequestMapping(value="/findGoodsById",method = {RequestMethod.POST})
    private HttpResult findGoodsById(Integer productId){
        Goods goods = goodsService.findOne(productId);
        return  HttpResult.createSuccess(goods);
    }

    /**
     * 商品转化为搜索引擎商品类
     * @param goods
     * @param serarchGoods
     * @return
     */
    private SearchGoods GoodsToSearchGoods(Goods goods,SearchGoods serarchGoods){
    	
    	BeanUtils.copyProperties(goods, serarchGoods);
    	
    	if(!StringUtils.isEmpty(goods.getBarcode())){
    		serarchGoods.setBarcode(goods.getBarcode());
    	}
    	if(null != goods.getCategory()){
    		serarchGoods.setCatId(goods.getCategory().getId());
    	}
    	if(!StringUtils.isEmpty(goods.getCoverImgUrl())){
    		serarchGoods.setCoverImgUrl(goods.getCoverImgUrl());
    	}
    	if(null != goods.getCreatedTime()){
    		serarchGoods.setCreatedTime(goods.getCreatedTime());
    	}
    	if(!StringUtils.isEmpty(goods.getDescr())){
    		serarchGoods.setDescr(goods.getDescr());
    	}
    	if(!StringUtils.isEmpty(goods.getDetailImgUrl())){
    		serarchGoods.setDetailImgUrl(goods.getDetailImgUrl());
    	}
    	if(null != goods.getId() && goods.getId()>0){
    		serarchGoods.setId(goods.getId().toString());
    	}
    	if(!StringUtils.isEmpty(goods.getLocation())){
    		serarchGoods.setLocation(goods.getLocation());
    	}
    	if(null != goods.getModifiedTime()){
    		serarchGoods.setModifiedTime(goods.getModifiedTime());
    	}
    	if(!StringUtils.isEmpty(goods.getNewRecommend())){
    		serarchGoods.setNewRecommend(goods.getNewRecommend());
    	}
    	if(null != goods.getNormalPrice() && goods.getNormalPrice() >0){
    		serarchGoods.setNormalPrice(goods.getNormalPrice());
    	}
    	if(null != goods.getPromotionPrice() && goods.getPromotionPrice() >0){
    		serarchGoods.setPromotionPrice(goods.getPromotionPrice());
    	}
    	if(!StringUtils.isEmpty(goods.getRemark())){
    		serarchGoods.setRemark(goods.getRemark());
    	}
    	if(!StringUtils.isEmpty(goods.getSeoTag())){
    		serarchGoods.setSeoTag(goods.getSeoTag());
    	}
    	if(null != goods.getShop()){
    		serarchGoods.setShopId(goods.getShop().getId());
    	}
    	if(!StringUtils.isEmpty(goods.getSid())){
    		serarchGoods.setSid(goods.getSid());
    	}
    	if(null!= goods.getSortNo() && goods.getSortNo()>0){
    		serarchGoods.setSortNo(goods.getSortNo());
    	}
    	if(!StringUtils.isEmpty(goods.getTitle())){
    		serarchGoods.setTitle(goods.getTitle());
    	}
    	
    	if(!StringUtils.isEmpty(goods.getType())){
    		serarchGoods.setType(goods.getType());
    	}
    	
		return serarchGoods;
    }
}
