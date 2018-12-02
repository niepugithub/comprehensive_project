package cn.e3mall.search.controller;

import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/12/2 12:19
 **/
@Controller
public class SearchController {
    @Autowired
    private SearchService searchService;

    @Value("${SEARCH_RESULT_ROWS}")
    private  int rows;
    @RequestMapping("/search")
    public String searchItems(String keyword,@RequestParam(defaultValue = "1") Integer page,
                              Model model) throws Exception {
        // 除了tomcat-maven-Plugin设置 编码，这里也可以强行转化字符编码
        // keyword=new String(keyword.getBytes("ISO-8859-1"),"UTF-8");
        // 查询商品列表
        SearchResult result = searchService.search(keyword,page,rows);
        // 把结果传递给页面
        model.addAttribute("query",keyword);
        model.addAttribute("totalPages",result.getTotalPages());
        model.addAttribute("page",page);
        model.addAttribute("recourdCount",result.getRecordCount());
        model.addAttribute("itemList",result.getItemList());
        return "search";
    }
}
