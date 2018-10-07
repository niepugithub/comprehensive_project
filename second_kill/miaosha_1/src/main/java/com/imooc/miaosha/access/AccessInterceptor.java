package com.imooc.miaosha.access;

import com.alibaba.fastjson.JSON;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.redis.AccessKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/7 15:44
 **/
@Component
public class AccessInterceptor extends HandlerInterceptorAdapter{
    @Autowired
    MiaoshaUserService userService;
    @Autowired
    RedisService redisService;
    public boolean preHandle(HttpServletRequest request, HttpServletResponse
            response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            // 获取到miaoshaUser保存到ThreadLocal中
            MiaoshaUser user = getMiaoshaUser(request,response);
            UserContext.setMiaoshaUser(user);

            // 首先要强转成HandlerMethod，否则啥功能方法都没有
            HandlerMethod hm=(HandlerMethod)handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if(accessLimit==null){// 如果没有这么注解，返回true；
                return true;
            }
            // 取用户；如何取呢？

            int seconds = accessLimit.seconds();

            boolean needLogin=accessLimit.needLogin();
            String key=request.getRequestURI();
            if(needLogin){
                if(user==null){
                    // 拦截器中没法返回错误信息给页面进行提示WebUtil工具类实现传递错误信息
                    render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key+="_"+user.getId();
            }else{
                // do nothing
            }
            // martine flower 重构-改善既有代码的设计
            AccessKey ak=AccessKey.withExpires(seconds);
            int maxCount=accessLimit.maxCount();
            Integer count=redisService.get(ak,key,Integer.class);
            if(count==null){
                redisService.set(ak,key,1);
            }else if(count<maxCount){
                redisService.incr(ak,key);
            }else {
                // Result.error(CodeMsg.ACCESS_LIMIT_REACHED);错误信息必须单独渲染，无页面了
                render(response,CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }

    private void render(HttpServletResponse response, CodeMsg sessionError) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        OutputStream out= null;
        out = response.getOutputStream();
        out.write(JSON.toJSONString(Result.error(sessionError)).getBytes());
        out.flush();
        out.close();
    }

    private MiaoshaUser getMiaoshaUser(HttpServletRequest request, HttpServletResponse response){
        // token可能通过cookie传参，也可能参数传递
        String paramToken=request.getParameter(MiaoshaUserService.COOKIE_NAME_TOKEN);
        String cookieToken=getCookieVale(request,MiaoshaUserService.COOKIE_NAME_TOKEN);
        if(StringUtils.isEmpty(cookieToken)&&StringUtils.isEmpty(paramToken)){
            return null;
        }
        // 参数token的优先级更高
        String token=StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        // 拿到token之后，可以从redis中取出用户信息啦！！
        MiaoshaUser user=userService.getByToken(response,token);
        return user;
    }
    private String getCookieVale(HttpServletRequest request, String cookieToken) {
        Cookie[] cookies=request.getCookies();
        if(cookies==null || cookies.length<=0){
            return null;
        }
        for(Cookie cookie:cookies){
            if(cookie.getName().equals(cookieToken)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
