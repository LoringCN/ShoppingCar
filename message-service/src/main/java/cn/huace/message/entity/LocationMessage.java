package cn.huace.message.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 用于封装推送单个消息，包括：收集消息、告警消息等
 * 使用时机：
 *  每次购物车上报的购物车待收集或告警信息，只要在websocket可以
 *  正常使用的情况下，都会使用该类封装消息内容
 *
 * Created by yld on 2017/10/13.
 */
@Data
public class LocationMessage implements Serializable{
    private static final long serialVersionUID = 123434081546689378L;
    private String carStatus;
    //楼层
    private String floorName;
    //购物车设置ID
    private String devId;
    private String x;
    private String y;
    private Integer shopId;
}
