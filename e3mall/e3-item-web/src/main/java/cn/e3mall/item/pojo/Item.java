package cn.e3mall.item.pojo;

import cn.e3mall.pojo.TbItem;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/12/16 14:06
 **/
public class Item extends TbItem{

    // 通过父对象构建子对象
    public Item(TbItem tbItem) {
        this.setId(tbItem.getId());
        this.setTitle(tbItem.getTitle());
        this.setSellPoint(tbItem.getSellPoint());
        this.setPrice(tbItem.getPrice());
        this.setNum(tbItem.getNum());
        this.setBarcode(tbItem.getBarcode());
        this.setImage(tbItem.getImage());
        this.setCid(tbItem.getCid());
        this.setStatus(tbItem.getStatus());
        this.setCreated(tbItem.getCreated());
        this.setUpdated(tbItem.getUpdated());
    }

    // 让页面上的item.images能够获取到值，必须在对应的POJO中添加上这个属性和get方法

    public String[] getImages(){
        String image2 = this.getImage();
        if (image2 != null && !"".equals(image2)) {
            String[] strings = image2.split(",");
            return strings;
        }
        return null;
    }
}
