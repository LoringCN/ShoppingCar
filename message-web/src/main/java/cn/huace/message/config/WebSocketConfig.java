package cn.huace.message.config;

import cn.huace.message.handler.TextMessageWebsocketHanlder;
import cn.huace.message.interceptor.MessageHandshakeInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Created by yld on 2017/10/15.
 */
@Slf4j
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(textMessageWebsocketHanlder(),"/trace")
                .setAllowedOrigins("*")
                .addInterceptors(messageHandshakeInterceptor());
    }
    @Bean
    public MessageHandshakeInterceptor messageHandshakeInterceptor(){
        return new MessageHandshakeInterceptor();
    }
    @Bean
    public TextMessageWebsocketHanlder textMessageWebsocketHanlder(){
        return new TextMessageWebsocketHanlder();
    }
}
