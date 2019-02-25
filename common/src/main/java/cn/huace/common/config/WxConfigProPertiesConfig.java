package cn.huace.common.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component("wxConfigProPertiesConfig")
@ConfigurationProperties(locations= {"classpath:weixin-prod.properties","classpath:weixin-dev.properties","classpath:weixin-test.properties"})
public class WxConfigProPertiesConfig {
	
	   String appId;//微信的AppId
	   
	   String appSecret; 
	
	   String mchId;//商户ID
	
	   String key;//微信商户KEY
	  
	   String notifyUrl;//回调URL
	   
	   String spbillCreateIp;//申请支付ip
	   
	   String orderInfoUrl;//获取订单详情URL
	   
	   String dev;
	   
	   public static WxConfigProPertiesConfig getInstance(){
	       return (WxConfigProPertiesConfig) SpringApplicationContextHolder.getSpringBean("wxConfigProPertiesConfig");
	   }
	

}
