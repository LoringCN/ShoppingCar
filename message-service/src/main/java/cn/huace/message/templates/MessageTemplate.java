package cn.huace.message.templates;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by yld on 2017/10/19.
 */
@Data
public class MessageTemplate<T> implements Serializable{
    private static final long serialVersionUID = 4337066647653541145L;
    private String type;
    private T content;
}
