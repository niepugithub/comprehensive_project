package com.imooc.miaosha.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/6 19:09
 **/
@Configuration
public class MQConfig {
    public static final String QUEUE_NAME="queue";
    public static final String TOPIC_QUEUE1="topic.queue1";
    public static final String TOPIC_QUEUE2="topic.queue2";
    public static final String TOPIC_EXCHANGE="topicExchange";
    public static final String ROUTING_KEY1="topic.key1";
    public static final String ROUTING_KEY2="topic.#";

    /*
     * direct模式
     * */
    @Bean
    public Queue queue(){
        Queue queue=new Queue(QUEUE_NAME);
        return queue;
    }

    /*
    * 先将信息发送到交换机
    * 交换机模式
    **/
    @Bean
    public Queue topicQueue1(){
        return new Queue(TOPIC_QUEUE1,true);
    }
    @Bean
    public Queue topicQueue2(){
        return new Queue(TOPIC_QUEUE2,true);
    }

    @Bean
    public TopicExchange topicExchange(){
        TopicExchange topicExchange=new TopicExchange(TOPIC_EXCHANGE);
        return topicExchange;
    }

    @Bean
    public Binding topicBind1(){
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(ROUTING_KEY1);
    }

    @Bean
    public Binding topicBind2(){
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(ROUTING_KEY2);
    }

    /*
    * fanout模式：广播模式，也就是发布订阅模式吧，不需要制定routingKey，也就是绑定时候没有with
    * 交换机
    * */
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange("fanout_exchange");
    }
    @Bean
    public Binding fanoutBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
    }
    @Bean
    public Binding fanoutBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
    }

}
