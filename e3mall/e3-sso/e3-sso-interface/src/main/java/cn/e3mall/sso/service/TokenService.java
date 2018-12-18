package cn.e3mall.sso.service;

import cn.e3mall.common.utils.E3Result;

/** 根据token查询用户信息
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/12/18 22:22
 **/
public interface TokenService {
    E3Result getUserByToken(String token);
}
