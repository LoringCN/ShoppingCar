package cn.huace.message.constant;

/**
 * 封装所有websocket请求和响应中的type值
 * Created by yld on 2017/10/20.
 */
public class TypeValues {
    public static final String HANDLE_ALARM_REQ = "handleAlarmReq";
    public static final String HANDLE_ALARM_RESP = "handleAlarmResp";
    public static final String BATCH_HANDLE_ALARM_REQ = "batchHandleAlarmReq";
    public static final String BATCH_HANDLE_ALARM_RESP = "batchHandleAlarmResp";
    public static final String HANDLE_GATHER_REQ = "handleGatherReq";
    public static final String HANDLE_GATHER_RESP = "handleGatherResp";
    public static final String BATCH_HANDLE_GATHER_REQ = "batchHandleGatherReq";
    public static final String BATCH_HANDLE_GATHER_RESP = "batchHandleGatherResp";
    //多个推送
    public static final String LIST_ALL_CARS_REQ = "listAllCarsReq";
    public static final String LIST_ALL_CARS_RESP = "listAllCarsResp";
    //单个推送
    public static final String LIST_CAR_REQ = "listCarReq";
    public static final String LIST_CAR_RESP = "listCarResp";

//    public static void main(String[] args) {
//        String str = "batchHandleGatherReq";
//        System.out.println(str.toUpperCase());
//    }
}
