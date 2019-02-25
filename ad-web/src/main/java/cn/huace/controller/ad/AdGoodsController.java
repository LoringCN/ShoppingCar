package cn.huace.controller.ad;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.huace.ad.entity.AdGoods;
import cn.huace.ad.service.AdGoodsService;
import cn.huace.ad.util.AdCodeConstants;
import cn.huace.common.bean.HttpResult;
import cn.huace.controller.base.AdminBasicController;
import cn.huace.goods.entity.Goods;
import cn.huace.goods.service.GoodsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * 广告管理商品
 * @author zhouyanbin
 */
@Slf4j
@RestController
@Api(value = "/admin/adGoods", description = "广告管理")
@RequestMapping(value = "/admin/adGoods")
public class AdGoodsController extends AdminBasicController {
	@Autowired
	private AdGoodsService adGoodsService;
	@Autowired
	private GoodsService goodsService;
	
	@ApiOperation(value = "根据广告ID查询管理商品",notes = "根据广告ID查询管理商品")
	@RequestMapping(value = "findAdGoodsByAdId", method = RequestMethod.POST)
	public HttpResult findAdGoodsByAdId(Integer adId){
		AdGoods adGoods = adGoodsService.findAdGoodsByAdId(adId);
		return HttpResult.createSuccess(adGoods);
	}
	
	@ApiOperation(value = "保存广告超市商品关系数据", notes = "保存广告超市商品关系数据")
	@RequestMapping(value = "save",method = RequestMethod.POST)
	public HttpResult save(Integer shopId,Integer adId,Integer goodsId){
		AdGoods adGoods = adGoodsService.findAdGoodsByAdId(adId);
		if(adGoods == null){
			adGoods = new AdGoods();
			adGoods.setAdId(adId);
			adGoods.setShopId(shopId);
			adGoods.setGoodsId(goodsId);
			adGoods.setCreatedTime(new Date());
			adGoods.setModifiedTime(new Date());
			adGoods.setValidFlag(AdCodeConstants.VALID);
		}else{
			adGoods.setGoodsId(goodsId);
			adGoods.setModifiedTime(new Date());
		}
		adGoodsService.save(adGoods);
		log.info("广告商品管理成功，广告ID:" + adId + ",超市ID:" + shopId + ",商品ID:" + goodsId);
		return HttpResult.createSuccess("保存成功");
	}
	
	@ApiOperation(value = "根据广告ID查询广告关联商品")
	@RequestMapping(value = "/findAllAdGoodsByAdId", method = { RequestMethod.POST })
	public HttpResult findAllAdGoodsByAdId(@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "8") Integer rows,
			@RequestParam(name = "adId", required = false) Integer adId) {
		log.info("**** 开始调用方法：findAllGoods(),参数={adId:" + adId + " pageNum:" + page + " pageSize:" + rows + "}");
		if (adId == null) {
			return HttpResult.createFAIL("非法接口访问！");
		}
		AdGoods adGoods = adGoodsService.findAdGoodsByAdId(adId);
		Integer goodsId = null;
		if (adGoods != null) {
			goodsId = adGoods.getGoodsId();
			Map<String,Object> searchMap = new HashMap<String,Object>();
			searchMap.put("EQ_id",goodsId);
			Page<Goods> goods = goodsService.findAll(searchMap,page,rows);
			return HttpResult.createSuccess("查询成功！",goods);
		}else{
			return HttpResult.createSuccess("查询成功！",null);

		}
	}
}
