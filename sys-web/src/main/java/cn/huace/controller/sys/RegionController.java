package cn.huace.controller.sys;

import cn.huace.common.bean.HttpResult;
import cn.huace.controller.base.AdminBasicController;
import cn.huace.sys.entity.RegionArea;
import cn.huace.sys.entity.RegionCity;
import cn.huace.sys.entity.RegionProvince;
import cn.huace.sys.service.RegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(value = "/admin/region",description = "区域")
@RequestMapping(value = "/admin/region")
public class RegionController extends AdminBasicController {

	@Autowired
	private RegionService regionService;

	@ApiOperation(value = "获取省份")
	@RequestMapping(value = "listProvince",method= RequestMethod.GET)
	public HttpResult listProvince(){
		List<RegionProvince>list=regionService.listProvince();
		return HttpResult.createSuccess(list);
	}
	@ApiOperation(value = "获取城市")
	@RequestMapping(value = "listCity",method= RequestMethod.GET)
	public HttpResult listCity(Integer provinceId){
		List<RegionCity>list=regionService.listCity(provinceId);
		return HttpResult.createSuccess(list);
	}
	@ApiOperation(value = "获取区域")
	@RequestMapping(value = "listArea",method= RequestMethod.GET)
	public HttpResult listArea(Integer cityId){
		List<RegionArea>list=regionService.listArea(cityId);
		return HttpResult.createSuccess(list);
	}
}
