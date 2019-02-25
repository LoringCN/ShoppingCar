package cn.huace.message.templates;

import cn.huace.message.entity.LocationMessage;
import lombok.Data;


/**
 * Created by yld on 2017/10/19.
 */
public class SingleMessageTemplate extends MessageTemplate<LocationMessage>{
    private static final long serialVersionUID = 8117339917524444731L;

    @Override
    public void setContent(LocationMessage content) {
        super.setContent(content);
    }

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public LocationMessage getContent() {
        return super.getContent();
    }

    @Override
    public void setType(String type) {
        super.setType(type);
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
