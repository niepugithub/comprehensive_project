package com.imooc.miaosha.redis;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/4 17:34
 **/
public class BasePrefix implements KeyPrefix{

    private int expireSeconds;
    private String prefix;

    public BasePrefix(int expireSeconds,String prefix){
        this.expireSeconds=expireSeconds;
        this.prefix=prefix;
    }

    @Override
    public int expireSeconds() {
        // 0 代表永不过期，
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }
}
