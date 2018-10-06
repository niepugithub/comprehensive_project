package com.imooc.miaosha.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;


/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/6 19:15
 **/
@Service
public class MQReceiver {
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
}
