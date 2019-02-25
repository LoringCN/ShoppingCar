package cn.huace.config;


import cn.huace.common.cache.ShrioRedisCacheManager;
import cn.huace.sys.service.ShiroDbRealm;
import com.google.common.collect.Maps;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.AnonymousFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.Map;


/**
 * Created by Administrator on 2016/12/8.
 */
@Configuration
public class ShiroConfig {
    /**
     * FilterRegistrationBean
     * @return
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilter"));
        filterRegistration.setEnabled(true);
        filterRegistration.addUrlPatterns("/*");
        filterRegistration.setDispatcherTypes(DispatcherType.REQUEST);
        return filterRegistration;
    }

    /**
     * @see ShiroFilterFactoryBean
     * @return
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(ShrioRedisCacheManager redisCacheManager){
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager(redisCacheManager));
        bean.setLoginUrl("/toLogin");
//        bean.setUnauthorizedUrl("/unauthor");

        Map<String, Filter>filters = Maps.newHashMap();
//        filters.put("perms", urlPermissionsFilter());
        filters.put("anon", new AnonymousFilter());
        bean.setFilters(filters);

        Map<String, String> chains = Maps.newHashMap();
        chains.put("/toLogin","anon");
        chains.put("/login", "anon");
        chains.put("/unauthor", "anon");
        chains.put("/base/**", "anon");
        chains.put("/rest/**", "anon");
        chains.put("/layer/**", "anon");
        chains.put("/admin/**", "authc,perms");
        bean.setFilterChainDefinitionMap(chains);
        return bean;
    }

    /**
     * @see org.apache.shiro.mgt.SecurityManager
     * @return
     */
    @Bean(name="securityManager")
    public DefaultWebSecurityManager securityManager(ShrioRedisCacheManager redisCacheManager) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(userRealm(redisCacheManager));
        manager.setCacheManager(redisCacheManager);
//        manager.setSessionManager(defaultWebSessionManager(redisCacheManager));
        return manager;
    }

//    /**
//     * @see
//     * @return
//     */
//    @Bean(name="sessionManager")
//    @DependsOn(value= "shrioRedisCacheManager")
//    public DefaultWebSessionManager defaultWebSessionManager(ShrioRedisCacheManager redisCacheManager) {
//        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
//        sessionManager.getSessionIdCookie().setPath("/");
//        sessionManager.getSessionIdCookie().setName("SHOP_CAR_SSIONID");
////        sessionManager.getSessionIdCookie().setHttpOnly(true);
//        sessionManager.setCacheManager(redisCacheManager);
//        sessionManager.setGlobalSessionTimeout(1800000);
//        sessionManager.setDeleteInvalidSessions(true);
//        sessionManager.setSessionValidationSchedulerEnabled(true);
//        sessionManager.setDeleteInvalidSessions(true);
//        return sessionManager;
//    }

    /**
     * @see --->AuthorizingRealm
     * @return
     */
    @Bean
    @DependsOn(value={"lifecycleBeanPostProcessor", "shrioRedisCacheManager"})
    public ShiroDbRealm userRealm(ShrioRedisCacheManager redisCacheManager) {
        ShiroDbRealm userRealm = new ShiroDbRealm();
        userRealm.setCacheManager(redisCacheManager);
        userRealm.setCachingEnabled(true);
        userRealm.setAuthenticationCachingEnabled(true);
        userRealm.setAuthorizationCachingEnabled(true);
        return userRealm;
    }

    @Bean(name="shrioRedisCacheManager")
    @DependsOn(value="redisTemplate")
    public ShrioRedisCacheManager redisCacheManager(RedisTemplate redisTemplate) {
        ShrioRedisCacheManager cacheManager = new ShrioRedisCacheManager(redisTemplate);
        return cacheManager;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
}
