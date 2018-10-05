package com.imooc.miaosha.util;

import java.util.UUID;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/5 12:07
 **/
public class UUIDUtil {
    public static String uuid(){
        // 原生的uuid带有"-"，这里手动去掉"-"
        return UUID.randomUUID().toString().replace("-","");
    }
}
