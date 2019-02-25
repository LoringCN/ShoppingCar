package cn.huace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by yld on 2017/9/11.
 */
@ServletComponentScan
@ComponentScan(basePackages = {"cn.huace.*"})
@EnableJpaRepositories
@EnableTransactionManagement
@SpringBootApplication
public class ScheduleApplication extends SpringBootServletInitializer{
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ScheduleApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(ScheduleApplication.class,args);

    }
}
