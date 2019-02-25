package cn.huace.common.config;

import cn.huace.common.constants.SolrCoreConstants;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpClientUtil;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;


/**
 *
 * Created by yld on 2017/6/21.
 */
@Configuration
@EnableSolrRepositories(basePackages = {"cn.huace.*"}, multicoreSupport = true)
public class SolrConfig {

    @Autowired
    private SolrServerPropertiesConfig solrServerPropertiesConfig;


    @Bean
    public SolrClient solrClient(){
        System.out.print("solrServerPropertiesConfig:"+solrServerPropertiesConfig);
        return new HttpSolrClient(solrServerPropertiesConfig.getServerHost(),closeableHttpClient());
    }

    @Bean
    public SolrTemplate solrTemplate(){
        return new SolrTemplate(solrClient());
    }

    /**
     * <p>从官方文档介绍可以看出从spring-data-solr 2.1版本起才支持以下特性：</p>
     *   Autoselect Solr core using SolrTemplate
     *  <p>由于本项目spring-boot版本为1.4.3,不兼容spring-data-solr 2.1，</p>
     *  <p>只能支持到spring-data-solr 2.0.6版本，多核时solrTemplate不能自动绑定solrCore,</p>
     *  <p>所以为了使用SolrTemplate API操作指定的solrCore,需要单独注册绑定了指定solrCore的solrTemplate</p>
     */
    @Bean("goodsSolrTemplate")
    public SolrTemplate goodsSolrTemplate(){
        String baseURL = solrServerPropertiesConfig.getServerHost()+ SolrCoreConstants.SOLR_CORE_NAME_GOODS;
        SolrClient solrClient = new HttpSolrClient(baseURL,closeableHttpClient());
        return new SolrTemplate(solrClient);
    }
    /*
        服务器开启了Basic安全验证，需在连接solr服务时传入client
        替代默认defaultHttpClient
     */
    private CloseableHttpClient closeableHttpClient(){
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set(HttpClientUtil.PROP_MAX_CONNECTIONS, 128);
        params.set(HttpClientUtil.PROP_MAX_CONNECTIONS_PER_HOST, 32);
        params.set(HttpClientUtil.PROP_FOLLOW_REDIRECTS, false);
        params.set(HttpClientUtil.PROP_BASIC_AUTH_USER, solrServerPropertiesConfig.getUserName());
        params.set(HttpClientUtil.PROP_BASIC_AUTH_PASS, solrServerPropertiesConfig.getPassword());
        params.set(HttpClientUtil.PROP_MAX_CONNECTIONS, 1000);
        params.set(HttpClientUtil.PROP_ALLOW_COMPRESSION, true);
        params.set(HttpClientUtil.PROP_MAX_CONNECTIONS_PER_HOST, 1000);
        CloseableHttpClient closeableHttpClient = HttpClientUtil.createClient(params);
        return closeableHttpClient;
    }
}
