package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.User;
import com.imooc.miaosha.rabbitmq.MQSender;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.redis.UserKey;
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

    @Autowired
    MQSender mqSender;

    // 没有防重前缀的key
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

    // 有防重前缀key
    @RequestMapping("/redis/setUser")
    @ResponseBody
    public Result<User> redisSetUser(){
        User user=new User("王五");
        redisService.set(UserKey.getByName,"k3",user);
        return Result.success(user);
    }

    @RequestMapping("/redis/getUser")
    @ResponseBody
    public Result<User> redisGetUser(){
        User user=redisService.get(UserKey.getByName,"k3",User.class);
        return Result.success(user);
    }

    @RequestMapping("/rabbit")
    @ResponseBody
    public Result<MiaoshaUser> testRabbit(){
        MiaoshaUser user=redisService.get(UserKey.getById,"18297850973",MiaoshaUser.class);
        mqSender.sendMsg(user);
        return Result.success(user);
    }

    @RequestMapping("/rabbit/topic")
    @ResponseBody
    public Result<MiaoshaUser> testRabbitTopic(){
        MiaoshaUser user=redisService.get(UserKey.getById,"18297850973",MiaoshaUser.class);
        mqSender.sendMsg(user);
        mqSender.sendTopic(user);
        return Result.success(user);
    }
    @RequestMapping("/rabbit/fanout")
    @ResponseBody
    public Result<MiaoshaUser> testRabbitFanout(){
        MiaoshaUser user=redisService.get(UserKey.getById,"18297850973",MiaoshaUser.class);
        mqSender.sendFanout(user);
        return Result.success(user);
    }
}
