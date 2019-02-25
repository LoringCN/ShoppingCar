package cn.huace.message.service.impl;

import cn.huace.message.service.MessageService;
import org.springframework.stereotype.Service;

/**
 * Created by yld on 2017/10/16.
 * Date:2017/10/16
 */
@Service
public class MessageServiceImpl implements MessageService {
    @Override
    public Boolean handleMessage(String message) {
        System.out.println("******** 开始处理消息:\n"+ message);
        return true;
    }
}
