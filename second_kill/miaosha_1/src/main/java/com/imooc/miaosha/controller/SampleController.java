package com.imooc.miaosha.controller;

import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/9/28 22:43
 **/
@Controller
@RequestMapping("/sample")
public class SampleController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<Long> redisGet(){
        Long v1=redisService.get("k1",Long.class);
        return Result.success(v1);
    }

    @RequestMapping("redis/set")
    @ResponseBody
    public Result<String> redisSet(){
        boolean ret= redisService.set("k2","hello,imooc");
        String s=redisService.get("k2",String.class);
        return Result.success(s);
    }
}
