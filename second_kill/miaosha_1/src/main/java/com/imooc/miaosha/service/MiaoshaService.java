package com.imooc.miaosha.service;

import com.imooc.miaosha.domain.Goods;
import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.redis.MiaoshaKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if(isSuccess){
            return orderService.createOrder(user,goodsVo);
        }else{
            // 添加一个是否已经卖完了的内存标记
            setGoodsOver(goodsVo.getId());
            return null;
        }
    }


    public long getMiaoshaResult(long userId, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId,goodsId);
        if(order!=null){
            return order.getOrderId();
        }else{// 可能是秒杀不成功，也可能是排队中
            // 获取是否已经卖完的内存标记，判断是否秒杀不成功还是排队中
            boolean isOver=getGoodsOver(goodsId);
            if(isOver){
                return -1;
            }else {
                return 0;
            }
        }
    }

    private boolean getGoodsOver(long goodsId) {
        // redis中如果已经存在这个key，说明商品已经卖完啦
        return redisService.exists(MiaoshaKey.isGoodsOver,goodsId+"");
    }

    public void setGoodsOver(long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver,goodsId+"",true);
    }
}
