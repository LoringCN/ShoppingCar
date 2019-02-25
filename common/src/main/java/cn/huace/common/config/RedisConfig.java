package cn.huace.common.config;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by Administrator on 2016/12/8.
 */
@Configuration
public class RedisConfig {

    @Autowired
    SystemConfig systemConfig;

    @Bean
    public JedisConnectionFactory connectionFactory(JedisPoolConfig jedisPoolConfig)
    {
        JedisConnectionFactory connection = new JedisConnectionFactory();
        System.out.println("systemConfig : " + systemConfig);
        connection.setPort(Integer.parseInt(systemConfig.getRedisPort()));
        connection.setHostName(systemConfig.getRedisHost());
        if(!StringUtils.isBlank(systemConfig.getRedisPassword())){
            connection.setPassword(systemConfig.getRedisPassword());
        }
//        connection.setHostName("10.26.89.2");
//        connection.setPassword("dsycredis");
        connection.setPoolConfig(jedisPoolConfig);
        connection.setUsePool(true);
        return connection;
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig(){
        JedisPoolConfig jedisPoolConfig=new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(300);
        jedisPoolConfig.setMaxTotal(300);
        jedisPoolConfig.setMaxWaitMillis(30000);
        jedisPoolConfig.setMinIdle(30);
        jedisPoolConfig.setTestOnBorrow(true);
        //在将连接放回池中前，自动检验连接是否有效
        jedisPoolConfig.setTestOnReturn(true);
        //自动测试池中的空闲连接是否都是可用连接
        jedisPoolConfig.setTestWhileIdle(true);
        return jedisPoolConfig;
    }
    @Bean(name="redisTemplate")
    public RedisTemplate<byte[], Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<byte[], Object> template = new RedisTemplate<byte[], Object>();
          template.setConnectionFactory(jedisConnectionFactory);
          template.setKeySerializer(new StringRedisSerializer());

//        ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
////        //允许使用未带引号的字段名
////        om.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
////        //允许使用单引号
////        om.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//        jackson2JsonRedisSerializer.setObjectMapper(om);
//        template.setConnectionFactory(jedisConnectionFactory);
//        template.setDefaultSerializer(jackson2JsonRedisSerializer);
//        template.setKeySerializer(template.getStringSerializer());
//        template.setValueSerializer(jackson2JsonRedisSerializer);
//        template.setHashKeySerializer(template.getStringSerializer());
//        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        return template;
    }

    @Bean(name="redisStringTemplate")
    public StringRedisTemplate redisStringTemplate(JedisConnectionFactory jedisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

//                ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
////        //允许使用未带引号的字段名
////        om.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
////        //允许使用单引号
////        om.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//        jackson2JsonRedisSerializer.setObjectMapper(om);
//        template.setConnectionFactory(jedisConnectionFactory);
//        template.setDefaultSerializer(jackson2JsonRedisSerializer);
//        template.setKeySerializer(template.getStringSerializer());
//        template.setHashKeySerializer(template.getStringSerializer());
//        template.setValueSerializer(jackson2JsonRedisSerializer);
//        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        return template;
    }
}
