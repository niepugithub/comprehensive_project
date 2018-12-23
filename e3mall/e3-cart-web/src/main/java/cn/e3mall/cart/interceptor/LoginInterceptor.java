package cn.e3mall.cart.interceptor;

import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 用户登录处理拦截器
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/12/23 20:18
 **/
public class LoginInterceptor implements HandlerInterceptor{

    @Autowired
    private TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 前处理，执行handler之前执行此方法。
        //返回true，放行	false：拦截
        //1.从cookie中取token
        String token = CookieUtils.getCookieValue(request, "token");
        //2.如果没有token，未登录状态，直接放行
        if (StringUtils.isBlank(token)) {
            return true;
        }
        //3.取到token，需要调用sso系统的服务，根据token取用户信息
        E3Result e3Result = tokenService.getUserByToken(token);
        //4.没有取到用户信息。登录过期，直接放行。
        if (e3Result.getStatus() != 200) {
            return true;
        }
        //5.取到用户信息。登录状态。
        TbUser user = (TbUser) e3Result.getData();
        //6.把用户信息放到request中。只需要在Controller中判断request中是否包含user信息。放行
        // 加上一个用户登录的标记，这样在controller中判断是否登录就比较简单了；只需判断request中
        // 是否保存有用户信息就好了
        request.setAttribute("user", user);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
