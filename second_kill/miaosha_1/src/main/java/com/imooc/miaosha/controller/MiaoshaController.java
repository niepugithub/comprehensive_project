package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/5 20:26
 **/
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {
    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;
    /*
    * thymeleaf：do_miaosha:143；
    *
    * 页面静态化：miaosha:247；
    * */
    @RequestMapping("/do_miaosha")
    public String doMiaosha(MiaoshaUser user, Model model,int goodsId){
        model.addAttribute("user",user);
        if(user==null){// 没有登录，去登录页面
            return "login";
        }
        // 判断库存
        GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);
        int stock=goodsVo.getStockCount();
        if(stock<=0){//没有库存
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }
        // 判断是否已经秒杀到了，防止同一个用户秒杀多次
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
            return "miaosha_fail";
        }
        // 减库存，下订单，写入秒杀订单；秒杀成功，进入订单信息
        OrderInfo orderInfo = miaoshaService.miaosha(user,goodsVo);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goodsVo);
        return "order_detail";
    }

    // 上面返回页面：这里由于商品详情页是静态化的，ajax提交请求，写跳转页面没有用
    @RequestMapping("/miaosha")
    @ResponseBody
    public Result<OrderInfo> doMiaoshaStatic(MiaoshaUser user, Model model,int goodsId){
        if(user==null){// 没有登录，去登录页面
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        // 判断库存
        GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);
        int stock=goodsVo.getStockCount();
        if(stock<=0){//没有库存
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        // 判断是否已经秒杀到了，防止同一个用户秒杀多次
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        // 减库存，下订单，写入秒杀订单；秒杀成功，进入订单信息
        OrderInfo orderInfo = miaoshaService.miaosha(user,goodsVo);
        return Result.success(orderInfo);
    }

}

