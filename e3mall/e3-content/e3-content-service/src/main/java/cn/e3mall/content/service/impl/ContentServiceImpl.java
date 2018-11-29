package cn.e3mall.content.service.impl;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentExample;
import cn.e3mall.pojo.TbContentExample.Criteria;
import cn.e3mall.common.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 内容管理Service
 * <p>Title: ContentServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p>
 *
 * @version 1.0
 */
@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private TbContentMapper contentMapper;

    @Autowired
    JedisClient jedisClient;

    @Value("${CONTENT_LIST}")
    private String CONTENT_LIST;
    @Override
    public E3Result addContent(TbContent content) {
        //将内容数据插入到内容表
        content.setCreated(new Date());
        content.setUpdated(new Date());
        //插入到数据库
        contentMapper.insert(content);
        return E3Result.ok();
    }

    /**
     * 根据内容分类id查询内容列表
     * <p>Title: getContentListByCid</p>
     * <p>Description: </p>
     *
     * @param cid
     * @return
     * @see cn.e3mall.content.service.ContentService#getContentListByCid(long)
     */
    @Override
    public List<TbContent> getContentListByCid(long cid) {
        // 无论是查询缓存还是添加缓存都不应该影业务，不能抛异常，所以try catch，打印日志信息，但是不抛异常
        // 查询缓存
        try {
            // 如果缓存中有直接响应结果；key放置的cid，但是id那么多，需要归类
            String json = jedisClient.hget(CONTENT_LIST,cid+"");
            if(StringUtils.isNotBlank(json)){
                List<TbContent> list = JsonUtils.jsonToList(json,TbContent.class);
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 如果没有查询数据库
        TbContentExample example = new TbContentExample();
        Criteria criteria = example.createCriteria();
        //设置查询条件
        criteria.andCategoryIdEqualTo(cid);
        //执行查询
        List<TbContent> list = contentMapper.selectByExampleWithBLOBs(example);
        // 查询结果放入到缓存中
        try {
            jedisClient.hset(CONTENT_LIST,cid+"", JsonUtils.objectToJson(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
