package cn.huace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@ServletComponentScan
@ComponentScan(basePackages = { "cn.huace.*" })
@EnableJpaRepositories
@EnableTransactionManagement
@SpringBootApplication
public class SysApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SysApplication.class);
    }

    public static void main(String[] args) throws Exception {
    	SpringApplication.run(SysApplication.class, args);
//    	System.out.println(VideoPlayUtils.findVideoPlayUrl("VID_20160812_151116.mp4"));;
//    	System.out.println(Config.getInstance().getMediaWorkflowName());
//
//    	String xml="<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg><appid><![CDATA[wx8d8e8a4b5d89b7b4]]></appid><mch_id><![CDATA[1362242202]]></mch_id><nonce_str><![CDATA[svteMyjiaUZPXZta]]></nonce_str><sign><![CDATA[FB531766D9D3D3645CC261B6E77B9E32]]></sign><result_code><![CDATA[SUCCESS]]></result_code><prepay_id><![CDATA[wx2016071314440161f8a018120097686369]]></prepay_id><trade_type><![CDATA[JSAPI]]></trade_type></xml>";
//    	WeiXinUtils.getXmlData(xml);
//    	System.out.println(((Config)SpringApplicationContextHolder.getSpringBean("config")).getFileHost());;
    }


}
