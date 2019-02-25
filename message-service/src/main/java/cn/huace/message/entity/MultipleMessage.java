package cn.huace.message.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用于推送既有待收集购物车消息又有报警购物车消息封装，
 * 使用时机：
 *  寻车app断线重连上后，一次性推送所有未收消息
 *
 * Created by yld on 2017/10/19.
 */
@Data
public class MultipleMessage implements Serializable{
    private static final long serialVersionUID = -855479161655300268L;
    private String floorName;
    private List<LocationMessage> gatherList;
    private List<LocationMessage> alarmList;

}
