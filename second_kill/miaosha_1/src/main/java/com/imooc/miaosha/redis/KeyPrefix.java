package com.imooc.miaosha.redis;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/4 17:33
 **/
public interface KeyPrefix {

    int expireSeconds();
    String getPrefix();
}
