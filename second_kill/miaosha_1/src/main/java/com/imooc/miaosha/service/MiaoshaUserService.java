package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.MiaoshaUserDao;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/4 21:29
 **/
@Service
public class MiaoshaUserService {
    @Autowired
    MiaoshaUserDao miaoshaUserDao;
    public MiaoshaUser getById(Long id){
        return miaoshaUserDao.getById(id);
    }

    public CodeMsg login(LoginVo loginVo){
        if(loginVo==null){
            return CodeMsg.SERVER_ERROR;
        }
        String mobile=loginVo.getMobile();
        // 判断手机号是否存在
        MiaoshaUser miaoshaUser = getById(Long.parseLong(mobile));
        if(miaoshaUser==null){
            return CodeMsg.MOBILE_NOT_EXIST;
        }
        // 验证密码
        String password= loginVo.getPassword();
        String saltDB= miaoshaUser.getSalt();
        String calcPass = MD5Util.formPassDBPass(password,saltDB);
        // 比较计算得到的密码和数据库中查询得到的密码是否一致
        if(!calcPass.equals(miaoshaUser.getPassword())){// 密码错误
            return CodeMsg.PASSWORD_ERROR;
        }
        return CodeMsg.SUCCESS;
    }
}
