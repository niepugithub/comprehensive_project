package cn.e3mall.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/** 注册功能controller
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/12/16 21:29
 **/
@Controller
public class RegisterController {

    @RequestMapping("/page/register")
    public String showRegister(){
        return "register";
    }
}
