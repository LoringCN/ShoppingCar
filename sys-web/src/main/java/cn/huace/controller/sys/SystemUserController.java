package cn.huace.controller.sys;


import cn.huace.common.bean.HttpResult;
import cn.huace.controller.base.AdminBasicController;
import cn.huace.controller.bean.TreeBean;
import cn.huace.sys.bean.ShiroUser;
import cn.huace.sys.entity.SystemRole;
import cn.huace.sys.entity.SystemUser;
import cn.huace.sys.service.AccountService;
import cn.huace.sys.service.SystemRoleService;
import cn.huace.sys.service.SystemUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequest;
import java.util.*;


/**
 * 管理员管理用户的Controller.
 *
 * @author 陆小凤
 */
@Slf4j
@RestController
@Api(value = "/admin/user", description = "系统用户")
@RequestMapping(value = "/admin/user")
public class SystemUserController extends AdminBasicController {

    @Autowired
    private SystemUserService systemUserService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private SystemRoleService systemRoleService;

    @RequestMapping(method = RequestMethod.GET)
    public HttpResult list(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer rows, String name, String account, ServletRequest request) {
        Map<String, Object> searchParams = new HashedMap();
        if (!StringUtils.isBlank(name)) {
            searchParams.put("LIKE_name", name);
        }
        if (!StringUtils.isBlank(account)) {
            searchParams.put("LIKE_account", account);
        }

        // *** created by Loring  start ***
        ShiroUser user = getCurrentUser();
        SystemUser systemUser = systemUserService.findOne(user.getId());
        if (systemUser.getType() == 0) {
            //超级管理员
        } else if (systemUser.getType() == 1) {
            //普通用户
            searchParams.put("EQ_account", account);
        } else if (systemUser.getType() == 2) {
            //管理员

        }

        // *** created by Loring   end ***

        Page<SystemUser> pageResult = systemUserService.findAll(searchParams, page, rows);
        return HttpResult.createSuccess(pageResult);
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public HttpResult save(SystemUser entity, Integer[] roleIds) {
        if (entity.isNew()) {
            accountService.entryptPassword(entity);
            List<SystemRole> roles = systemRoleService.findAll(Arrays.asList(roleIds));
            entity.setRoleSet(new HashSet<SystemRole>(roles));
            systemUserService.save(entity);
        } else {
            SystemUser old = systemUserService.findOne(entity.getId());
            old.setName(entity.getName());
            old.setPhone(entity.getPhone());
            old.setEmail(entity.getEmail());
            if (!StringUtils.isBlank(entity.getPlainPassword())) {
                old.setPlainPassword(entity.getPlainPassword());
                accountService.entryptPassword(old);
            }
            List<SystemRole> roles = systemRoleService.findAll(Arrays.asList(roleIds));
            old.setRoleSet(new HashSet<SystemRole>(roles));
            old.setUseFlag(entity.getUseFlag());
            old.setRemark(entity.getRemark());
            old.setType(entity.getType());
            old.setShopIds(entity.getShopIds());
            systemUserService.save(old);
        }

        return HttpResult.createSuccess("保存成功！");
    }

    @RequestMapping(value = "del", method = RequestMethod.POST)
    public HttpResult del(Integer id) {
        systemUserService.delete(id);
        return HttpResult.createSuccess("删除成功！");
    }

    @ApiOperation(value = "获取角色树", notes = "获取角色树")
    @RequestMapping(value = "findRoleTree", method = RequestMethod.GET)
    public HttpResult findRoleTree() {
        List<SystemRole> list = systemRoleService.findAll();
        List<TreeBean> tree = new ArrayList<TreeBean>();
        for (SystemRole role : list) {
            TreeBean treeBean = new TreeBean();
            treeBean.setId(role.getId());
            treeBean.setText(role.getName());
            tree.add(treeBean);
        }
        return HttpResult.createSuccess(tree);
    }

    @ApiOperation(value = "获取用户", notes = "获取用户")
    @RequestMapping(value = "find", method = RequestMethod.GET)
    public HttpResult find(@RequestParam Integer id) {
        SystemUser entity = systemUserService.findOne(id);
        return HttpResult.createSuccess(entity);
    }

    @ApiOperation(value = "获取角色Id", notes = "获取角色Id")
    @RequestMapping(value = "findRoleIdByUserId", method = RequestMethod.GET)
    public HttpResult findRoleIdByUserId(Integer id) {
        SystemUser entity = systemUserService.findOne(id);
        Set<SystemRole> set = entity.getRoleSet();
        List<Integer> roleIds = new ArrayList<Integer>();
        if (set != null) {
            for (SystemRole temp : set) {
                roleIds.add(temp.getId());
            }
        }
        return HttpResult.createSuccess(roleIds);
    }

    @ApiOperation(value = "修改用户密码", notes = "修改用户密码")
    @RequestMapping(value = "editPassword", method = RequestMethod.POST)
    public HttpResult editPassword(Integer id, String plainPassword) {
        SystemUser user = systemUserService.findOne(id);
        if (!StringUtils.isBlank(plainPassword)) {
            user.setPlainPassword(plainPassword);
            accountService.entryptPassword(user);
        }
        systemUserService.save(user);
        return HttpResult.createSuccess("修改密码成功");
    }
}
