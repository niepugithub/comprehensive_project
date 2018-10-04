package com.imooc.miaosha.controller;

import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.util.ValidatorUtil;
import com.imooc.miaosha.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/4 20:17
 **/
@Controller
@RequestMapping("/login")
public class LoginController {
    @Autowired
    MiaoshaUserService userService;
    private Logger logger= LoggerFactory.getLogger(LoginController.class);

    @RequestMapping("/to_login")
    public String toLogin(){
        return "login";
    }

    // 注意，只要不是返回页面字符串，一般需要加上@ResponseBody注解
    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(LoginVo loginVo){
        logger.info(loginVo.toString());
        // 参数校验
        String passInput=loginVo.getPassword();
        String mobile=loginVo.getMobile();
        if(StringUtils.isEmpty(passInput)){
            return Result.error(CodeMsg.PASSWORD_EMPTY);
        }
        if(StringUtils.isEmpty(mobile)){
            return Result.error(CodeMsg.MOBILE_EMPTY);
        }
        if(!ValidatorUtil.isMobile(mobile)){
            return Result.error(CodeMsg.MOBILE_ERROR);
        }
        // 登录
        CodeMsg cm=userService.login(loginVo);
        if(cm.getCode()==0){
            return Result.success(true);
        }else{
            return Result.error(cm);
        }
    }
}
