package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.vo.GoodsVo;
import com.imooc.miaosha.vo.LoginVo;
import org.codehaus.groovy.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

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
    GoodsService goodsService;
    @Autowired
    MiaoshaUserService userService;
    private Logger logger= LoggerFactory.getLogger(GoodsController.class);
    @RequestMapping("/to_list")
    public String toLogin(Model model){
        // 查询商品列表
        List<GoodsVo> goodsVos=goodsService.listGoodsVo();
        model.addAttribute("goodsVos",goodsVos);
        return "goods_list";
    }

    @RequestMapping("/to_detail/{goodsId}")
    public String toLogin(Model model, MiaoshaUser miaoshaUser, @PathVariable("goodsId") int id){
        model.addAttribute("user",miaoshaUser);
        // 查询商品列表
        GoodsVo goods=goodsService.getGoodsVoById(id);
        model.addAttribute("goods",goods);

        int remainSeconds=0;
        int miaoshaStatus=2;// 0还没开始，1正在进行，2已经结束

        long startAt=goods.getStartDate().getTime();
        long endAt=goods.getEndDate().getTime();
        long now=System.currentTimeMillis();

        if(now<startAt){//还没开始，倒计时
            miaoshaStatus=0;
            // 这里int强转需要注意后面的数据全部放入到小括号中，否则得到的竟然是个负数
            remainSeconds=(int)((startAt-now)/1000);
        }else if(now>endAt){// 秒杀已经结束
            remainSeconds=-1;
            miaoshaStatus=2;
        }else {
            miaoshaStatus=1;
            remainSeconds=0;
        }
        model.addAttribute("remainSeconds",remainSeconds);
        model.addAttribute("miaoshaStatus",miaoshaStatus);
        return "goods_detail";
    }

}
