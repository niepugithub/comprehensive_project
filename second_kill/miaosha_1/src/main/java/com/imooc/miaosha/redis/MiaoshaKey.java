package com.imooc.miaosha.redis;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/10/6 13:33
 **/
public class MiaoshaKey extends BasePrefix{

    private MiaoshaKey(String prefix) {
        super(prefix);
    }
    private MiaoshaKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static MiaoshaKey isGoodsOver = new MiaoshaKey(0, "go");
    public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60, "mp");
}
