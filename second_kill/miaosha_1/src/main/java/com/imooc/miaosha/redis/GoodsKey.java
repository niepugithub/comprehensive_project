package com.imooc.miaosha.redis;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/6 13:33
 **/
public class GoodsKey extends BasePrefix{

    private GoodsKey(String prefix) {
        super(prefix);
    }

    private GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static GoodsKey getGoodsList = new GoodsKey(60, "gl");
    public static GoodsKey getGoodsDetail = new GoodsKey(60, "gd");
    public static GoodsKey getMiaoshaGoodsStock = new GoodsKey(0, "gs");

}
