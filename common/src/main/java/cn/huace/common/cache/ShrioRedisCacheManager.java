package cn.huace.common.cache;


import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

public class ShrioRedisCacheManager extends AbstractCacheManager{
	private RedisTemplate<String, Object> redisTemplate;

	public ShrioRedisCacheManager(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	protected Cache<String, Object> createCache(String name) throws CacheException {
		return new ShrioRedisCache<String, Object>(redisTemplate, name);
	}
}
