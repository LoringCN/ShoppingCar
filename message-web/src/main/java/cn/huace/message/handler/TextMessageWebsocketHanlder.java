package cn.huace.message.handler;

import cn.huace.message.constant.TypeValues;
import cn.huace.message.entity.Location;
import cn.huace.message.entity.LocationMessage;
import cn.huace.message.entity.MultipleMessage;
import cn.huace.message.entity.ShopCarHandleResponse;
import cn.huace.message.enums.HandleCode;
import cn.huace.message.enums.ShopCarStatus;
import cn.huace.message.service.LocationService;
import cn.huace.message.templates.MultipleMessageTemplate;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yld on 2017/10/16.
 */
@Slf4j
public class TextMessageWebsocketHanlder extends TextWebSocketHandler {

    @Autowired
    private LocationService locationService;

    //ConcurrentWebSocketSessionDecorator会将响应消息先加入队列
    private static Map<String,Set<ConcurrentWebSocketSessionDecorator>> sessionMap = new ConcurrentHashMap<>();
    //记录ping信息
    private static Map<String,Integer> pingMap = new ConcurrentHashMap<>();
    //记录活跃session通信时间
    private static Map<String,Long> activeSessionMap = new HashMap<>();

    private static final String SESSION_IDENTIFY_KEY = "shopId";
    private static final Integer BUFFER_SIZE_LIMIT = Integer.MAX_VALUE;
    //关闭session前连续ping不同最大次数
    private static final Integer MAX_PING_COUNT_BEFORE_CLOSE_SESSION = 6;
    //最大不活跃时间，40分钟，单位：毫秒
    private static final Long MAX_INACTIVE_TIME = 40 * 60 * 1000L;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("----------> websocket已连接,sessionId = "+ session.getId());
        String shopId = getShopIdFromSession(session);
        if(shopId != null){
            //考虑到一个超市可能有多个寻车app管理员,所以map使用set集合为value值
            Set<ConcurrentWebSocketSessionDecorator> sessionSet = sessionMap.get(shopId);
            if(sessionSet == null){
                sessionSet = new HashSet<>();
            }

            ConcurrentWebSocketSessionDecorator sessionDecorator
                    = new ConcurrentWebSocketSessionDecorator(session,0,BUFFER_SIZE_LIMIT);

            sessionSet.add(sessionDecorator);
            sessionMap.put(shopId,sessionSet);
            activeSessionMap.put(shopId+":"+session.getId(),System.currentTimeMillis());
            /*
                连接上后检查是否有购物车数据需要推送
            */
            List<Location> locationList = locationService.findAllRequirePushLocations(Integer.parseInt(shopId));
            MultipleMessageTemplate messageTemplate = convertLocationList2MultipleMessageTemplate(locationList);
            JsonConfig config = new JsonConfig();
            config.setExcludes(new String[]{"shopId","carStatus"});
            String pushMessage = JSONObject.fromObject(messageTemplate,config).toString();
            //推送消息
            sendMessageToUser(pushMessage,sessionDecorator);
        }
    }

    //接收文本消息
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("----------> 收到客户端发来消息："+message.getPayload());
        String shopId = getShopIdFromSession(session);
        //更新通信时间
        activeSessionMap.put(shopId+":"+session.getId(),System.currentTimeMillis());
        ShopCarHandleResponse respMsg = new ShopCarHandleResponse();
        try {
            String sessionKey = String.valueOf(session.getAttributes().get(SESSION_IDENTIFY_KEY));
            String payload = message.getPayload();
            JSONObject payloadJson = JSONObject.fromObject(payload);
            String reqType = payloadJson.getString("type");
            //推送消息反馈
            boolean feedback = handlePushMessageFeedback(payloadJson,reqType);
            if (feedback){
                return;
            }
            respMsg.setFloorName(payloadJson.getString("floorName"));
            String respType = getResponseTypeByRequestType(reqType);
            respMsg.setType(respType);
            String devId = payloadJson.getJSONObject("content").has("devId")?"devId":"devIdList";
            respMsg.setDevId(payloadJson.getJSONObject("content").getString(devId));

            Boolean result = locationService.handleMessage(payload);
            if(!result){
                respMsg.setStatus(HandleCode.FAILURE.getValue());
                //操作失败时，只返回消息给发送消息的寻车app管理员
                sendMessageToUser(respMsg.toString(),session);
            }else {
                respMsg.setStatus(HandleCode.SUCCESS.getValue());
                //成功时,广播消息给该超市下所有《在线》寻车app管理员
                broadCastMessage(respMsg.toString(),sessionKey);
            }
        } catch (Exception e) {
            log.error("********* 处理消息出错，error: {}",e);
            respMsg.setStatus(HandleCode.FAILURE.getValue());
            sendMessageToUser(respMsg.toString(),session);
        }

    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        log.info("----------> 来自客户端：{}，sessionId: {} , pongMessage：{}" ,session.getRemoteAddress().toString(),session.getId(),message.getPayload());
        //ping通之后，ping次数归零
        pingMap.put(session.getId(),0);
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("----------> 处理传输错误信息..."+exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("----------> 连接关闭后,开始处理...");
        String shopId = getShopIdFromSession(session);
        Set<ConcurrentWebSocketSessionDecorator> sessionSet = sessionMap.get(shopId);
        Iterator<ConcurrentWebSocketSessionDecorator> iterator = sessionSet.iterator();
        while (iterator.hasNext()){
            ConcurrentWebSocketSessionDecorator sessionDecorator = iterator.next();
            if (sessionDecorator != null){
                WebSocketSession delegate = sessionDecorator.getDelegate();
                if(session == delegate && !delegate.isOpen()){
                    iterator.remove();
                    //移除session对应ping信息
                    pingMap.remove(delegate.getId());
                    activeSessionMap.remove(shopId+":"+delegate.getId());
                }
            }
        }
        //当所有session都被关闭之后，移除set
        if(sessionSet.isEmpty()){
            sessionMap.remove(shopId);
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 供外面服务获取session数据
     */
    public Map<String, Set<ConcurrentWebSocketSessionDecorator>> getSessionMap() {
        return sessionMap;
    }

    /**
     * 定时ping所有存活session
     */
    @Scheduled(fixedRate = 10000)
    public void sendPing(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        log.info("-------> 执行定时ping任务！，time: "+sdf.format(new Date()));
        Collection<Set<ConcurrentWebSocketSessionDecorator>> sessions = sessionMap.values();
        if(!sessions.isEmpty()){
            log.info("*********** 发送心跳！时间：" + sdf.format(new Date()));
            for(Set<ConcurrentWebSocketSessionDecorator> sessionSet:sessions){
                if(sessionSet != null && !sessionSet.isEmpty()){
                    for(ConcurrentWebSocketSessionDecorator session:sessionSet){
                        if(session != null && session.isOpen()){
                            ByteBuffer byteBuffer = ByteBuffer.wrap("ping".getBytes());
                            PingMessage pingMessage = new PingMessage(byteBuffer);
                            try {
                                session.sendMessage(pingMessage);
                                //记录ping次数
                                recordPingCount(session);
                            } catch (IOException e) {
                                log.error("************ 发送ping信息报错！error: {}",e);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 定时检查是否有session可以关闭:
     * 65s检查一次，因为只关闭连续6次ping不通的session
     */
    @Scheduled(fixedRate = 65000)
    public void checkHasSessionClosable(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Collection<Set<ConcurrentWebSocketSessionDecorator>> sessions = sessionMap.values();
        if(!sessions.isEmpty()){
            log.info("*********** 定时检查《未ping通》session！时间：" + sdf.format(new Date()));
            for(Set<ConcurrentWebSocketSessionDecorator> sessionSet:sessions){
                if(sessionSet != null && !sessionSet.isEmpty()){
                    for(ConcurrentWebSocketSessionDecorator session:sessionSet){
                        if(session != null && session.isOpen()){
                            String sessionId = session.getDelegate().getId();
                            Integer pingCount = pingMap.get(sessionId);
                            if(pingCount != null && pingCount > MAX_PING_COUNT_BEFORE_CLOSE_SESSION){
                                //连续6次未ping通
                                try {
                                    session.close();
                                    log.info("********* 关闭6次未ping通WebSocketSession,ID = {}",session.getDelegate().getId());
                                } catch (IOException e) {
                                   log.error("********* 关闭6次未ping通WebSocketSession出错！，error: {}",e);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 定时关闭超过最大不活跃时间的session
     */
    @Scheduled(fixedRate = 20*60*1000)
    public void checkInactiveSession(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("******** 定时检查超时不活跃session！！时间：{}",sdf.format(new Date()));
        if(!activeSessionMap.isEmpty()){
            Iterator<Map.Entry<String,Long>> activeSessionIterator = activeSessionMap.entrySet().iterator();
            while (activeSessionIterator.hasNext()){
                Map.Entry<String,Long> entry = activeSessionIterator.next();
                Long currentTime = System.currentTimeMillis();
                Long lastReceiveMessageTime = entry.getValue();
                String mapKey = entry.getKey();
                String[] keys = mapKey.split(":");
                if(currentTime - lastReceiveMessageTime >= MAX_INACTIVE_TIME){
                    //关闭不活跃session
                    Set<ConcurrentWebSocketSessionDecorator> sessionSet = sessionMap.get(keys[0]);
                    if(sessionSet != null && !sessionSet.isEmpty()){
                        Iterator<ConcurrentWebSocketSessionDecorator> iterator = sessionSet.iterator();
                        while (iterator.hasNext()){
                            ConcurrentWebSocketSessionDecorator sessionDecorator = iterator.next();
                            WebSocketSession session = sessionDecorator.getDelegate();
                            if(keys[1].equals(session.getId())){
                                try {
                                    sessionDecorator.close();
                                    log.info("****** 关闭超时不活跃session成功,sessionId = {}",keys[1]);
                                } catch (IOException e) {
                                    log.error("********* 关闭超时不活跃session失败,sessionId = {},error: = {}",keys[1],e);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * 获取session中shopId
     */
    private String getShopIdFromSession(WebSocketSession session){
        Map<String,Object> attributes = session.getAttributes();
        return String.valueOf(attributes.get(SESSION_IDENTIFY_KEY));
    }
    /**
     * 记录ping次数，检查是否需要关闭session
     */
    private void recordPingCount(ConcurrentWebSocketSessionDecorator session){
        //记录ping次数信息
        String pingMapKey = session.getDelegate().getId();
        Integer pingCount = pingMap.get(pingMapKey);
        pingCount = pingCount == null ? 1 :(pingCount + 1);
        pingMap.put(pingMapKey,pingCount);
        log.info("********** 连续ping数次，pingCount = {}",pingCount);
    }

    /**
     * 将查询出来的数据库中未推送购物车位置信息转化为约定的消息模板
     */
    private MultipleMessageTemplate convertLocationList2MultipleMessageTemplate(List<Location> locationList){
        MultipleMessageTemplate message = new MultipleMessageTemplate();
        message.setType(TypeValues.LIST_ALL_CARS_REQ);
        if(StringUtils.isEmpty(locationList)){
            message.setContent(null);
        }else{
            List<MultipleMessage> messageList = new ArrayList<>();

            //获取楼层信息
            Set<Integer> floorNumSet = new HashSet<>();
            for(Location location:locationList){
                floorNumSet.add(location.getFloor().intValue());
            }
            for(Integer floorNum:floorNumSet){
                MultipleMessage multipleMessage = new MultipleMessage();
                multipleMessage.setFloorName(floorNum+"");
                List<LocationMessage> gatherList = new ArrayList<>();
                List<LocationMessage> alarmList = new ArrayList<>();

                for(Location location:locationList){
                    if(location.getFloor().intValue() == floorNum){
                        LocationMessage locationMessage = new LocationMessage();
                        locationMessage.setFloorName(String.valueOf(location.getFloor().intValue()));
                        locationMessage.setCarStatus(location.getCarStatus());
                        locationMessage.setDevId(location.getDevId());
                        locationMessage.setX(location.getX());
                        locationMessage.setY(location.getY());

                        if(ShopCarStatus.GATHER.getValue().equals(location.getCarStatus())){
                            gatherList.add(locationMessage);
                        }else {
                            alarmList.add(locationMessage);
                        }
                    }
                }
                multipleMessage.setGatherList(gatherList);
                multipleMessage.setAlarmList(alarmList);
                messageList.add(multipleMessage);
            }
            message.setContent(messageList);
        }

        return message;
    }
    /**
     * 根据请求数据type类型，决定响应数据type类型
     *
     */
    private String getResponseTypeByRequestType(String reqType){
        if(StringUtils.isEmpty(reqType)){
            return null;
        }
        if(TypeValues.HANDLE_GATHER_REQ.equalsIgnoreCase(reqType.trim())){

            return TypeValues.HANDLE_GATHER_RESP;
        }
        else if(TypeValues.BATCH_HANDLE_GATHER_REQ.equalsIgnoreCase(reqType.trim())){

            return TypeValues.BATCH_HANDLE_GATHER_RESP;
        }
        else if(TypeValues.HANDLE_ALARM_REQ.equalsIgnoreCase(reqType.trim())){

            return TypeValues.HANDLE_ALARM_RESP;
        }
        else if(TypeValues.BATCH_HANDLE_ALARM_REQ.equalsIgnoreCase(reqType.trim())){

            return TypeValues.BATCH_HANDLE_ALARM_RESP;
        }else {
            return reqType;
        }
    }
    /**
     *  广播消息
     */
    private void broadCastMessage(final String msg,String sessionKey) throws IOException {
        if(!sessionMap.isEmpty()){
            Set<ConcurrentWebSocketSessionDecorator> sessionSet = sessionMap.get(sessionKey);
            for(ConcurrentWebSocketSessionDecorator session:sessionSet){
                if(session != null && session.getDelegate().isOpen()){
                    session.sendMessage(new TextMessage(msg));
                }
            }
        }
    }

    /**
     *  发送消息给指定用户
     */
    private void sendMessageToUser(final String msg,final WebSocketSession session) throws IOException {
        if(session != null && session.isOpen()){
            session.sendMessage(new TextMessage(msg));
        }
    }
    private Boolean handlePushMessageFeedback(JSONObject payloadJson,String reqType){
        if(TypeValues.LIST_ALL_CARS_RESP.equalsIgnoreCase(reqType)
                || TypeValues.LIST_CAR_RESP.equalsIgnoreCase(reqType)){
            String pushMessageResult = payloadJson.getInt("status") == 0?"成功":"失败";
            String pushMsgType
                    = TypeValues.LIST_ALL_CARS_RESP.equalsIgnoreCase(reqType)?
                    TypeValues.LIST_ALL_CARS_REQ:TypeValues.LIST_CAR_REQ;
            log.info("************ 已推送消息：{}，反馈结果：{}",pushMsgType,pushMessageResult);
            return true;
        }
        return false;
    }
}
