package cn.huace.message.service.impl;

import cn.huace.message.constant.MQDestination;
import cn.huace.message.constant.TypeValues;
import cn.huace.message.entity.LocationMessage;
import cn.huace.message.service.LocationMessageConsumerService;
import cn.huace.message.service.WebSocketSessionService;
import cn.huace.message.templates.SingleMessageTemplate;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import java.io.IOException;
import java.util.Set;

/**
 * Created by yld on 2017/10/16.
 */
@Slf4j
@Service
public class LocationMessageConsumerServiceImpl implements LocationMessageConsumerService {
    @Autowired
    private WebSocketSessionService sessionService;

    @JmsListener(destination = MQDestination.QUEUE_NAME,concurrency = "10")
    public void consume(LocationMessage message) {
        log.info("---------> consume 收到Queue消息："+ message);
        if(message != null){
            String shopId = String.valueOf(message.getShopId());
            Set<ConcurrentWebSocketSessionDecorator> sessionSet = sessionService.getWebSocketSessionByKey(shopId);
            if(sessionSet != null && sessionSet.size() > 0){
                SingleMessageTemplate template = new SingleMessageTemplate();
                String pushMsgType = TypeValues.LIST_CAR_REQ;
                template.setType(pushMsgType);
                template.setContent(message);
                JsonConfig config = new JsonConfig();
                config.setExcludes(new String[]{"shopId"});
                String pushMsg = JSONObject.fromObject(template,config).toString();

                //给该超市所有寻车app管理员推送消息
                for(ConcurrentWebSocketSessionDecorator session:sessionSet){
                    try {
                        session.sendMessage(new TextMessage(pushMsg));
                        log.info("******* 推送消息成功！msg = {}",pushMsg);
                    } catch (IOException e) {
                        log.error("******* 推送消息出错！！",e);
                    }
                }
            }
        }
    }

//    @JmsListener(destination = MQDestination.TOPIC_NAME)
    public void consumeTopMsg1(LocationMessage message){
        System.out.println("---------> consumeTopMsg1收到Topic消息："+message);
    }
//    @JmsListener(destination = MQDestination.TOPIC_NAME)
    public void consumeTopMsg2(LocationMessage message){
        System.out.println("---------> consumeTopMsg2收到Topic消息："+message);
    }
}
