package cn.huace.common.utils.redis;


import cn.huace.common.utils.SerializeUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import redis.clients.util.SafeEncoder;

import javax.annotation.Resource;
import java.util.*;

@Component
public class RedisService
{

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisTemplate redisTemplate;

    public String getCacheValue(final String key, final Integer dbIndex)
    {
        return this.stringRedisTemplate.execute(new RedisCallback<String>()
        {

            public String doInRedis(RedisConnection connection)
                throws DataAccessException
            {
                connection.select(dbIndex);
                byte[] temp = connection.get(key.getBytes());
                if (temp != null)
                {
                    return SafeEncoder.encode(temp);
                }
                return null;
            }
        });
    }
    

    public void setCacheValue(final String key, final String value, final Integer dbIndex)
    {
        this.stringRedisTemplate.execute(new RedisCallback<Object>()
        {
            public Object doInRedis(RedisConnection connection)
                throws DataAccessException
            {
                connection.select(dbIndex);
                connection.set(key.getBytes(), value.getBytes());
                return null;
            }
        });
    }
    

    public String getCacheValue(final String key)
    {
        return this.stringRedisTemplate.execute(new RedisCallback<String>()
        {

            public String doInRedis(RedisConnection connection)
                throws DataAccessException
            {
                byte[] temp = connection.get(key.getBytes());
                if (temp != null)
                {
                    return SafeEncoder.encode(temp);
                }
                return null;
            }
        });
    }
    

    public void setCacheValue(final String key, final String value)
    {
        this.stringRedisTemplate.execute(new RedisCallback<Object>()
        {

            public Object doInRedis(RedisConnection connection)
                throws DataAccessException
            {
                connection.set(key.getBytes(), value.getBytes());
                return null;
            }
        });
    }
    

    public void setCacheValue(final String key, final Integer seconds, final String value, final Integer dbIndex)
    {
        this.stringRedisTemplate.execute(new RedisCallback<Object>()
        {

            public Object doInRedis(RedisConnection connection)
                throws DataAccessException
            {
                connection.select(dbIndex);
                connection.setEx(key.getBytes(), seconds, value.getBytes());
                return null;
            }
        });
    }
    
    /**
     * 
     * 更新失效时间
     */

    public void setExpire(final String key, final Integer seconds, final Integer dbIndex)
    {
        this.stringRedisTemplate.execute(new RedisCallback<Object>()
        {

            public Object doInRedis(RedisConnection connection)
                throws DataAccessException
            {
                connection.select(dbIndex);
                connection.expire(key.getBytes(), seconds);
                return null;
            }
        });
    }
    

    public void delBykey(final Integer dbIndex, final String key)
    {
        this.stringRedisTemplate.execute(new RedisCallback<Object>()
        {

            public Object doInRedis(RedisConnection connection)
                throws DataAccessException
            {
                connection.select(dbIndex);
                connection.del(key.getBytes());
                return null;
            }
        });
    }
    
    /**
     * 清空数据库 param:Integer dbIndex(数据库的标识)
     */
    public void redisFlushdb(final Integer dbIndex)
    {
        
        this.stringRedisTemplate.execute(new RedisCallback<Object>()
        {

            public Object doInRedis(RedisConnection connection)
                throws DataAccessException
            {
                connection.select(dbIndex);
                connection.flushDb();
                return null;
            }
        });
    }
    

    public List<String> getAllKeys(final Integer dbIndex)
    {
        return this.stringRedisTemplate.execute(new RedisCallback<List<String>>()
        {

            public List<String> doInRedis(RedisConnection connection)
                throws DataAccessException
            {
                List<String> result = new ArrayList<String>();
                connection.select(dbIndex);
                Set<byte[]> keys = connection.keys("*".getBytes());
                for (byte[] key : keys)
                {
                    result.add(SafeEncoder.encode(key));
                }
                return result;
            }
        });
    }

    /********************************************* redis操作Map数据开始 *************************************************/

    public void hashMapSetValue(final String mapKey, final String key, final String value, final Integer dbIndex)
    {
        this.stringRedisTemplate.execute(new RedisCallback<Object>()
        {

            public Object doInRedis(RedisConnection connection)
                throws DataAccessException
            {
                connection.select(dbIndex);
                connection.hSet(mapKey.getBytes(), key.getBytes(), value.getBytes());
                return null;
            }
        });
    }

    public void hashMapSetValue(final String mapKey, final Map<byte[], byte[]> value, final Integer dbIndex)
    {
        this.stringRedisTemplate.execute(new RedisCallback<Object>()
        {
            public Object doInRedis(RedisConnection connection)
                throws DataAccessException
            {
                connection.select(dbIndex);
                connection.hMSet(mapKey.getBytes(), value);
                return null;
            }
        });
        
    }
    

    public String hashMapGetValue(final String mapKey, final String key, final Integer dbIndex)
    {
        return this.stringRedisTemplate.execute(new RedisCallback<String>()
        {
            public String doInRedis(RedisConnection connection)
                throws DataAccessException
            {
                connection.select(dbIndex);
                byte[] temp = connection.hGet(mapKey.getBytes(), key.getBytes());
                if (temp != null)
                {
                    return SafeEncoder.encode(temp);
                }
                return null;
            }
        });
    }
    

