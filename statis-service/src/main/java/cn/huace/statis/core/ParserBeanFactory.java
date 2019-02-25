package cn.huace.statis.core;
import java.util.HashMap;
import java.util.Map;

import cn.huace.common.config.SpringApplicationContextHolder;
import cn.huace.statis.handle.Constants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Slf4j
public class ParserBeanFactory {

	@SuppressWarnings("rawtypes")
    public static Object getParserBean(String beanName) {
		String className = Constants.PARSERS_Map.get(beanName);
		if(className == null || className.equals("")) {
			log.warn("no such bean:{}", beanName);
		}
		Object bean =null;
        try {
            bean = SpringApplicationContextHolder.getSpringBean(className);
        } catch (Exception e) {
            log.error("", e);
        }
        return bean;
    }
}
