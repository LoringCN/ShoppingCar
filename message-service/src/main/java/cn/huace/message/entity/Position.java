package cn.huace.message.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 封装购物车上传的位置信息
 * Created by yld on 2017/10/19.
 */
@Data
public class Position implements Serializable{
    private static final long serialVersionUID = -2442231283301947905L;

    private String devId;
    private Integer shopId;
    private String x;
    private String y;
    private Byte offset;
    private Byte floor;
    private String carStatus;
    private String bt_id;
}