    public Map<String, String> hashMapGetAllValue(final String mapKey, final Integer dbIndex)
    {
        return this.stringRedisTemplate.execute(new RedisCallback<Map<String, String>>()
        {

            public Map<String, String> doInRedis(RedisConnection connection)
                throws DataAccessException
            {
                Map<String, String> result = new HashMap<String, String>();
                connection.select(dbIndex);
                Map<byte[], byte[]> temp = connection.hGetAll(mapKey.getBytes());
                
                for (Map.Entry<byte[], byte[]> entry : temp.entrySet())
                {
                    result.put(SafeEncoder.encode(entry.getKey()), SafeEncoder.encode(entry.getValue()));
                }
                return result;
            }
        });
    }
    

    public void hashMapMgetAllValue(final String[] mapKeys, final Integer dbIndex)
    {
        
    }
    

    public void hashMapDelByKey(final String mapKey, final String key, final Integer dbIndex)
    {
        this.stringRedisTemplate.execute(new RedisCallback<Object>()
        {
            public Object doInRedis(RedisConnection connection)
                throws DataAccessException
            {
                connection.select(dbIndex);
                connection.hDel(mapKey.getBytes(), key.getBytes());
                return null;
            }
        });
    }

    /**
     * 以键值对形式保存Map数据至redis
     * @param redisKey -- redis命名空间key
     * @param key -- map中key值
     * @param value -- map中value值
     * @param dbindex -- redis库索引
     */
    public void hsetCacheValue(final String redisKey, final String key, final Object value,final Integer dbindex){
        this.redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                selectRedisDB(redisConnection,dbindex);
                redisConnection.hSet(SafeEncoder.encode(redisKey),SafeEncoder.encode(key), SerializeUtils.serialize(value));
                return null;
            }
        });
    }
    /**
     * 根据单个map的key获取对应value值
     */
    public Object hgetCacheValue(final String redisKey, final String key,final Integer dbindex){
        return this.redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                selectRedisDB(redisConnection,dbindex);
                return redisConnection.hGet(SafeEncoder.encode(redisKey),SafeEncoder.encode(key));
            }
        });
    }
    /**
     * 直接保存Map数据到redis
     * @param redisKey
     * @param data
     * @param dbindex 不设置时默认为第一个库
     * @param expireTime 过期时间，单位：秒
     */
    public void hmsetCacheValue(final String redisKey,final Map<String,Object> data,Integer dbindex,final Long expireTime){
        this.redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                Map<byte[],byte[]> serializeMap = new HashMap<>();
                for(Map.Entry<String,Object> entry:data.entrySet()){
                    String mapKey = entry.getKey();
                    Object mapValue = entry.getValue();
                    serializeMap.put(SafeEncoder.encode(mapKey),SerializeUtils.serialize(mapValue));
                }
                selectRedisDB(redisConnection,dbindex);
                redisConnection.hMSet(SafeEncoder.encode(redisKey),serializeMap);
                if(expireTime != null){
                    redisConnection.expire(SafeEncoder.encode(redisKey),expireTime);
                }
                return null;
            }
        });
    }

    /**
     * 从指定的库hash中读取全部的域和值
     * @param redisKey
     * @param dbindex
     * @return
     */
    public Object hgetAllCacheValue(final String redisKey,final Integer dbindex){
        return this.redisTemplate.execute(new RedisCallback(){
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                selectRedisDB(redisConnection,dbindex);
                Map<byte[],byte[]> mapBytes = redisConnection.hGetAll(SafeEncoder.encode(redisKey));
                Map<String,Object> result = new HashMap<>();
                for(Map.Entry<byte[],byte[]> entry:mapBytes.entrySet()){
                    String mapKey = SafeEncoder.encode(entry.getKey());
                    Object mapValue = SerializeUtils.deserialize(entry.getValue());
                    result.put(mapKey,mapValue);
                }
                return result;
            }
        });
    }
    /**
     * 获取Map中指定键的值
     */
    public Object hmgetCacheValueByMapkeys(final String redisKey,final String[] keys,Integer dbindex){
        return this.redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                selectRedisDB(redisConnection,dbindex);
                byte[][] keyBytes = new byte[keys.length][];
                for(int i = 0;i<keys.length;i++){
                    keyBytes[i] = SafeEncoder.encode(keys[i]);
                }
                List<byte[]> list = redisConnection.hMGet(SafeEncoder.encode(redisKey),keyBytes);
                List<Object> result = new ArrayList<>();
                for(byte[] value:list){
                    Object obj = SerializeUtils.deserialize(value);
                    result.add(obj);
                }
                return result;
            }
        });
    }
    /**
     * 判断Map中某个key是否存在
     */
    public Object hexistsByMapKey(final String redisKey,final String key,final Integer dbindex){
        return this.redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                selectRedisDB(redisConnection,dbindex);
                Boolean result = redisConnection.hExists(SafeEncoder.encode(redisKey),SafeEncoder.encode(key));
                return result;
            }
        });
    }

    /********************************************* redis操作Map数据结束 *************************************************/

    /********************************************* redis操作Set数据开始 ************************************************/

    public void hashSetAddValue(final String setKey, final String value, final Integer dbIndex)
    {
        this.stringRedisTemplate.execute(new RedisCallback<Object>()
        {
            public Object doInRedis(RedisConnection connection)
                throws DataAccessException
            {
                connection.select(dbIndex);
                connection.sAdd(setKey.getBytes(), value.getBytes());
                return null;
            }
        });
    }
    

    public void hashSetDelMember(final String setKey, final String member, final Integer dbIndex)
    {
        this.stringRedisTemplate.execute(new RedisCallback<Object>()
        {
            public Object doInRedis(RedisConnection connection)
                throws DataAccessException
            {
                connection.select(dbIndex);
                connection.sRem(setKey.getBytes(), member.getBytes());
                return null;
            }
        });
        
    }
    

    public Long hashSetCount(final String setKey, final Integer dbIndex)
    {
        return this.stringRedisTemplate.execute(new RedisCallback<Long>()
        {
            public Long doInRedis(RedisConnection connection)
                throws DataAccessException
            {
                connection.select(dbIndex);
                return connection.sCard(setKey.getBytes());
            }
        });
        
    }

    /********************************************* redis操作Set数据结束 *************************************************/

    /********************************************* redis操作List数据开始 ************************************************/
    /**
     * 保存集合数据至redis
     */
    public Object lpushCacheValueList(final String redisKey,final List<Object> objects,final Integer dbindex){
        return this.redisTemplate.execute(new RedisCallback(){
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                selectRedisDB(redisConnection,dbindex);
                Object[] objArr = objects.toArray(new Object[objects.size()]);
                byte[][] datas = new byte[objects.size()][];
                for(int i=0;i<objArr.length;i++){
                    datas[i] = SerializeUtils.serialize(objArr[i]);
                }
                Long result = redisConnection.lPush(SafeEncoder.encode(redisKey),datas);
                return result;
            }
        });
    }

    /********************************************* redis操作List数据结束 ************************************************/

    public ValueOperations<String, String> getOpsForValue()
    {
        // TODO Auto-generated method stub
        return stringRedisTemplate.opsForValue();
    }
    public ValueOperations<String,Object> getObjectOpsForValue(){
        return this.redisTemplate.opsForValue();
    }
    public Integer incr(String key){
        return stringRedisTemplate.getConnectionFactory().getConnection().incr(key.getBytes()).intValue();
    }
    @SuppressWarnings("unchecked")
    public void deletePatter(String patter){
        Set<String>set = stringRedisTemplate.keys(patter);
        if(!CollectionUtils.isEmpty(set)) {
                stringRedisTemplate.delete(set);
        }
    }

    /**
     * 设置Object类型缓存数据，永久有效，默认库
     */
    public void setObjectCacheValue(final String redisKey,final Object value){
        setObjectCacheValue(redisKey,value,null);
    }
    /**
     * 根据指定redis库，设置Object类型缓存数据，永久有效
     */
    public void setObjectCacheValue(final String redisKey,final Object value,final Integer dbindex){
        this.redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                selectRedisDB(redisConnection,dbindex);
               redisConnection.set(SafeEncoder.encode(redisKey),SerializeUtils.serialize(value));
                return null;
            }
        });
    }
    /**
     * 设置Object类型缓存数据，指定过期时间，默认库
     * @param expire 过期时间，单位：秒
     */
    public void setEXObjectCacheValue(final String redisKey,final Object value,final Long expire){
        setEXObjectCacheValue(redisKey,value,expire,null);
    }
    /**
     * 根据指定redis库，设置Object类型缓存数据，指定过期时间
     * @param expire 过期时间，单位：秒
     */
    public void setEXObjectCacheValue(final String redisKey,final Object value,final Long expire,final Integer dbindex){
        this.redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                selectRedisDB(redisConnection,dbindex);
                redisConnection.setEx(SafeEncoder.encode(redisKey),expire,SerializeUtils.serialize(value));
                return null;
            }
        });
    }

    /**
     * 根据redisKey从默认redis库取回Object类型value,可以强转为对应的java类型
     */
    public Object getObjectCacheValue(final String redisKey){
        return getObjectCacheValue(redisKey,null);
    }

    /**
     * 根据redisKey从redis指定库取回Object类型value,可以强转为对应的java类型
     */
    public Object getObjectCacheValue(final String redisKey,final Integer dbindex){
        return this.redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                selectRedisDB(redisConnection,dbindex);
                byte[] bytes = redisConnection.get(SafeEncoder.encode(redisKey));
                return SerializeUtils.deserialize(bytes);
            }
        });
    }


    /**
     * 选择数据存储的RedisDB,如果不给定，默认为第一个库
     */
    private void selectRedisDB(RedisConnection redisConnection,Integer dbindex){
        if(dbindex != null){
            redisConnection.select(dbindex);
        }/*else{
            redisConnection.select(0);
        }*/
    }
}
