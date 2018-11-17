package cn.e3mall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/11/17 18:15
 **/
@Controller
public class PageController {
    @RequestMapping("/")
    public String showIndex(){
        return "/index";
    }

    @RequestMapping("{page}")
    public String showPage(@PathVariable("page") String page){
        return page;
    }
}
