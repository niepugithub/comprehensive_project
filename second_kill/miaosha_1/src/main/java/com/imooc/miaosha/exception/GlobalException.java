package com.imooc.miaosha.exception;

import com.imooc.miaosha.result.CodeMsg;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/5 11:29
 **/
public class GlobalException extends RuntimeException {
    private CodeMsg codeMsg;
    public GlobalException(){}
    public GlobalException(CodeMsg codeMsg){
        super(codeMsg.toString());
        this.codeMsg=codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }
}
