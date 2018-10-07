package com.imooc.miaosha.access;

import com.imooc.miaosha.domain.MiaoshaUser;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/7 15:55
 **/
public class UserContext {

    private static final ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<MiaoshaUser>();

    public static void setMiaoshaUser(MiaoshaUser user){
        userHolder.set(user);
    };

    public static MiaoshaUser getMiaoshaUser(){
        return  userHolder.get();
    }
}
