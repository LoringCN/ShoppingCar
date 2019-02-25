package cn.huace.controller.goods;

import cn.huace.common.bean.HttpResult;
import cn.huace.controller.base.BaseFrontController;
import cn.huace.entity.GoodsRenewalVo;
import cn.huace.goods.entity.Goods;
import cn.huace.goods.entity.GoodsLocation;
import cn.huace.goods.entity.GoodsPictrue;
import cn.huace.goods.entity.GoodsRenewal;
import cn.huace.goods.service.*;
import cn.huace.shop.shop.entity.Shop;
import cn.huace.shop.shop.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@Api(value = "/goodsRenewal", description = "商品更新")
@RequestMapping("/goodsRenewal")
public class GoodsRenewalController extends BaseFrontController {

    @Autowired
    private GoodsRenewalService goodsRenewalService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private GoodsCategoryService goodsCategoryService;

    @Autowired
    private GoodsLocationService goodsLocationService;

    @Autowired
    private GoodsPictrueService goodsPictrueService;

    @ApiOperation(value = "商品更新")
    @RequestMapping(value = "/postData", method = {RequestMethod.POST})
    private HttpResult postGoodsRenewal(HttpServletRequest request) {

        // 读取请求内容
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            //商品数据
            String data = sb.toString();
            //数据存文件
            FileOutputStream fos = new FileOutputStream("/goodsRenewal/data.txt",true);
            //true表示在文件末尾追加
            fos.write(data.getBytes());
            fos.write("\n".getBytes());
            fos.close();

            JSONArray jsonArray = JSONArray.fromObject(data);

            //Java集合
            List<GoodsRenewalVo> list = (List<GoodsRenewalVo>) jsonArray.toCollection(jsonArray, GoodsRenewalVo.class);
            List <GoodsRenewal> listSave = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                /***
                 * 1、先判断商品是否存在，如不存在直接保存接口数据，商品表不做任何修改。
                 * 2、根据指令做分支，del : 下线 下架 商品 置为无效状态。
                 */
                GoodsRenewalVo goodsRenewalVo =  list.get(i);
                GoodsRenewal goodsRenewal =  new GoodsRenewal();
                BeanUtils.copyProperties(goodsRenewal,goodsRenewalVo);

                Integer shopId = null;
                //00112 -- 深圳沙井天虹
                //02402 -- 天虹北京新奥
                if(goodsRenewalVo.getShopId().equals("00112")){
                    shopId = 15;
                }else {
                    shopId = shopService.findShopByShopName("天虹北京新奥").getId();
                }
                //商品
                Goods goods = goodsService.findByBarcodeAndShopId(goodsRenewalVo.getBarcode(),shopId);
                //图片库
                GoodsPictrue goodsPictrue = goodsPictrueService.findByBarcode(goodsRenewal.getBarcode());
                //位置信息
                GoodsLocation goodsLocation = goodsLocationService.findByBarCode(goodsRenewal.getBarcode());

                if(goods != null && goodsRenewalVo.getAction().equals("del")){
                    //下架商品
                    goods.setFlag("-1");
                    goodsService.save(goods);
                }else {
                    goodsService.save(convertGoods(goods,goodsRenewal,goodsPictrue,goodsLocation));
                }
                goodsRenewal.setStatus(1);
                listSave.add(goodsRenewal);

            }
            goodsRenewalService.batchInsert(listSave);

        } catch (Exception e) {
            log.error("商品数据更新接口报错：" + e.getMessage());
            return HttpResult.createFAIL("err");
        }

        return HttpResult.createSuccess("ok");
    }

    @ApiOperation(value = "商品位置信息校验")
    @RequestMapping(value = "/inLocation", method = {RequestMethod.POST})
    private HttpResult inLocation(HttpServletRequest request) {

        // 读取请求内容
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            //商品数据
            String data = sb.toString();
            JSONArray jsonArray = JSONArray.fromObject(data);
            //Java集合
            List<GoodsLocation> list = (List<GoodsLocation>) jsonArray.toCollection(jsonArray, GoodsLocation.class);
            Integer successNum = goodsLocationService.batchUpdate(list);
            //商店暂时固定 北京新奥
            Integer shopId = shopService.findShopByShopName("天虹北京新奥").getId();
            String  oldShopId = "02402";
            //更新至商品表
            for (GoodsLocation goodsLocation: list) {
                    //Goods
                    Goods goods = goodsService.findByBarcodeAndShopId(goodsLocation.getProductId(),shopId);
                    if(goods == null ){
                        goods = new Goods();
                    }
                    //GoodsRenewal
                    GoodsRenewal goodsRenewal = goodsRenewalService.findByBarcodeAndShopId(goodsLocation.getProductId(),oldShopId);
                    if(goodsRenewal == null ){
                        continue;
                    }
                    //GoodsPictrue
                    GoodsPictrue goodsPictrue = goodsPictrueService.findByBarcode(goodsLocation.getProductId());
                    if(goodsRenewal.getAction().equals("del")){
                        //下架商品
                        goods.setFlag("-1");
                        goodsService.save(goods);
                    }else{
                        goods.setFlag("1");
                        Goods goods1 = goodsService.save(convertGoods(goods,goodsRenewal,goodsPictrue,goodsLocation));
//                        System.out.println(goods1.getBarcode());
                    }

                    goodsRenewal.setStatus(1);
                    goodsRenewalService.save(goodsRenewal);

            }
            return HttpResult.createSuccess("商品位置信息导入成功("+successNum+")条");
        } catch (Exception e) {
            log.error("商品位置信息接口异常：",e);
            return HttpResult.createFAIL("商品位置信息导入失败"+e.getMessage());
        }


    }

    private Goods convertGoods(Goods goods ,GoodsRenewal goodsRenewal, GoodsPictrue goodsPictrue, GoodsLocation goodsLocation){
        try {
            if(goods == null){
                goods = new Goods();
            }
            /*基本信息goodsRenewal*/
            //00112 -- 深圳沙井天虹
            //02402 -- 天虹北京新奥
            Integer shopId = null;
            if(goodsRenewal.getShopId().equals("00112")){
                shopId = 15;
                goods.setShop(shopService.findOne(shopId));
            }else {
                Shop shop = shopService.findShopByShopName("天虹北京新奥");
                shopId = shop.getId();
                goods.setShop(shop);
            }
            if(goods.getBarcode() == null){
                goods.setBarcode(goodsRenewal.getBarcode());
            }

            //更新商品
            //价格
            //标价
            Double price = goodsRenewal.getPrice()*100;
            goods.setPrice(price);
            goods.setNormalPrice(price.intValue());
            //会员价
            goods.setMemberPrice(goodsRenewal.getMemberPrice()*100);
            //促销原价
            goods.setPromotionalPrice(goodsRenewal.getPromotionalPrice()*100);
            //促销价
            goods.setPromotionalSalePrice(goodsRenewal.getPromotionalSalePrice()*100);
            SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            if(StringUtils.isNotBlank(goodsRenewal.getPromotionStartDate())){
                goods.setPromotionStartDate(sDateFormat.parse(goodsRenewal.getPromotionStartDate()));
            }
            if(StringUtils.isNotBlank(goodsRenewal.getPromotionEndDate())){
                goods.setPromotionEndDate(sDateFormat.parse(goodsRenewal.getPromotionEndDate()));
            }

            //商品名
            goods.setTitle(goodsRenewal.getTitle());
            //商品分类
            /* 生鲜熟食 880201,880202,880203,880205,880206,880207,880208,880301, 880302,880303,880304,990231,990307*/
            /*粮油副食	880209,880401,880402,880405,880416,880601,990215*/
            /*低温日配	880701,880702*/
            /*休闲食品	880307,880406,880409,880410,880411,880603,990223,990225*/
            /*酒水饮料	880407,880408,880602,990218,990220*/
            /*冲饮保健	880403,880414,990203*/
            /*婴幼宠	880412,880413,880415,990217*/
            /*个人护理	880501,880502,880503,880504,880505,880506,880507,880509,881001,880525*/
            /*家居厨房	880510,880511,880512,880513,880514,880518,880519,880521,880522,880523,880524,990104,990211*/
            /*其他	    0,880111,880299,880417,881603,881605*/
            /*(物料)不显示	881701,990216*/
            if("880201,880202,880203,880205,880206,880207,880208,880301, 880302,880303,880304,990231,990307".contains(goodsRenewal.getSubCategoryId())){
                goods.setCategory(goodsCategoryService.findByCatName("生鲜熟食",shopId));
            }else if ("880209,880401,880402,880405,880416,880601,990215".contains(goodsRenewal.getSubCategoryId())){
                goods.setCategory(goodsCategoryService.findByCatName("粮油副食",shopId));
            }else if ("880701,880702".contains(goodsRenewal.getSubCategoryId())){
                goods.setCategory(goodsCategoryService.findByCatName("低温日配",shopId));
            }else if ("880307,880406,880409,880410,880411,880603,990223,990225".contains(goodsRenewal.getSubCategoryId())){
                goods.setCategory(goodsCategoryService.findByCatName("休闲食品",shopId));
            }else if("880407,880408,880602,990218,990220".contains(goodsRenewal.getSubCategoryId())){
                goods.setCategory(goodsCategoryService.findByCatName("酒水饮料",shopId));
            }else if("880403,880414,990203".contains(goodsRenewal.getSubCategoryId())){
                goods.setCategory(goodsCategoryService.findByCatName("冲饮保健",shopId));
            }else if("880412,880413,880415,990217".contains(goodsRenewal.getSubCategoryId())){
                goods.setCategory(goodsCategoryService.findByCatName("婴幼宠",shopId));
            }else if("880501,880502,880503,880504,880505,880506,880507,880509,881001,880525".contains(goodsRenewal.getSubCategoryId())){
                goods.setCategory(goodsCategoryService.findByCatName("个人护理",shopId));
            }else if("880510,880511,880512,880513,880514,880518,880519,880521,880522,880523,880524,990104,990211".contains(goodsRenewal.getSubCategoryId())){
                goods.setCategory(goodsCategoryService.findByCatName("家居厨房",shopId));
    //                        }else if("0,880111,880299,880417,881603,881605".contains(goodsRenewal.getSubCategoryId())){
    //                            goods.setCategory(goodsCategoryService.findByCatName("其他",shopId));
            }else if("881701,990216".contains(goodsRenewal.getSubCategoryId())){
                goods.setCategory(null);//(物料)不显示
            }else {
                goods.setCategory(goodsCategoryService.findByCatName("其他",shopId));
            }

            //品牌
            goods.setBrandName(goodsRenewal.getBrandName());
            //备注
            String remark = "规格:"+ goodsRenewal.getSpecification() +
                    "\n品牌:"+ goodsRenewal.getBrandName() +
                    "\n产地:"+ goodsRenewal.getOrigin() +
                    "\n单位:"+ goodsRenewal.getUnitName() +
                    "\n详情:"+ (StringUtils.isBlank(goodsRenewal.getDescription())?"见商品详情":goodsRenewal.getDescription());
            goods.setDescr(remark);
            goods.setStock(goodsRenewal.getStock());

            goods.setFlag("1");

            /*图片信息goodsPictrue*/
            if(goodsPictrue != null){
                goods.setCoverImgUrl(goodsPictrue.getUrl());
                goods.setDetailImgUrl(goodsPictrue.getUrl());
            }

            /*位置信息goodsLocation*/
            if(goodsLocation != null){
                goods.setSid(goodsLocation.getShelfId());
            }

            return goods;
        } catch (Exception e) {
            log.error("商品位置信息接口异常："+e.getMessage());
            return goods;
        }
    }

}
