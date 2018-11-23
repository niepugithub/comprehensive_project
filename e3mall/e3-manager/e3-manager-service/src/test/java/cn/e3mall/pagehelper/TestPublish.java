package cn.e3mall.pagehelper;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestPublish {

	@Test
	public void publishService() throws Exception {
		// 靠这里的test方法启动spring容器，照样好使！！！不必非得使用tomcat启动spring容器
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
		System.out.println("服务已经启动。。。。");
		System.in.read();
		System.out.println("服务已经关闭");
		
	}
}
