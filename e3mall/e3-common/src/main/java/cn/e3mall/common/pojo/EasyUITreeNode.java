package cn.e3mall.common.pojo;

import java.io.Serializable;

/**
 * @description:
 * @author:niepu
 * @version:1.0
 * @date:2018/11/17 21:31
 **/
public class EasyUITreeNode implements Serializable {
    // 服务者和提供者都需要这个pojo，放在common模块中再合适不过了
    private long id;
    private String state;
    private String text;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
