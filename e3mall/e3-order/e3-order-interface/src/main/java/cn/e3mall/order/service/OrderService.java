package cn.e3mall.order.service;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.order.pojo.OrderInfo;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/12/26 22:55
 **/
public interface OrderService {
    E3Result createOrder(OrderInfo orderInfo);
}
