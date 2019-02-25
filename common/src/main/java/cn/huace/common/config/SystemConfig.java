package cn.huace.common.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2016/12/13.
 */
@Data
@Component("systemConfig")
//@ConfigurationProperties(locations= "classpath:systemconfig*.properties")
//@ConfigurationProperties(locations= "classpath:systemconfig-${profileActive}.properties")
@ConfigurationProperties(locations= {"classpath:systemconfig-prod.properties","classpath:systemconfig-dev.properties","classpath:systemconfig-test.properties"})
//@SpringBootApplication
public class SystemConfig {
    // @Value("${redisHost}")
    String redisHost;
     // @Value("${redisPort}")
    String redisPort;
    // @Value("${redisPassword}")
    String redisPassword;
    // @Value("${accessKeyId}")
    String accessKeyId;
    // @Value("${accessKeySecret}")
    String accessKeySecret;
    // @Value("${ossURL}")
    String ossURL;
    // @Value("${ossBulketName}")
    String ossBulketName;
    // @Value("${ossEndpoint}")
    String ossEndpoint;
    // @Value("${fileTemp}")
    String fileTemp;
    // @Value("${filePre}")
    String filePre;
    // @Value("${domain}")
    String domain;
    // @Value("${mobileProject}")
    String mobileProject;
    // @Value("${backendProject}")
    String backendProject;
    // @Value("${websocket}")
    String websocket;

    public static SystemConfig getInstance(){
        return (SystemConfig) SpringApplicationContextHolder.getSpringBean("systemConfig");
    }
}
