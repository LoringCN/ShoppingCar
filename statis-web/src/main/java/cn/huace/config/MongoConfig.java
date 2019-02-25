package cn.huace.config;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class MongoConfig {

	@Bean
	public MappingMongoConverter mongoConverter(MongoDbFactory factory,MongoMappingContext context, BeanFactory beanFactory)throws Exception {
		DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
		MappingMongoConverter mappingConverter = new MappingMongoConverter(
				dbRefResolver, context);
		try {
			mappingConverter.setCustomConversions(beanFactory.getBean(CustomConversions.class));
		} catch (NoSuchBeanDefinitionException ignore) {
		
		}
		// Don't save _class to mongo
		mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
		//设置key中的.为_
		mappingConverter.setMapKeyDotReplacement("_");
		
		mappingConverter.afterPropertiesSet();
		return mappingConverter;
	}
}
