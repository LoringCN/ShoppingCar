package cn.huace.message.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 封装寻车App操作人员处理收集购物车结果数据
 * Created by yld on 2017/10/13.
 */
@Data
public class ShopCarHandleRequest implements Serializable{
    private static final long serialVersionUID = 2171971336298049547L;
    private String devId;
    /**
     * 处理状态
     */
    private Integer handle;
    private String reason;
}
