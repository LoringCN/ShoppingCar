package cn.huace.config;

import cn.huace.common.config.SpringApplicationContextHolder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * Created by Administrator on 2016/12/13.
 */
@Data
@Component
@ConfigurationProperties
public class Config {

    String statFilePath;

    public static Config getInstance(){
        return (Config) SpringApplicationContextHolder.getSpringBean("config");
    }
}
