package com.imooc.miaosha.service;

import com.imooc.miaosha.domain.Goods;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
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
    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goodsVo) {
        // 减少库存，下订单，写入秒杀订单；
        // 这么写很清爽，只有两步！！！容易理解：秒杀也就两步：减库存，生成订单
        goodsService.reduceStock(goodsVo);
        OrderInfo orderInfo = orderService.createOrder(user,goodsVo);
        return orderInfo;
    }





}
