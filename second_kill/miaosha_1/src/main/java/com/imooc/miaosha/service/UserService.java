package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.UserDao;
import com.imooc.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/9/27 22:53
 **/
@Service
public class UserService {
    @Autowired
    private UserDao  userDao;
    public User getUserbyId(int id){
        return userDao.getById(id);
    }

    @Transactional
    public boolean tx(){
        User user1= new User();
        user1.setName("lisi");
        userDao.insert(user1);
        User user2= new User();
        user2.setName("zhangsan");
        userDao.insert(user2);
        return true;
    }
}
