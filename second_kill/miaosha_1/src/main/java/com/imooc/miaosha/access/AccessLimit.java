package com.imooc.miaosha.access;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/7 15:40
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {
    int seconds() default 5;
    int maxCount() default 5;
    boolean needLogin() default true;
}
