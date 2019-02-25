package cn.huace.message.config;

import cn.huace.message.constant.MQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

import javax.jms.Queue;
import javax.jms.Topic;

/**
 * Created by yld on 2017/10/16.
 * Date:2017/10/16
 */
@Configuration
@EnableJms
public class ActiveMQConfifg {

    /*
       activeMQ点对点传输消息队列
     */
    @Bean
    public Queue queue(){
        return new ActiveMQQueue(MQDestination.QUEUE_NAME);
    }
    /*
        activeMQ一对多消息队列
     */
    @Bean
    public Topic topic(){
        return new ActiveMQTopic(MQDestination.TOPIC_NAME);
    }

}
