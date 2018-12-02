package cn.e3mall.search.service;

import cn.e3mall.common.pojo.SearchResult;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/12/2 11:58
 **/
public interface SearchService {
    SearchResult search(String keyword,int page,int rows) throws Exception;
}
