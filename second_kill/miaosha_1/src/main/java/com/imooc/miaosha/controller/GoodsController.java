package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.redis.GoodsKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.vo.GoodsDetailVo;
import com.imooc.miaosha.vo.GoodsVo;
import com.imooc.miaosha.vo.LoginVo;
import org.codehaus.groovy.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
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
    @Autowired
    RedisService redisService;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;
    @Autowired
    ApplicationContext applcationContext;
    private Logger logger= LoggerFactory.getLogger(GoodsController.class);
    @RequestMapping(value = "/to_list",produces = "text/html")
    @ResponseBody
    public String list(HttpServletRequest request, HttpServletResponse response, Model model){
        //取缓存，这里的第二个参数没有写，粒度比较大，这里查看所有，没有参数，
        // 如果有参数，可以考虑加参数，变成url级别的缓存
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if(!StringUtils.isEmpty(html)) {
            return html;
        }
        // 查询商品列表
        List<GoodsVo> goodsVos=goodsService.listGoodsVo();
        model.addAttribute("goodsVos",goodsVos);
        //手动渲染页面
        SpringWebContext ctx=new SpringWebContext(request,response,request.getServletContext(),
                request.getLocale(),model.asMap(),applcationContext);
        html=thymeleafViewResolver.getTemplateEngine().process("goods_list",ctx);
        if(!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
        return html;
    }

    // 页面缓存
    @RequestMapping(value="/to_detail/{goodsId}",produces = "text/html")
    @ResponseBody
    public String detail(HttpServletRequest request, HttpServletResponse response,Model model, MiaoshaUser miaoshaUser, @PathVariable("goodsId") int id){
        String html = redisService.get(GoodsKey.getGoodsDetail, ""+id, String.class);
        if(!StringUtils.isEmpty(html)) {
            return html;
        }
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
        //手动渲染页面
        SpringWebContext ctx=new SpringWebContext(request,response,request.getServletContext(),
                request.getLocale(),model.asMap(),applcationContext);
        html=thymeleafViewResolver.getTemplateEngine().process("goods_detail",ctx);
        if(!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsDetail, ""+id, html);
        }
        return html;
    }
    // 页面静态化，只需要调用这个接口返回客户端需要的数据就好了
    @RequestMapping(value="/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detailStatic(MiaoshaUser miaoshaUser, @PathVariable("goodsId") int id){
        // 查询商品列表
        GoodsVo goods=goodsService.getGoodsVoById(id);
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
        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(miaoshaUser);
        vo.setRemainSeconds(remainSeconds);
        vo.setMiaoshaStatus(miaoshaStatus);
        return Result.success(vo);
    }

}
