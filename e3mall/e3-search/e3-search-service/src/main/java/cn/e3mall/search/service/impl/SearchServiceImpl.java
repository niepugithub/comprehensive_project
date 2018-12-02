package cn.e3mall.search.service.impl;

import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.search.dao.SearchDao;
import cn.e3mall.search.service.SearchService;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/12/2 12:00
 **/
@Service
public class SearchServiceImpl implements SearchService{
    @Autowired
    private SearchDao searchDao;
    @Override
    public SearchResult search(String keyword, int page, int rows) throws Exception {
        // 创建SolrQuery查询条件
        SolrQuery solrQuery = new SolrQuery();
        // 设置查询条件
        solrQuery.setQuery(keyword);
        // 设置分页条件
        if(page<=0){
            page=1;
        }
        solrQuery.setStart((page-1)*rows);
        solrQuery.setRows(rows);
        // 设置默认搜索域
        solrQuery.set("df","item_title");
        // 开启高亮显示
        solrQuery.setHighlight(true);
        solrQuery.addHighlightField("item_title");
        solrQuery.setHighlightSimplePre("<em style=\"color:red\">");
        solrQuery.setHighlightSimplePost("</em>");
        // 调用dao执行查询
        SearchResult result = searchDao.search(solrQuery);
        // 计算总页数
        long count = result.getRecordCount();
        int totalPage=(int)(count/rows);
        if(count % rows>0){
            totalPage++;
        }
        // 添加到返回结果
        result.setTotalPages(totalPage);
        return result;
    }
}
