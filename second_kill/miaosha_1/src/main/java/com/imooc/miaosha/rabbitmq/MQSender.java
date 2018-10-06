package com.imooc.miaosha.rabbitmq;

import com.imooc.miaosha.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/6 19:16
 **/
@Service
public class MQSender {
    private static final Logger log= LoggerFactory.getLogger(MQSender.class);
    @Autowired
    AmqpTemplate amqpTemplate;
    public void sendMsg(Object msg){
        log.info("发送的消息====="+msg);
        amqpTemplate.convertAndSend(MQConfig.QUEUE_NAME, RedisService.beanToString(msg));
    }

    public void sendTopic(Object obj){
        String msg=RedisService.beanToString(obj);
        log.info("sendTopic====="+msg);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key1",msg+"1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key2",msg+"2");
    }
    public void sendFanout(Object obj){
        String msg=RedisService.beanToString(obj);
        log.info("sendFanout====="+msg);
        // 这里虽然没有routingKey了，还是需要空字符串占位，否则消息发布不成功
        amqpTemplate.convertAndSend("fanout_exchange","",msg);
    }
}
