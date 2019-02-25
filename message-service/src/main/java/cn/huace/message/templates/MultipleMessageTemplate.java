package cn.huace.message.templates;

import cn.huace.message.entity.MultipleMessage;
import lombok.Data;

import java.util.List;

/**
 * Created by yld on 2017/10/19.
 */
public class MultipleMessageTemplate extends MessageTemplate<List<MultipleMessage>>{
    private static final long serialVersionUID = 6375412567132277135L;

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public List<MultipleMessage> getContent() {
        return super.getContent();
    }

    @Override
    public void setType(String type) {
        super.setType(type);
    }

    @Override
    public void setContent(List<MultipleMessage> content) {
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
