package cn.huace.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * Created by yld on 2017/6/26.
 */
@Data
@Component("solrServerPropertiesConfig")
@ConfigurationProperties(locations={"classpath:solrServer-prod.properties","classpath:solrServer-test.properties","classpath:solrServer-dev.properties"},prefix = "solr")
//@ConfigurationProperties(prefix = "solr")
//@PropertySource(value = {"classpath:solrServer-prod.properties","classpath:solrServer-test.properties","classpath:solrServer-dev.properties"})
//@PropertySource(value = "classpath:solrServer-${profileActive}.properties")
public class SolrServerPropertiesConfig {

    private String serverHost;

    private String userName;

    private String password;

    public static SolrServerPropertiesConfig getInstance(){
        return (SolrServerPropertiesConfig)SpringApplicationContextHolder.getSpringBean("solrServerPropertiesConfig");
    }

}
