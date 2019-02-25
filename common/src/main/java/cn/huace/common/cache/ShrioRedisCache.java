package cn.huace.common.cache;


import cn.huace.common.utils.SerializeUtils;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.*;

@Slf4j
public class ShrioRedisCache<K, V> implements Cache<K, V> {
    //	private Logger log = LogManager.getLogger(getClass());
    private RedisTemplate<String, V> redisTemplate;
    private String prefix = "shiro_redis:";

    public ShrioRedisCache(RedisTemplate<String, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public ShrioRedisCache(RedisTemplate<String, V> redisTemplate, String prefix) {
        this(redisTemplate);
        this.prefix = prefix;
    }


    public V get(K key) throws CacheException {
        if (log.isDebugEnabled()) {
            log.debug("Key: {}", key);
        }
        if (key == null) {
            return null;
        }

//		byte[] bkey = getByteKey(key);
//		return redisTemplate.opsForValue().get(bkey);
        return redisTemplate.opsForValue().get(getStringKey(key));
    }


    public V put(K key, V value) throws CacheException {
        if (log.isDebugEnabled()) {
            log.debug("Key: {}, value: {}", key, value);
        }

        if (key == null || value == null) {
            return null;
        }

//		byte[] bkey = getByteKey(key);
//		redisTemplate.opsForValue().set(bkey, value);
        redisTemplate.opsForValue().set(getStringKey(key), value);
        return value;
    }


    public V remove(K key) throws CacheException {
        if (log.isDebugEnabled()) {
            log.debug("Key: {}", key);
        }

        if (key == null) {
            return null;
        }

//		byte[] bkey = getByteKey(key);
//		ValueOperations<byte[], V> vo = redisTemplate.opsForValue();
        ValueOperations<String, V> vo = redisTemplate.opsForValue();
//		V value = vo.get(bkey);
//		redisTemplate.delete(bkey);

        String stringKey = getStringKey(key);
        V value = vo.get(stringKey);
        redisTemplate.delete(stringKey);
        return value;
    }


    public void clear() throws CacheException {
//		redisTemplate.getConnectionFactory().getConnection().flushDb();

//		byte[] bkey = (prefix+"*").getBytes();
//		Set<byte[]> set = redisTemplate.keys(bkey);
//		if(!CollectionUtils.isEmpty(set)) {
//			for(byte[] key:set) {
//				redisTemplate.delete(key);
//			}
//		}

        String stringkey = "prefix*";
        Set<String> set = redisTemplate.keys(stringkey);
        if (!CollectionUtils.isEmpty(set)) {
            for (String key : set) {
                redisTemplate.delete(key);
            }
        }
    }

    public int size() {
//		Long len = redisTemplate.getConnectionFactory().getConnection().dbSize();
//		return len.intValue();
//        byte[] bkey = (prefix + "*").getBytes();
//        Set<byte[]> set = redisTemplate.keys(bkey);
        String key = "prefix*";
        return redisTemplate.keys(key).size();
    }

    @SuppressWarnings("unchecked")
    public Set<K> keys() {
//        byte[] bkey = (prefix + "*").getBytes();
        String keys = "prefix*";
        Set<String> set = redisTemplate.keys(keys);
        Set<K> result = Sets.newHashSet();

        if (CollectionUtils.isEmpty(set)) {
            return Collections.emptySet();
        }

        for (String key : set) {
            result.add((K) key);
        }
        return result;
    }


    public Collection<V> values() {
        Set<K> keys = keys();
        List<V> values = new ArrayList<V>(keys.size());
        for (K k : keys) {
            byte[] bkey = getByteKey(k);
            values.add(redisTemplate.opsForValue().get(bkey));
        }
        return values;
    }

    private byte[] getByteKey(K key) {
        if (key instanceof String) {
            String preKey = this.prefix + key.toString();
            return preKey.getBytes();
        } else {
            return SerializeUtils.serialize(key);
        }
    }

    private String getStringKey(K key) {
        return this.prefix + key.toString();
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}