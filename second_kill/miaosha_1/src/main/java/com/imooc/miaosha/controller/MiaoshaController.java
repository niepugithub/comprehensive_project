package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.rabbitmq.MQSender;
import com.imooc.miaosha.rabbitmq.MiaoshaMessage;
import com.imooc.miaosha.redis.GoodsKey;
import com.imooc.miaosha.redis.MiaoshaKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/5 20:26
 **/
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean{
    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;
    private HashMap<Long, Boolean> localOverMap =  new HashMap<Long, Boolean>();
    /*
    * thymeleaf：do_miaosha:143；
    *
    * 页面静态化：miaosha:247；
    * 页面静态化+rabbit:370
    *
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
    // 隐藏秒杀地址
    @RequestMapping("/{path}/miaosha")
    @ResponseBody
    public Result<Integer> doMiaoshaStatic(MiaoshaUser user,@PathVariable("path")String path, long goodsId){
        if(user==null){// 没有登录，去登录页面
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        // 验证path
        boolean check = miaoshaService.checkPath(user,goodsId,path);
        if(!check){
            return Result.error(CodeMsg.REQUEST_IILEGAL);
        }
        //内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if(over) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        // 从redis中获取库存信息，而不是直接查询数据库了，之前是直接查询数据库
        long stock=redisService.decr(GoodsKey.getMiaoshaGoodsStock,goodsId+"");
        if(stock<0){//库存以及小于0了，直接返回秒杀失败
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        // 判断是否已经秒杀到了，防止同一个用户秒杀多次
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        // 入队：
        MiaoshaMessage mm=new MiaoshaMessage();
        mm.setGoodsId(goodsId);
        mm.setMiaoshaUser(user);
        mqSender.sendMiaoshaMessage(mm);
        // 将消息写入到rabbitmq中，写入订单就不是这里的事了！！获取到消息的时候再写入订单就好了
        // 返回0表示排队中
        return Result.success(0);
    }

    /*
     * 系统初始化，将库存放入到redis中，这样防止查询数据库了
     * */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVos = goodsService.listGoodsVo();
        if(goodsVos==null ||goodsVos.size()<=0){
            return;
        }
        for(GoodsVo goodsVo:goodsVos){
            // 将秒杀商品的数量放入到redis中，key是秒杀商品的id+其他防重字段
            redisService.set(GoodsKey.getMiaoshaGoodsStock,goodsVo.getId()+"",goodsVo.getStockCount());
            localOverMap.put(goodsVo.getId(), false);
        }
    }

    /*
    * 如果秒杀成功，返回orderId
    * 库存不足：-1
    * 排队中：0
    * */
    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(MiaoshaUser user, Model model,int goodsId){
        model.addAttribute("user",user);
        if(user==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long orderId = miaoshaService.getMiaoshaResult(user.getId(),goodsId);
        return Result.success(orderId);
    }

    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(MiaoshaUser user, Model model,int goodsId,
                         @RequestParam(value="verifyCode", defaultValue="0")int verifyCode) {
        // 防止用户不输验证码出错，MethodArgumentTypeMismatchException
        model.addAttribute("user", user);
        if (user == null) {// 没有登录，去登录页面
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        // 验证前端传递过来的验证码是否正确
        boolean check=miaoshaService.checkVerifyCode(user,goodsId,verifyCode);
        if(!check){
            return Result.error(CodeMsg.REQUEST_IILEGAL);
        }
        String path = miaoshaService.createPath(user,goodsId);
        return Result.success(path);
    }
    @RequestMapping(value = "/verifyCode",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getVerifyCode(HttpServletResponse response,MiaoshaUser user, int goodsId) {
        if (user == null) {// 没有登录，去登录页面
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        BufferedImage image = miaoshaService.createMiaoshaVerify(user,goodsId);
        // 将图片返回到客户端
        OutputStream outputStream=null;
        try {
            outputStream=response.getOutputStream();
            ImageIO.write(image,"JPEG",outputStream);
            outputStream.flush();
            outputStream.close();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }
}

