package cn.e3mall.service;

import cn.e3mall.common.pojo.EasyUITreeNode;

import java.util.List;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/11/17 21:37
 **/
public interface ItemCatService {

    List<EasyUITreeNode> getItemCatList(long parentId);
}
