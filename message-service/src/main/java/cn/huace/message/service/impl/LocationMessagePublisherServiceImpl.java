package cn.huace.message.service.impl;

import cn.huace.message.entity.LocationMessage;
import cn.huace.message.service.LocationMessagePublisherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.jms.Queue;
import javax.jms.Topic;

/**
 * Created by yld on 2017/10/16.
 */
@Slf4j
@Service
public class LocationMessagePublisherServiceImpl implements LocationMessagePublisherService {
    @Autowired
    private Queue queue;
    @Autowired
    private Topic topic;

    private final JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    public LocationMessagePublisherServiceImpl(JmsMessagingTemplate jmsMessagingTemplate) {
        this.jmsMessagingTemplate = jmsMessagingTemplate;
    }

    @Async
    public void publish(LocationMessage message) {
//        log.info("-------> 将要发布的Queue消息为：" + message);
        //发布一对一queue消息
//        jmsMessagingTemplate.convertAndSend(MQDestination.QUEUE_NAME,message);
        jmsMessagingTemplate.convertAndSend(this.queue,message);
    }

    @Override
    public Boolean publishTopicMsg(LocationMessage msg) {
        System.out.println("-------> 将要发布的Topic消息为："+msg);
//        JmsTemplate jmsTemplate = jmsMessagingTemplate.getJmsTemplate();
//        jmsTemplate.setExplicitQosEnabled(true);
//        jmsTemplate.setDeliveryMode(DeliveryMode.PERSISTENT);

        //发布一对多topic消息
        jmsMessagingTemplate.convertAndSend(this.topic,msg);
        return true;
    }
}
