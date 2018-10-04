package com.imooc.miaosha.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/9/28 22:42
 **/
@Service
public class RedisService {
    @Autowired
    JedisPool jedisPool;
    public <T> T get(String key,Class<T> clazz){
        Jedis jedis = null;
        T t=null;
        try{
            jedis=jedisPool.getResource();
            // 设置密码
//            jedis.auth("12345");
            String str = jedis.get(key);
            t = stringToBean(str,clazz);
        }finally {
            returnToPool(jedis);
        }
        return t;
    }
    public <T> boolean set(String key,T value){
        Jedis jedis = null;
        try{
            jedis=jedisPool.getResource();
            // 设置密码
//            jedis.auth("12345");
            String str = beanToString(value);
            if(str==null || str.length()<=0){
                return false;
            }
            jedis.set(key,str);
            return true;
        }finally {
            returnToPool(jedis);
        }
    }

    private <T> String beanToString(T value) {
        // 如果value不是字符串而是int咋办？需要校验判断
        if(value==null){
            return null;
        }
        Class<?> clazz=value.getClass();
        if(clazz==int.class || clazz==Integer.class){
            return value+"";
        }else if(clazz==String.class){
            return (String)value;
        }else if(clazz==long.class || clazz==Long.class){
            return value+"";
        }else {
            return JSON.toJSONString(value);
        }
    }

    private <T> T stringToBean(String str,Class<T> clazz) {
        if(str==null || str.length()<=0){
            return null;
        }
        if(clazz==int.class || clazz==Integer.class){
            return (T)Integer.valueOf(str);
        }else if(clazz==String.class){
            return (T) str;
        }else if(clazz==long.class || clazz==Long.class){
            return (T)Long.valueOf(str);
        }else {
            return JSON.toJavaObject(JSON.parseObject(str),clazz);
        }
    }

    private void returnToPool(Jedis jedis) {
        if(jedis!=null){
            // 这里也不是真的close，而是还给连接池
            jedis.close();
        }
    }
}












