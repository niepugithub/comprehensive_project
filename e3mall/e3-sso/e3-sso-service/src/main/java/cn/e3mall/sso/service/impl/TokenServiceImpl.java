package cn.e3mall.sso.service.impl;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** 根据token查询用户信息
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/12/18 22:24
 **/
@Service
public class TokenServiceImpl implements TokenService{

    @Autowired
    private JedisClient jedisClient;
    @Value("${SESSION_EXPIRE}")
    private Integer SESSION_EXPIRE;
    @Override
    public E3Result getUserByToken(String token) {
        // 根据token从redis获取用户信息
        String json = jedisClient.get("SESSION:"+token);
        // 取不到用户信息，说明登录已经过期，返回登录过期
        if(StringUtils.isBlank(json)){
            return E3Result.build(201,"用户登录已经过期");
        }
        // 取到用户信息，更新token过期时间，返回结果，E3Result，包含TbUser对象
        TbUser tbUser = JsonUtils.jsonToPojo(json,TbUser.class);
        // 更新过期时间
        jedisClient.expire("SESSION:"+token,SESSION_EXPIRE);
        return E3Result.ok(tbUser);
    }
}
