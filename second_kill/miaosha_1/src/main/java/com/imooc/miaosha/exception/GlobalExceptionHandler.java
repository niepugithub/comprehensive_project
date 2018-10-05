package com.imooc.miaosha.exception;

import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/5 10:45
 **/
@ControllerAdvice
@ResponseBody //方便输出
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(HttpServletRequest req,Exception e){
        e.printStackTrace();
        if(e instanceof GlobalException){// 如果是自定义全局异常
            GlobalException ex = (GlobalException)e;
            return Result.error(ex.getCodeMsg());
        }else if(e instanceof BindException){
            BindException ex = (BindException)e;
            List<ObjectError> errorList=ex.getAllErrors();
            // 这里只处理第一个error示范下， 具体可以处理所有error
            ObjectError error=errorList.get(0);
            String msg=error.getDefaultMessage();
            return Result.error(CodeMsg.Bind_ERROR.fillArgs(msg));
        }else{
            // 如果不是绑定异常，则返回一个通用异常
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}
