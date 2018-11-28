package cn.e3mall.jedis;

import cn.e3mall.common.jedis.JedisClient;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/11/28 22:29
 **/
public class JedisClientTest {

    @Test
    public void test(){
        // 初始化spring容器
        ApplicationContext ctx=new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
        // 从容器中获取JedisClient对象
        JedisClient jedisClient=ctx.getBean(JedisClient.class);
        System.out.println(jedisClient.get("k3"));
    }
}
