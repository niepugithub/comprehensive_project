package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.GoodsDao;
import com.imooc.miaosha.domain.Goods;
import com.imooc.miaosha.domain.MiaoshaGoods;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/5 17:14
 **/
@Service
public class GoodsService {
    @Autowired
    private GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoById(long id) {
        return goodsDao.getGoodsVoById(id);
    }

    public boolean reduceStock(GoodsVo goodsVo) {
         MiaoshaGoods g= new MiaoshaGoods();
         g.setGoodsId(goodsVo.getId());
         g.setStockCount(goodsVo.getStockCount()-1);
         return goodsDao.reduceStock(g)>0;
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoById((int)goodsId);
    }
}
