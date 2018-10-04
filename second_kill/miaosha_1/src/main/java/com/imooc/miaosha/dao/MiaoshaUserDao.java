package com.imooc.miaosha.dao;

import com.imooc.miaosha.domain.MiaoshaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/4 21:23
 **/
@Mapper
public interface MiaoshaUserDao {

    @Select("select * from miaosha_user where id=#{id}")
    public MiaoshaUser getById(@Param("id")Long id);
}
