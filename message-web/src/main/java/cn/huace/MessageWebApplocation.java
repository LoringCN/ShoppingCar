package cn.huace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by wjcomputer on 2017/10/18.
 */
@ServletComponentScan
@ComponentScan(basePackages = { "cn.huace.*" })
@EnableJpaRepositories
@EnableTransactionManagement
@EnableScheduling
@SpringBootApplication
public class MessageWebApplocation extends SpringBootServletInitializer{
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MessageWebApplocation.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(MessageWebApplocation.class,args);
    }
}
