package cn.e3mall.search.mapper;

import cn.e3mall.common.pojo.SearchItem;

import java.util.List;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/12/1 21:02
 **/
public interface ItemMapper {
    List<SearchItem> getItemList();
}
