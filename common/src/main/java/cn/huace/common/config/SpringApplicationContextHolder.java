package cn.huace.common.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
@Component
public class SpringApplicationContextHolder  implements ApplicationContextAware{
	
	private static ApplicationContext context;

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SpringApplicationContextHolder.context = context;
    }

   
    public static Object getSpringBean(String beanName) {
//        notEmpty(beanName, "bean name is required");
//    	System.out.println(context);
        return context==null?null:context.getBean(beanName);
    }

    public static String[] getBeanDefinitionNames() {
        return context.getBeanDefinitionNames();
    }
}
