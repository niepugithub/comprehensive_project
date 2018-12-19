package cn.e3mall.sso.controller;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.sso.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 根据token查询用户信息Controller
 *
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/12/18 22:35
 **/
@Controller
public class TokenController {
    @Autowired
    private TokenService tokenService;

    // 下面两种方式都可以解决跨域问题

    // "application/json;charset=utf-8"
    @RequestMapping(value = "/user/token/{token}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getUserByToken(@PathVariable String token, String callback) {
        E3Result result = tokenService.getUserByToken(token);
        //响应结果之前，判断是否为jsonp请求
        if (StringUtils.isNotBlank(callback)) {
            //把结果封装成一个js语句响应
            return callback + "(" + JsonUtils.objectToJson(result) + ");";
        }
        return JsonUtils.objectToJson(result);
    }
//    @RequestMapping(value = "/user/token/{token}")
//    @ResponseBody
//    public Object getUserByToken(@PathVariable String token, String callback) {
//        E3Result result = tokenService.getUserByToken(token);
//        //响应结果之前，判断是否为jsonp请求
//        if (StringUtils.isNotBlank(callback)) {
//            //把结果封装成一个js语句响应
//            MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
//            mappingJacksonValue.setJsonpFunction(callback);
//            return mappingJacksonValue;
//        }
//        return result;
//    }
}
