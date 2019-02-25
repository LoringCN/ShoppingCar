package cn.huace.message.service;


import cn.huace.message.entity.LocationMessage;

/**
 * Created by yld on 2017/10/16.
 */
public interface LocationMessageConsumerService {
    void consume(LocationMessage message);
    void consumeTopMsg1(LocationMessage message);
    void consumeTopMsg2(LocationMessage message);
}
