package cn.huace.message.templates;

import cn.huace.message.entity.ShopCarHandleRequest;

/**
 * Created by yld on 2017/10/20.
 */
public class ShopCarHandleResultTemplate extends MessageTemplate<ShopCarHandleRequest>{
    private static final long serialVersionUID = 1416165332718539561L;

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public ShopCarHandleRequest getContent() {
        return super.getContent();
    }

    @Override
    public void setType(String type) {
        super.setType(type);
    }

    @Override
    public void setContent(ShopCarHandleRequest content) {
        super.setContent(content);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    protected boolean canEqual(Object other) {
        return super.canEqual(other);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
