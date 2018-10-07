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
    /*
     * description: 获取单个对象，没有额外的前缀
     * author:niepu
     * param: [key, clazz]
     * date: 2018-10-04 18:33:23
     * return: T
     **/
    public <T> T get(String key,Class<T> clazz){
        Jedis jedis = null;
        T t=null;
        try{
            jedis=jedisPool.getResource();
            // 设置密码
            //  jedis.auth("12345");
            String str = jedis.get(key);
            t = stringToBean(str,clazz);
        }finally {
            returnToPool(jedis);
        }
        return t;
    }

    /*
     * description: 获取单个对象，有防重额外的前缀
     * author:niepu
     * param: [prefix, key, clazz]
     * date: 2018-10-04 18:33:42
     * return: T
     **/
    // redis的key防重的get方法
    public <T> T get(KeyPrefix prefix,String key,Class<T> clazz){
        Jedis jedis = null;
        T t=null;
        try{
            jedis=jedisPool.getResource();
            // 生成真正的key
            String realKey=prefix.getPrefix()+key;
            String str = jedis.get(realKey);
            t = stringToBean(str,clazz);
        }finally {
            returnToPool(jedis);
        }
        return t;
    }

    // 设置对象：没有防重前缀
    public <T> boolean set(String key,T value){
        Jedis jedis = null;
        try{
            jedis=jedisPool.getResource();
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
    // redis的key防重的set方法
    public <T> boolean set(KeyPrefix prefix,String key,T value){
        Jedis jedis = null;
        try{
            jedis=jedisPool.getResource();
            String str = beanToString(value);
            if(str==null || str.length()<=0){
                return false;
            }
            // 生成真正的key
            String realKey=prefix.getPrefix()+key;
            // set方法中设置过期时间
            int seconds=prefix.expireSeconds();
            if(seconds<=0){
                jedis.set(realKey,str);
            }else{
                // 过期时间；
                jedis.setex(realKey,seconds,str);
            }

            return true;
        }finally {
            returnToPool(jedis);
        }
    }

    // 判断key是否已经存在
    public <T> boolean exists(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis=jedisPool.getResource();
            // 生成真正的key
            String realKey=prefix.getPrefix()+key;
            return jedis.exists(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    public <T> Long incr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis=jedisPool.getResource();
            // 生成真正的key
            String realKey=prefix.getPrefix()+key;
            return jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }
    public <T> Long decr(KeyPrefix prefix,String key){
        Jedis jedis = null;
        try{
            jedis=jedisPool.getResource();
            // 生成真正的key
            String realKey=prefix.getPrefix()+key;
            return jedis.decr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    public static <T> String beanToString(T value) {
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

    public static <T> T stringToBean(String str,Class<T> clazz) {
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

    public boolean delete(KeyPrefix getById, String s) {
        Jedis jedis=null;
        try{
            jedis=jedisPool.getResource();
            // 生成realKey
            String realKey=getById.getPrefix()+s;
            long ret=jedis.del(realKey);
            return ret > 0;
        }finally {
            returnToPool(jedis);
        }
    }
}












