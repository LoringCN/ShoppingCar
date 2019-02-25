package cn.huace.message.controller;

import cn.huace.common.bean.HttpResult;
import cn.huace.message.constant.TypeValues;
import cn.huace.message.entity.LocationMessage;
import cn.huace.message.service.WebSocketSessionService;
import cn.huace.message.templates.SingleMessageTemplate;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import java.io.IOException;
import java.util.Set;

/**
 * Created by yld on 2018/1/10.
 */
@Slf4j
@RestController
@RequestMapping(value = "/message")
public class MessageController {
    @Autowired
    private WebSocketSessionService sessionService;

    @RequestMapping(value = "/push",method = RequestMethod.POST)
    public HttpResult manualPushMessage(@RequestBody LocationMessage locationMessage){
        log.info("********* 调用方法：manualPushMessage,主动推送数据~~，locationMessage = {}",locationMessage);
        if(locationMessage.getShopId() == null){
            return HttpResult.createFAIL("shopId不能为空！");
        }
        Set<ConcurrentWebSocketSessionDecorator> sessionSet
                = sessionService.getWebSocketSessionByKey(String.valueOf(locationMessage.getShopId()));
        if(sessionSet != null && sessionSet.size() > 0){
            SingleMessageTemplate template = new SingleMessageTemplate();
            template.setType(TypeValues.LIST_CAR_REQ);
            template.setContent(locationMessage);
            JsonConfig config = new JsonConfig();
            config.setExcludes(new String[]{"shopId"});
            String pushMsg = JSONObject.fromObject(template,config).toString();
            //给该超市所有寻车app管理员推送消息
            for(ConcurrentWebSocketSessionDecorator session:sessionSet){
                try {
                    session.sendMessage(new TextMessage(pushMsg));
                    log.info("******* 手动推送消息成功！msg = {}",pushMsg);
                } catch (IOException e) {
                    log.error("******* 手动推送消息出错！！",e);
                }
            }
            return HttpResult.createSuccess("手动推送消息成功！",pushMsg);
        }
        return HttpResult.createFAIL("手动推送消息失败！");
    }
}
