package cn.e3mall.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/** 用户登录处理
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/12/17 22:19
 **/
@Controller
public class LoginController {
    @RequestMapping("/page/login")
    public String showLogin(){
        return "login";
    }
}
