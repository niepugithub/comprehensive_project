package com.imooc.miaosha.redis;

import com.imooc.miaosha.domain.MiaoshaUser;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/5 12:11
 **/
public class MiaoshaUserKey extends BasePrefix {
    private final static int TOKEN_EXPIRE=1800;
    private MiaoshaUserKey(int expireSeconds, String prefix) {
        // 这里的prefix是父类中定义的属性，子类中并没有，如果不调用父类的构造器，则这个属性
        // 就不会被初始化赋值，需要留意
        super(expireSeconds, prefix);
    }

    private MiaoshaUserKey(String prefix) {
        super(prefix);
    }

    public static MiaoshaUserKey token=new MiaoshaUserKey(TOKEN_EXPIRE,"token");
    public static MiaoshaUserKey getById = new MiaoshaUserKey(0, "id");
}
