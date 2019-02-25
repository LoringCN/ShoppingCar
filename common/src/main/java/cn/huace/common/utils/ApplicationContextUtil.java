package cn.huace.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 
 * <上下文获取工具类><br />
 * <功能详细描述>
 *
 * @author 陆小凤
 * @version [版本号1.0, 2014年9月25日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class ApplicationContextUtil implements ApplicationContextAware
{
	private static ApplicationContext context = null;


	public void setApplicationContext(ApplicationContext context) throws BeansException
	{
		if (null == ApplicationContextUtil.context)
		{
			ApplicationContextUtil.context = context;
		}
	}

	public static ApplicationContext getContext()
	{
		return context;
	}

}
