package cn.huace.goods.config;

import cn.huace.common.config.SpringApplicationContextHolder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by yld on 2017/5/24.
 */
@Data
@Component("goodsProperties")
@ConfigurationProperties(locations = "classpath:goods.properties")
public class GoodsConfig {

    String recommendGoodsSize;

    public static GoodsConfig getInstance(){
        return (GoodsConfig) SpringApplicationContextHolder.getSpringBean("goodsProperties");
    }
}
