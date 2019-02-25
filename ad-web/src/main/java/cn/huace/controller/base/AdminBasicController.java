package cn.huace.controller.base;


import cn.huace.common.controller.DateEditor;
import cn.huace.sys.bean.ShiroUser;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.util.Date;

/**
 * <功能详细描述>
 *
 */
public abstract class AdminBasicController
{

    /**
     * 取出Shiro中的当前用户.
     */
    public ShiroUser getCurrentUser()
    {
        ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
        return user;
    }
    @InitBinder
    public void initBinder(ServletRequestDataBinder binder){
        binder.registerCustomEditor(Date.class, new DateEditor());
    }

}
