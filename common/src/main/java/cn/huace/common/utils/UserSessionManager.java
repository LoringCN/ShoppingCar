/*
 * 文 件 名:  UserSessionManager.java
 * 版    权:  Ysten Technologies Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  bingbing
 * 修改时间:  2014年5月8日
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package cn.huace.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

/**
 * <session管理应用>
 * 
 * @author bingbing
 * @version [版本号, 2014年5月8日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class UserSessionManager
{
    private static final UserSessionManager instance = new UserSessionManager();
    
    private UserSessionManager()
    {
        
    }
    public static UserSessionManager getInstance()
    {
        return instance;
    }
    /**
     * <往session中存放key和value键值>
     * @param key 
     * @param value
     * @see [类、类#方法、类#成员]
     */
    public void put(String key, Object value)
    {
        Session session = SecurityUtils.getSubject().getSession();
        if (session == null)
        {
            return;
        }
        session.setAttribute(key, value);
    }
    
    /**
     * <根据key取session中的value>
     * @param key
     * @return
     * @see [类、类#方法、类#成员]
     */
    public Object get(String key)
    {
        Session session = SecurityUtils.getSubject().getSession();
        if (session == null)
        {
            return null;
        }
        return session.getAttribute(key);
    }
    
    /**
     * <根据key和value校验session中是否存在>
     * @param key
     * @param value
     * @return boolean
     * @see [类、类#方法、类#成员]
     */
    public boolean checkKey(String key, String value)
    {
        Session session = SecurityUtils.getSubject().getSession();
        if (session == null)
        {
            return false;
        }
        String newValue = String.valueOf(session.getAttribute(key));
        
        if (StringUtils.equals(value, newValue))
        {
            return true;
        }
        return false;
    }
}
