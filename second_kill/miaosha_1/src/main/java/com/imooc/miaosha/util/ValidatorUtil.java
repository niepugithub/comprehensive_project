package com.imooc.miaosha.util;

import org.apache.ibatis.annotations.Mapper;
import org.thymeleaf.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:校验工具类；
 * @author:niepu
 * @version:1.0
 * @date:2018/10/4 21:11
 **/
public class ValidatorUtil {
    // 简单的判断下是不是手机号：如果以1开头，并且后面加上10个数字就认为是手机号
    private static final Pattern mobile_pattern= Pattern.compile("1\\d{10}");
    public static boolean isMobile(String src){
        if(StringUtils.isEmpty(src)){
            return false;
        }
        Matcher matcher=mobile_pattern.matcher(src);
        return matcher.matches();
    }

    // 简单测试下
    public static void main(String[] args) {
        System.out.println(isMobile("12322222222"));
    }
}
