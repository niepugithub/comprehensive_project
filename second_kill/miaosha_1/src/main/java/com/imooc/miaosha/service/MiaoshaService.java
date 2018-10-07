package com.imooc.miaosha.service;

import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.redis.MiaoshaKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/5 21:46
 **/
@Service
public class MiaoshaService {
    // 这里直接依赖其他service而不是dao；为了方便管理
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goodsVo) {
        // 减少库存，下订单，写入秒杀订单；
        // 这么写很清爽，只有两步！！！容易理解：秒杀也就两步：减库存，生成订单
        boolean isSuccess = goodsService.reduceStock(goodsVo);
        // 只有减库存成功了，才需要生成订单
        if (isSuccess) {
            return orderService.createOrder(user, goodsVo);
        } else {
            // 添加一个是否已经卖完了的内存标记
            setGoodsOver(goodsVo.getId());
            return null;
        }
    }


    public long getMiaoshaResult(long userId, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
        if (order != null) {
            return order.getOrderId();
        } else {// 可能是秒杀不成功，也可能是排队中
            // 获取是否已经卖完的内存标记，判断是否秒杀不成功还是排队中
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private boolean getGoodsOver(long goodsId) {
        // redis中如果已经存在这个key，说明商品已经卖完啦
        return redisService.exists(MiaoshaKey.isGoodsOver, goodsId + "");
    }

    public void setGoodsOver(long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver, goodsId + "", true);
    }

    public boolean checkPath(MiaoshaUser user, long goodsId, String path) {
        if (user == null || path == null) {
            return false;
        }
        String pathOld = redisService.get(MiaoshaKey.getMiaoshaPath, user.getId() + "_" + goodsId, String.class);
        return path.equals(pathOld);
    }

    public String createPath(MiaoshaUser user, int goodsId) {
        String uuid = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisService.set(MiaoshaKey.getMiaoshaPath, user.getId() + "_" + goodsId, uuid);
        return uuid;
    }

    public BufferedImage createMiaoshaVerify(MiaoshaUser user, int goodsId) {
        if (user == null || goodsId <= 0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code，生成表达式
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsId, rnd);
        //输出图片
        return image;
    }

    // 计算验证码中算术表达式的值
    private int calc(String verifyCode) {
        try{
            ScriptEngineManager manager=new ScriptEngineManager();
            ScriptEngine engine= manager.getEngineByName("javascript");
            return (Integer)engine.eval(verifyCode);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }
    private static char[] ops = new char[] {'+', '-', '*'};
    /*
     * + - *
     * */
    // 生成验证码中算术表达式
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1=ops[rdm.nextInt(3)];
        char op2=ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 + num2 + op2 + num3;
        return exp;
    }

    public boolean checkVerifyCode(MiaoshaUser user, int goodsId, int verifyCode) {
        // 参数验证不要忘记！！！！！要养成习惯
        if(user==null || goodsId<=0){
            return false;
        }
        Integer codeOld = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, Integer.class);
        if(codeOld == null || codeOld - verifyCode != 0 ) {
            return false;
        }
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId);
        return true;
    }
}
