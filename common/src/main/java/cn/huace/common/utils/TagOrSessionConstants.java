/*
 * 文 件 名:  TagConstants.java
 * 版    权:  Ysten Technologies Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  bingbing
 * 修改时间:  2014年5月13日
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package cn.huace.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * <自定义标签或session使用的常量类>
 * 
 * @author bingbing
 * @version [版本号, 2014年5月13日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class TagOrSessionConstants
{
    /**
     * 按钮类型
     */
    public static final String BUTTON_TYPE = "1";
    
    /**
     * a标签
     */
    public static final String A_TAG_TYPE = "2";
    
    /**
     * 不需要展示的属性
     */
    private static List<String> noDisplayKeys = new ArrayList<String>();
    
    static
    {
        noDisplayKeys.add("menuId");
        noDisplayKeys.add("buttonCode");
        noDisplayKeys.add("type");
    }
    
    public static List<String> getNoDisplayKeys()
    {
        return noDisplayKeys;
    }
    
    /**
     * session中的登陆用户名
     */
    public static final String SESSION_LOGINNAME = "loginName";
    
    /**
     * session中的登陆用户名
     */
    public static final String SESSION_MENUDISPLAY = "menuDisplay";
    
    /**
     * session中的登陆用户名
     */
    public static final String SESSION_BUTTON_RIGHT = "userButton";
}
