package cn.huace.controller.sys;


import cn.huace.common.bean.HttpResult;
import cn.huace.controller.base.AdminBasicController;
import cn.huace.controller.bean.TreeBean;
import cn.huace.sys.bean.ShiroUser;
import cn.huace.sys.entity.SystemMenu;
import cn.huace.sys.entity.SystemUser;
import cn.huace.sys.service.SystemDicService;
import cn.huace.sys.service.SystemMenuService;
import cn.huace.sys.service.SystemRoleService;
import cn.huace.sys.service.SystemUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Api(value = "/admin",description = "主页")
@RequestMapping(value = "/admin")
public class HomeController extends AdminBasicController {

	@Autowired
	protected SystemRoleService systemRoleService;

	@Autowired
	protected SystemMenuService systemMenuService;

	@Autowired
	protected SystemUserService systemUserService;

	@Autowired
	protected SystemDicService systemDicService;

	@ApiOperation(value = "个人菜单")
	@RequestMapping(value = "listUserMenu",method= RequestMethod.GET)
	public HttpResult listUserMenu(){
		ShiroUser shiroUser=getCurrentUser();
		Integer userId=shiroUser.getId();
		List<SystemMenu> systemMenuList= systemUserService.getMenuTreeByUserId(userId);
		return HttpResult.createSuccess(systemMenuList);
	}
	@ApiOperation(value = "个人信息")
	@RequestMapping(value = "userInfo",method= RequestMethod.GET)
	public HttpResult userInfo(){
		ShiroUser shiroUser=getCurrentUser();
		Integer userId=shiroUser.getId();
		SystemUser user=systemUserService.findOne(userId);
		return HttpResult.createSuccess(user);
	}
	@ApiOperation(value = "数据字典")
	@RequestMapping(value = "findSystemDic",method= RequestMethod.GET)
	public HttpResult findSystemDic(Integer type){
		return HttpResult.createSuccess(systemDicService.findDicByType(type));
	}
	@ApiOperation(value = "菜单树")
	@RequestMapping(value = "listAllMenu",method= RequestMethod.GET)
	public HttpResult listAllMenu(){
		List<SystemMenu> systemMenuList= systemUserService.getMenuTree();
		List<TreeBean> tree=new ArrayList<TreeBean>();
		for(SystemMenu systemMenu:systemMenuList){
			TreeBean treeBean= TreeBean.passSystemMenu(systemMenu);
			tree.add(treeBean);
			if(systemMenu.getChildMenus()!=null){
				treeBean.setChildren(new ArrayList<TreeBean>());
				for(SystemMenu child:systemMenu.getChildMenus()){
					TreeBean childBean= TreeBean.passSystemMenu(child);
					treeBean.getChildren().add(childBean);
				}
			}
		}
		return HttpResult.createSuccess(tree);
	}

}
