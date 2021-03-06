package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.GoodsDao;
import com.imooc.miaosha.dao.OrderDao;
import com.imooc.miaosha.domain.MiaoshaGoods;
import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/5 17:14
 **/
@Service
public class OrderService {
    @Autowired
    OrderDao orderDao;
    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long userId,long goodsId){
        return orderDao.getMiaoshaOrderByGoodsIdUserId(userId,goodsId);
    }
    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goodsVo) {
        OrderInfo orderInfo=new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setGoodsPrice(goodsVo.getMiaoshaPrice());
        orderInfo.setStatus(0);// 订单的五种状态，最好使用枚举表示下，可读性好
        orderInfo.setUserId(user.getId());
        orderDao.insert(orderInfo);
        // userId_goodsId利用数据库唯一索引，防止用户秒杀到两个商品；
        // update判断库存大于0，防止超卖；数据库会禁止多个线程同时修改一条记录的
        MiaoshaOrder miaoshaOrder=new MiaoshaOrder();
        miaoshaOrder.setGoodsId(orderInfo.getGoodsId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(user.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);
        return orderInfo;
    }


    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }
}
