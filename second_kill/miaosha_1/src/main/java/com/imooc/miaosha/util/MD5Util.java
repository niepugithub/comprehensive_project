package com.imooc.miaosha.util;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/4 20:00
 **/
public class MD5Util {
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    private static final String salt="1a2b3c4d";
    // 用户输入密码转化为表单密码
    public static String inputPassFormPass(String inputPass){
        String str = salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }
    // 表单密码转化为DB密码
    public static String formPassDBPass(String formPass,String salt){
        String str = salt.charAt(0)+salt.charAt(2)+formPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    // 用户输入密码转化为DB密码
    public static String inputPassDBPass(String input,String salt){
        String formPass=inputPassFormPass(input);
        String dbPass = formPassDBPass(formPass,salt);
        return dbPass;
    }

    public static void main(String[] args) {
        // d3b1294a61a07da9b49b6e22b2cbd7f9
        // d3b1294a61a07da9b49b6e22b2cbd7f9
        System.out.println(inputPassFormPass("123456"));
        System.out.println(formPassDBPass("d3b1294a61a07da9b49b6e22b2cbd7f9","1a2b3c4d"));
        System.out.println(inputPassDBPass("123456","1a2b3c4d"));
    }
}
