package com.imooc.miaosha.redis;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/4 17:46
 **/
public class UserKey extends BasePrefix{

    // 防止外界实例化UserKey，私有化构造器
    private UserKey(String prefix) {
        super(prefix);
    }
    // UserKey:namekeyName
    // 这样可以key防重：不同模块类名不同，UserKey也就不同，keyName是针对相同类中key的区分
    public static UserKey getById=new UserKey("id");
    public static UserKey getByName=new UserKey("name");

}
