package cn.e3mall.search.message;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.search.mapper.ItemMapper;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.IOException;

/**  监听商品同步信息，将对应的商品信息同步到索引库
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/12/15 14:32
 **/
public class ItemAddMessageListener implements MessageListener{

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private SolrServer solrServer;

    @Override
    public void onMessage(Message message) {
        // 从消息中取出商品id
        TextMessage textMessage = (TextMessage) message;
        String text = null;
        try {
            text = textMessage.getText();
            Long id = new Long(text);
            // 等待e3-manager-service添加商品的事务提交才进行查询，否则查找不到商品信息
            // 可以在这里sleep1秒钟，感觉还是不靠谱啊。
            Thread.sleep(1000);// 在这里等下吧，简单法方便
            // 实际上消息应该是在web层发，这样service层的事务肯定已经提交了；

            // 根据商品id查询商品信息
            SearchItem searchItem = itemMapper.getItemById(id);
            // 创建文档对象，向文档对象中添加域，然后将文档对象添加到索引库
            SolrInputDocument doc = new SolrInputDocument();
            // 向文档对象中添加域
            doc.addField("id", searchItem.getId());
            doc.addField("item_title", searchItem.getTitle());
            doc.addField("item_sell_point", searchItem.getSell_point());
            doc.addField("item_price", searchItem.getPrice());
            doc.addField("item_image", searchItem.getImage());
            doc.addField("item_category_name", searchItem.getCategory_name());
            //把文档对象写入索引库
            solrServer.add(doc);

        } catch (JMSException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
