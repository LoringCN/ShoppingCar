package cn.huace.controller.ad;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.huace.ad.entity.AdRelation;
import cn.huace.ad.service.AdRelationService;
import cn.huace.ad.util.AdCodeConstants;
import cn.huace.common.bean.HttpResult;
import cn.huace.common.bean.TreeBean;
import cn.huace.controller.base.AdminBasicController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(value = "/admin/relation", description = "关联关系管理")
@RequestMapping(value = "/admin/relation")
public class AdRelationController extends AdminBasicController{
	@Autowired
	private AdRelationService adRelationService;
	
	@ApiOperation(value = "获取所有的关联关系",notes = "获取所有的关联关系")
	@RequestMapping(value="/list",method = RequestMethod.POST)
	public HttpResult list(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10") Integer rows,
			String name,String validFlag) {
		Map<String, Object> searchParams = new HashedMap();
		if (!StringUtils.isBlank(name)) {
			searchParams.put("LIKE_name", name);
		}
		
		if (!StringUtils.isBlank(validFlag)) {
			searchParams.put("LIKE_validFlag", validFlag);
		}else{
			searchParams.put("LIKE_validFlag", AdCodeConstants.VALID);
		}
		Page<AdRelation> pageResult = adRelationService.findAll(searchParams, page, rows);
		return HttpResult.createSuccess(pageResult);
	}
	
	@ApiOperation(value = "保存关联关系",notes = "保存关联关系")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public HttpResult save(AdRelation adRelation){
		if(adRelation.getId() == null){
			adRelation.setValidFlag(AdCodeConstants.VALID);
			adRelation.setCreatedTime(new Date());
			adRelation.setModifiedTime(new Date());
			adRelationService.save(adRelation);
			log.info("关联关系新增成功，id :" + adRelation.getId());
		}else{
			AdRelation relation = adRelationService.findOne(adRelation.getId());
			relation.setName(adRelation.getName());
			relation.setModifiedTime(new Date());
			adRelationService.save(relation);
			log.info("关联关系修改成功，id:" + adRelation.getId());
		}
		return HttpResult.createSuccess("保存成功！");
	}
	
	@ApiOperation(value = "删除关联关系",notes = "删除关联关系")
	@RequestMapping(value = "delete", method = RequestMethod.GET)
	public HttpResult delete(Integer id){
		adRelationService.delete(id);
		log.info("关联关系删除成功，id:" + id);
		return HttpResult.createSuccess("删除成功！");
	}
	
	@ApiOperation(value = "根据主键查询关联关系",notes = "根据主键查询关联关系")
	@RequestMapping(value = "findRelationById", method = RequestMethod.GET)
	public HttpResult findRelationById(Integer id){
		AdRelation adRelation = adRelationService.findOne(id);
		return HttpResult.createSuccess(adRelation);
	}
	
	@ApiOperation(value = "获取关联关系树", notes = "获取关联关系树")
	@RequestMapping(value = "findAdRelationTree",method = RequestMethod.GET)
	public HttpResult findAdRelationTree() {
		List<AdRelation> adRelationList = adRelationService.findAll();
		List<TreeBean> treeBeanList = new ArrayList<TreeBean>();
		if(adRelationList != null){
			for (AdRelation adRelation : adRelationList) {
				TreeBean treeBean = new TreeBean();
				treeBean.setId(adRelation.getId());
				treeBean.setText(adRelation.getName());
				treeBeanList.add(treeBean);
			}
		}
		return HttpResult.createSuccess(treeBeanList);
	}
}
