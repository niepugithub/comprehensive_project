package com.imooc.miaosha.dao;

import com.imooc.miaosha.domain.MiaoshaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
    // 不更新的字段没必要写，浪费资源
    @Update("update miaosha_user set password=#{password} where id=#{id}")
    void update(MiaoshaUser toBeUpdate);
}
