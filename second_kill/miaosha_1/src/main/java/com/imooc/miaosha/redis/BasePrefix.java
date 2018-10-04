package com.imooc.miaosha.redis;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/4 17:34
 **/
public abstract class BasePrefix implements KeyPrefix{

    private int expireSeconds;
    private String prefix;

    public BasePrefix(int expireSeconds,String prefix){
        // 0 代表永不过期，
        this.expireSeconds=expireSeconds;
        this.prefix=prefix;
    }
    public BasePrefix(String prefix){
       this(0,prefix);
    }

    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        // 防止不同类或者不同模块存储到redis中的key重复问题，使用类型区分
        String className=getClass().getSimpleName();
        return className+":"+prefix;
    }
}
