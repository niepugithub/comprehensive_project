package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.MiaoshaUserDao;
import com.imooc.miaosha.domain.MiaoshaUser;
import com.imooc.miaosha.exception.GlobalException;
import com.imooc.miaosha.redis.MiaoshaUserKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.redis.UserKey;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtil;
import com.imooc.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/4 21:29
 **/
@Service
public class MiaoshaUserService {
    public static final String COOKIE_NAME_TOKEN="token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;
    // 调用别人的service，不能调用别人的dao，因为别人的service可能用到了缓存，如果直接调用别人的dao
    // 那么缓存就没用到了，可能会出现数据一致性问题
    @Autowired
    RedisService redisService;
    public MiaoshaUser getById(Long id){
        MiaoshaUser user=redisService.get(UserKey.getById,id+"",MiaoshaUser.class);
        if(user!=null){
            return user;
        }
        user = miaoshaUserDao.getById(id);
        if(user!=null){
            redisService.set(UserKey.getById,id+"",user);
        }
        return user;
    }

    public boolean updatePassword(String token, long id, String formPass) {
        // 取user
        MiaoshaUser user=getById(id);
        if(user==null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        // 更新数据库；创建新的对象，因为只更新密码字段，没必要用user，去更新所有字段
        MiaoshaUser toBeUpdate=new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassDBPass(formPass, user.getSalt()));
        miaoshaUserDao.update(toBeUpdate);
        //处理缓存
        redisService.delete(MiaoshaUserKey.getById, ""+id);
        user.setPassword(toBeUpdate.getPassword());
        // 更新token这个key对应的值；如果是一般数据，是应该让缓存失效，删除这个缓存
        // 由于缓存的是登录用户信息，永久有效的，而且只是登录的时候才赋值，如果删除了，
        // 用户就只能重新登录了；好像也有道理啊，修改密码后，重新登录!!!
        redisService.set(MiaoshaUserKey.token, token, user);
        return true;
    }

    // 之前是返回CodeMsg.success，现在是返回业务需要的bool类型
    public boolean login(HttpServletResponse response,LoginVo loginVo){
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
        // session功能在此实现；
        // 用户登录成功后，生成一个sessionId或者token标志用户，存入数据库，并返回到客户端
        // 客户端在随后的访问中，都会上传这个token，服务器拿到这个token就可以获取到用户信息

        // 生成cookie，随机的uuid字符串就好了;这个token需要传递到客户端；但是这个uuid属于哪个用户呢？
        // 因此，需要将用户信息写入到redis中；引入redisService，核心就是将用户信息放入到第三方缓存中
        // 代码修改，之前每次都生成新的uuid，导致每次请求就产生一新uuid，这些uuid都是同一个用户的
        // 浪费redis空间，修改，一个用户只生成一个uuid，后面将这个uuid当做参数传递就好了
        String token = UUIDUtil.uuid();
        addCookie(response,token,miaoshaUser);
        return true;
    }
    // public 方法首先第一步是要进行参数校验的，养成习惯
    public MiaoshaUser getByToken(HttpServletResponse response,String token) {
        if(StringUtils.isEmpty(token)){
            return null;
        }
        MiaoshaUser user =redisService.get(MiaoshaUserKey.token,token,MiaoshaUser.class);
        // 延长有效期!!!只需要覆盖之前的expireSeconds就好了
        if(user!=null){
            addCookie(response,token,user);
        }
        return user;
    }

    // 实现cookie的有效期是最后一次访问的时间加上30min
    public void addCookie(HttpServletResponse response,String token,MiaoshaUser miaoshaUser){
        // 第三个参数是用户信息
        redisService.set(MiaoshaUserKey.token,token,miaoshaUser);
        Cookie cookie= new Cookie(COOKIE_NAME_TOKEN,token);
        // cookie的过期时间与session一致就好了
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");// 路径就设成网站的根目录
        // 需要将cookie写到客户端，因此需要HttpServletResponse
        response.addCookie(cookie);
        // 可以看到do_login uri的response headers中有cookie头
        // Set-Cookie:token=7c519f3cf9184beb82c00ef515427e70; Max-Age=0; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/
    }
}
