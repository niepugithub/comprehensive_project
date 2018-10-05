package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.MiaoshaUserDao;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.exception.GlobalException;
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
    // 之前是返回CodeMsg.success，现在是返回业务需要的bool类型
    public boolean login(LoginVo loginVo){
        if(loginVo==null){
            //return CodeMsg.SERVER_ERROR;
            // 之前是返回CodeMsg对象，现在不是了，现在直接抛出异常
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile=loginVo.getMobile();
        // 判断手机号是否存在
        MiaoshaUser miaoshaUser = getById(Long.parseLong(mobile));
        if(miaoshaUser==null){
            // return CodeMsg.MOBILE_NOT_EXIST;
            // 之前是返回CodeMsg对象，现在不是了，现在直接抛出异常
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        // 验证密码
        String password= loginVo.getPassword();
        String saltDB= miaoshaUser.getSalt();
        String calcPass = MD5Util.formPassDBPass(password,saltDB);
        // 比较计算得到的密码和数据库中查询得到的密码是否一致
        if(!calcPass.equals(miaoshaUser.getPassword())){// 密码错误
            //return CodeMsg.PASSWORD_ERROR;
            // 之前是返回CodeMsg对象，现在不是了，现在直接抛出异常
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        return true;
    }
}
