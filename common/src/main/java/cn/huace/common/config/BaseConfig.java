package cn.huace.common.config;


import cn.huace.common.exception.SystemExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;


/**
 * Created by Administrator on 2016/12/13.
 */
@Configuration
public class BaseConfig {

    @Bean
    public HandlerExceptionResolver SystemExceptionHandler(){
     return  new SystemExceptionHandler();
    }

}
