package cn.e3mall.sso.service;

import cn.e3mall.common.utils.E3Result;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/12/16 22:03
 **/
public interface RegisterService {
    E3Result checkData(String param, int type);
}
