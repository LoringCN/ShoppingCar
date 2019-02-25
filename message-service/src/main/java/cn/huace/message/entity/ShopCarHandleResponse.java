package cn.huace.message.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 反馈购物车处理结果
 * Created by yld on 2017/10/23.
 */
@Data
public class ShopCarHandleResponse implements Serializable{
    private static final long serialVersionUID = -3575913155398610150L;
    private String type;
    private Integer status;
    private String devId;
    private String floorName;

    @Override
    public String toString() {
        return "{\"type\":\""+type+"\",\"status\":"+status+",\"devId\":\""+devId+"\",\"floorName\":\"" + floorName + "\"}";
    }
}
