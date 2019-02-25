package cn.huace.message.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * Created by yld on 2017/10/15.
 */
@Slf4j
public class MessageHandshakeInterceptor implements HandshakeInterceptor {
    private static final String SESSION_IDENTIFY_KEY = "shopId";
    @Override
    public boolean beforeHandshake(
            ServerHttpRequest serverHttpRequest,
            ServerHttpResponse serverHttpResponse,
            WebSocketHandler webSocketHandler,
            Map<String, Object> attributes) throws Exception {
        if(serverHttpRequest instanceof ServletServerHttpRequest){
            String shopId = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest().getParameter(SESSION_IDENTIFY_KEY);
            attributes.put(SESSION_IDENTIFY_KEY,shopId);
        }
        log.info("------> map: "+ attributes);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }
}
