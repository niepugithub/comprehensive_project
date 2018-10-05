package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.vo.LoginVo;
import org.codehaus.groovy.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/4 20:17
 **/
@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    MiaoshaUserService userService;
    private Logger logger= LoggerFactory.getLogger(GoodsController.class);
    @RequestMapping("/to_list")
    public String toLogin(Model model,
           @CookieValue(value = MiaoshaUserService.COOKIE_NAME_TOKEN,required = false)String cookieToken,
           @RequestParam(value = MiaoshaUserService.COOKIE_NAME_TOKEN,required = false)String paramToken){
        // token可能通过cookie传参，也可能参数传递
        if(StringUtils.isEmpty(cookieToken)&&StringUtils.isEmpty(paramToken)){
            return "login";
        }
        // 参数token的优先级更高
        String token=StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        // 拿到token之后，可以从redis中取出用户信息啦！！
        MiaoshaUser user=userService.getByToken(token);
        model.addAttribute("user",user);
        return "goods_list";
    }
}
