package cn.huace.message.service;


import cn.huace.message.entity.LocationMessage;

import java.util.concurrent.Future;

/**
 * Created by yld on 2017/10/16.
 */
public interface LocationMessagePublisherService {
    /**
     * 发布消息接口
     * @param message 待发布消息
     */
    void publish(LocationMessage message);
    Boolean publishTopicMsg(LocationMessage msg);
}
