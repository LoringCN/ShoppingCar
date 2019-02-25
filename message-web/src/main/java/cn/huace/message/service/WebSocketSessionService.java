package cn.huace.message.service;

import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import java.util.Set;

/**
 * Created by yld on 2017/10/18.
 */
public interface WebSocketSessionService {
    /**
     * 根据唯一标识获取tcp通信session
     */
    Set<ConcurrentWebSocketSessionDecorator> getWebSocketSessionByKey(String key);
}
