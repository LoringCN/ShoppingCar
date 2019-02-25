package cn.huace.message.service.impl;

import cn.huace.message.handler.TextMessageWebsocketHanlder;
import cn.huace.message.service.WebSocketSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import java.util.Map;
import java.util.Set;

/**
 * 用于获取TextMessageWebsocketHanlder中保存的session
 * Created by yld on 2017/10/18.
 */
@Slf4j
@Service
public class WebSocketSessionServiceImpl implements WebSocketSessionService {
    @Autowired
    private TextMessageWebsocketHanlder websocketHanlder;

    @Override
    public Set<ConcurrentWebSocketSessionDecorator> getWebSocketSessionByKey(String key) {
        log.info("********** 要获取WebSocketSession的key = {}",key);
        Map<String,Set<ConcurrentWebSocketSessionDecorator>> sessionMap = websocketHanlder.getSessionMap();
        log.info("********** sessionMap: {}",sessionMap);
        if(!sessionMap.isEmpty() && !StringUtils.isEmpty(key)){
            Set<ConcurrentWebSocketSessionDecorator> sessionSet = sessionMap.get(key);
            return sessionSet;
        }
        return null;
    }
}
