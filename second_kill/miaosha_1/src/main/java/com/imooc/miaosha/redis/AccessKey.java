package com.imooc.miaosha.redis;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/6 13:33
 **/
public class AccessKey extends BasePrefix{



    private AccessKey(String prefix) {
        super(prefix);
    }
    private AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static AccessKey withExpires(int expireSeconds){
        return new AccessKey(expireSeconds, "access");
    }
}
