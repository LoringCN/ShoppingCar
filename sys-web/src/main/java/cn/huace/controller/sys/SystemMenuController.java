package cn.huace.controller.sys;


import cn.huace.common.bean.HttpResult;
import cn.huace.controller.base.AdminBasicController;
import cn.huace.sys.entity.SystemMenu;
import cn.huace.sys.service.SystemMenuService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangdan on 2016/12/27.
 */
@Slf4j
@RestController
@Api(value = "/admin/menu",description = "菜单管理")
@RequestMapping(value = "/admin/menu")
public class SystemMenuController extends AdminBasicController
{
    
    @Autowired
    private SystemMenuService systemMenuService;

    @RequestMapping(method=RequestMethod.GET)
    public HttpResult list()
    {
        List<SystemMenu> menuList=systemMenuService.findAll();
        List<SystemMenu> resultList=new ArrayList<SystemMenu>();
        List<SystemMenu> realResultList=new ArrayList<SystemMenu>();
        for(SystemMenu mu:menuList){
            if(mu.getParentId() ==null){
                mu.setChildMenus(new ArrayList<SystemMenu>());
                for(SystemMenu me :menuList){
                    if(me.getParentId() != null && me.getParentId().equals(mu.getId())){
                        mu.getChildMenus().add(me);
                    }
                }
                resultList.add(mu);
            }
        }

        for(SystemMenu weiXinMenu:resultList){
            realResultList.add(weiXinMenu);
            realResultList.addAll(weiXinMenu.getChildMenus());
        }
        return HttpResult.createSuccess(realResultList);
    }

    @RequestMapping(value="save",method=RequestMethod.POST)
    public HttpResult save(SystemMenu entity){
        if(!entity.isNew()){
            SystemMenu old= systemMenuService.findOne(entity.getId());
            entity.setCreatedTime(old.getCreatedTime());
        }
        systemMenuService.save(entity);
        return HttpResult.createSuccess("保存成功！");
    }

    @RequestMapping(value="del",method=RequestMethod.POST)
    public HttpResult del(Integer id){
        systemMenuService.delete(id);
        return HttpResult.createSuccess("删除成功！");
    }

    @RequestMapping(value="listParent",method=RequestMethod.GET)
    public HttpResult listParent()
    {
        List<SystemMenu> menuList=systemMenuService.findAll();
        List<SystemMenu> resultList=new ArrayList<SystemMenu>();
        for(SystemMenu mu:menuList){
                if(mu.getParentId()==null)
                resultList.add(mu);
            }
        return HttpResult.createSuccess(resultList);
    }

    @RequestMapping(value="find",method=RequestMethod.GET)
    public HttpResult find(@RequestParam Integer id){
        SystemMenu entity =systemMenuService.findOne(id);
        return HttpResult.createSuccess(entity);
    }
}
