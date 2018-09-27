package com.imooc.miaosha.dao;

import com.imooc.miaosha.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/9/27 22:42
 **/
@Mapper
public interface UserDao {
    @Select("select * from user where id=#{id}")
    public User getById(@Param("id")int id);

    @Insert("insert into user (name) values (#{name})")
    public int insert(User user);
}
