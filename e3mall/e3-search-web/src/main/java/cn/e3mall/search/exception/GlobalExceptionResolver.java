package cn.e3mall.search.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/12/2 23:00
 **/
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionResolver.class);
    @Override
    public ModelAndView resolveException(HttpServletRequest req, HttpServletResponse resp, Object handler, Exception ex) {
        // 全局异常处理，只有一个
        // dao抛给service，service抛给controller，然后抛给框架
        //打印控制台
        ex.printStackTrace();
        //写日志
        logger.debug("测试输出的日志。。。。。。。");
        logger.info("系统发生异常了。。。。。。。");
        logger.error("系统发生异常", ex);
        //发邮件、发短信
        //使用jmail工具包。发短信使用第三方的Webservice。
        //显示错误页面
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error/exception");
        return modelAndView;
    }
}
