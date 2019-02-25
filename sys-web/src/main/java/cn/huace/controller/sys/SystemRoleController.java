package cn.huace.controller.sys;


import cn.huace.common.bean.HttpResult;
import cn.huace.controller.base.AdminBasicController;
import cn.huace.sys.entity.SystemMenu;
import cn.huace.sys.entity.SystemRole;
import cn.huace.sys.service.SystemMenuService;
import cn.huace.sys.service.SystemRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.ServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * <角色信息管理>
 * 
 * @author 陆小凤
 * @version [1.0, 2015年9月11日]
 */
@Slf4j
@RestController
@Api(value = "/admin/role",description = "系统管理")
@RequestMapping(value = "/admin/role")
public class SystemRoleController extends AdminBasicController
{
    @Autowired
    private SystemRoleService systemRoleService;

    @Autowired
    private SystemMenuService systemMenuService;

    /**
     * <查询角色信息>
     *
     * @param request
     * @return
     */
    @RequestMapping(method=RequestMethod.GET)
    public HttpResult roleList(@RequestParam(defaultValue="1") Integer page, @RequestParam(defaultValue="10") Integer rows, ServletRequest request)
    {
        Map<String, Object> searchParams = new HashedMap();
        Page<SystemRole> pageResult=systemRoleService.findAll(searchParams,page,rows);
        return HttpResult.createSuccess(pageResult);
    }

    @RequestMapping(value="save",method =RequestMethod.POST)
    @ApiOperation(value = "保存角色", notes = "保存角色", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID", paramType = "form",  dataType = "int"),
            @ApiImplicitParam(name = "name", value = "名称", paramType = "form", required = true, dataType = "string"),
            @ApiImplicitParam(name = "description", value = "描述", paramType = "form", dataType = "string"),
            @ApiImplicitParam(name = "menuIds", value = "菜单ids", paramType = "form", dataType = "string")
    })
    public HttpResult save(@ApiIgnore SystemRole entity){
        if(!entity.isNew()){
            SystemRole oldRole=systemRoleService.findOne(entity.getId());
            oldRole.setName(entity.getName());
            oldRole.setUseFlag(entity.getUseFlag());
            oldRole.setRemark(entity.getRemark());
            systemRoleService.save(oldRole);
        }else{
            systemRoleService.save(entity);
        }
       return HttpResult.createSuccess("保存成功！");
    }
    @ApiOperation(value = "保存角色菜单", notes = "保存角色菜单", httpMethod = "POST")
    @RequestMapping(value="saveRoleMenu",method=RequestMethod.POST)
    public HttpResult saveRoleMenu(Integer id,Integer[] menuIds){
        SystemRole entity =systemRoleService.findOne(id);
        List<SystemMenu>menus= systemMenuService.findAll(Arrays.asList(menuIds));
        entity.setMenuSet(new HashSet<SystemMenu>(menus));
        systemRoleService.save(entity);
        return HttpResult.createSuccess("保存成功！");
    }
    @ApiOperation(value = "删除角色")
    @RequestMapping(value="del",method=RequestMethod.POST)
    public HttpResult del(Integer id){
        systemRoleService.delete(id);
        return HttpResult.createSuccess("删除成功！");
    }
    @ApiOperation(value = "获取角色菜单", notes = "获取角色菜单")
    @RequestMapping(value="findMenuByRoleId",method=RequestMethod.GET)
    public HttpResult findMenuByRoleId(Integer id){
        SystemRole entity =systemRoleService.findOne(id);
        return HttpResult.createSuccess(entity.getMenuSet());
    }

    @ApiOperation(value = "获取角色", notes = "获取角色")
    @RequestMapping(value="find",method=RequestMethod.GET)
    public HttpResult find(Integer id){
        SystemRole entity =systemRoleService.findOne(id);
        return HttpResult.createSuccess(entity);
    }
}
