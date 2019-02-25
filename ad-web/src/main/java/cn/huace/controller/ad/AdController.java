package cn.huace.controller.ad;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.huace.ad.entity.Ad;
import cn.huace.ad.entity.AdRelation;
import cn.huace.ad.service.AdService;
import cn.huace.ad.util.AdCodeConstants;
import cn.huace.common.bean.HttpResult;
import cn.huace.common.utils.Contants;
import cn.huace.common.utils.PostObjectToOss;
import cn.huace.controller.base.AdminBasicController;
import cn.huace.goods.service.GoodsService;
import cn.huace.shop.shop.entity.Shop;
import cn.huace.shop.shop.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhouyanbin
 * @date 2017年5月3日 下午12:23:56
 * @version 1.0
 */

@Slf4j
@RestController
@Api(value = "/admin/ad", description = "广告管理")
@RequestMapping(value = "/admin/ad")
public class AdController extends AdminBasicController {
	@Autowired
	private AdService adService;
	@Autowired
    private ShopService shopService;
	@Autowired
	private GoodsService goodsService;
	@RequestMapping(value="/list",method = RequestMethod.POST)
	public HttpResult list(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10") Integer rows, 
			String name, String type,String status,String validFlag,String shopId) {
		Map<String, Object> searchParams = new HashedMap();
		if (!StringUtils.isBlank(name)) {
			searchParams.put("LIKE_name", name);
		}
		if (!StringUtils.isBlank(type) && !"0".equals(type)) {
			searchParams.put("LIKE_type", type);
		}
		if (!StringUtils.isBlank(status)) {
			searchParams.put("EQ_status", status);
		}
		if(!StringUtils.isBlank(validFlag) && !"-1".equals(validFlag)){
			searchParams.put("EQ_validFlag", validFlag);
		}
        if(!StringUtils.isBlank(shopId)){
        	searchParams.put("EQ_shopId",shopId);
        }else{
        	List<Shop> shopList = shopService.findAll();
        	searchParams.put("EQ_shopId",shopList.get(0).getId());
        }
		Page<Ad> pageResult = adService.findAll(searchParams, page, rows);
		return HttpResult.createSuccess(pageResult);
	}
	
	@ApiOperation(value = "保存广告",notes = "保存广告")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public HttpResult save(@RequestParam(value = "adpath",required = false) MultipartFile adpath,Ad ad){
		if(ad.getId() == null){
			ad.setValidFlag(AdCodeConstants.VALID);
			String image = null;
			if(adpath != null && !adpath.isEmpty()){
				image = PostObjectToOss.postFile(adpath, Contants.IMG_FOLDER_AD);
			}
			String md5 = null;
			try {
				md5 = DigestUtils.md5DigestAsHex(adpath.getInputStream());
			} catch (IOException e) {
				log.error("加密失败"+e);
			}
			ad.setPath(image);
			ad.setMd5(md5);
			ad.setCreatedTime(new Date());
			ad.setModifiedTime(new Date());
			adService.save(ad);
			log.info("广告新增成功，广告id:" + ad.getId());
		}else{
			Ad ad_old = adService.findOne(ad.getId());
			ad.setModifiedTime(new Date());
			if(adpath != null && !adpath.isEmpty()){
				String image = PostObjectToOss.postFile(adpath, Contants.IMG_FOLDER_AD);
				ad.setPath(image);
				try {
					ad.setMd5(DigestUtils.md5DigestAsHex(adpath.getInputStream()));
				} catch (IOException e) {
					log.error("加密失败"+e);
				}
			}else{
				ad.setPath(ad_old.getPath());
				ad.setMd5(ad_old.getMd5());
			}
			ad.setShopId(ad_old.getShopId());
			adService.save(ad);
			log.info("广告修改成功，广告id:" + ad.getId());
		}
		return HttpResult.createSuccess("保存成功！");
	}
	
	@ApiOperation(value = "删除广告",notes = "删除广告")
	@RequestMapping(value = "delete", method = RequestMethod.GET)
	public HttpResult delete(Integer id){
		adService.delete(id);
		log.info("广告删除成功，id:" + id);
		return HttpResult.createSuccess("广告删除成功！");
	}
	
	@ApiOperation(value = "根据ID查询广告",notes = "根据ID查询广告")
	@RequestMapping(value = "findAdById", method = RequestMethod.GET)
	public HttpResult findAdById(Integer id){
		Ad ad = adService.findOne(id);
		return HttpResult.createSuccess(ad);
	}
}
