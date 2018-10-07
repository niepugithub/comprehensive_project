package com.imooc.miaosha.rabbitmq;

import com.imooc.miaosha.domain.MiaoshaOrder;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoshaService;
import com.imooc.miaosha.service.OrderService;
import com.imooc.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/6 19:15
 **/
@Service
public class MQReceiver {
    @Autowired
    GoodsService goodsService;
    @Autowired
    MiaoshaService miaoshaService;
    @Autowired
    OrderService orderService;
    private static final Logger log= LoggerFactory.getLogger(MQReceiver.class);
    @RabbitListener(queues = MQConfig.QUEUE_NAME)
    public void receiveMsg1(String message){
        log.info("接收到的消息====="+message);
    }
    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void topicMsg1(String message){
        log.info("receiveMsg22接收到的消息====="+message);
    }
    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void topicMsg2(String message){
        log.info("receiveMsg33接收到的消息====="+message);
    }

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void miaoMsg(String message){
        log.info("receive massage:"+message);
        MiaoshaMessage mm = RedisService.stringToBean(message,MiaoshaMessage.class);
        MiaoshaUser user = mm.getMiaoshaUser();
        long goodsId=mm.getGoodsId();

        // 查询数据库判断库存；这里再查询数据库是没关系的，redis已经过滤了绝大多数访问了
        // 这里只有很少的一部分请求过来，跟秒杀商品数目差不多的请求会过来
        GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);
        int stock=goodsVo.getStockCount();
        if(stock<=0){//没有库存
            return;
        }
        // 判断是否已经秒杀到了，防止同一个用户秒杀多次
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return;
        }
        // 减库存，下订单，写入秒杀订单；秒杀成功，进入订单信息
        miaoshaService.miaosha(user,goodsVo);
    }
}
