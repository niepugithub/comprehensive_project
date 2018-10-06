package com.imooc.miaosha.dao;

import com.imooc.miaosha.domain.Goods;
import com.imooc.miaosha.domain.MiaoshaGoods;
import com.imooc.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/5 17:14
 **/
@Mapper
public interface GoodsDao {
    // 两个表中的数据查询出来，需要做联合查询
    @Select("select g.*,mg.stock_count,mg.start_date,mg.end_date,mg.miaosha_price from miaosha_goods mg left join goods g on mg.goods_id=g.id")
    List<GoodsVo> listGoodsVo();
    @Select("select g.*,mg.stock_count,mg.start_date,mg.end_date,mg.miaosha_price from miaosha_goods mg left join goods g on mg.goods_id=g.id where g.id=#{goodsId}")
    GoodsVo getGoodsVoById(@Param("goodsId") int id);
    // goodsId是goods对象的一个属性，这么写也是可以传递参数的！！！
    // 防止超卖，判断库存大于0才OK，利用数据库的锁防止多个线程同时修改库存！！
    // userId_goodsId利用数据库唯一索引，防止用户秒杀到两个商品；
    @Update("update miaosha_goods set stock_count=stock_count-1 where goods_id=#{goodsId} and stock_count>0")
    int reduceStock(MiaoshaGoods goods);
}
