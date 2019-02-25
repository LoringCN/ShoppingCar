package cn.huace.common.bean;


import cn.huace.common.config.SystemConfig;
import lombok.Data;

/**
 * Created by Administrator on 2016/12/7.
 */
@Data
public class HttpFrontResult {

    private int state;

    private String msg;

    private Object content;

    public static int STATE_SUCCESS=0;

    public static int STATE_FAIL=1;

    public static int STATE_UNAUTHORIZED=401;

    public HttpFrontResult(int state, String msg, Object content){
        this.state=state;
        this.msg=msg;
        this.content=content;
    }

    public static HttpFrontResult createSuccess(String msg, Object content){
    return new HttpFrontResult(STATE_SUCCESS,msg,content);
    }
    public static HttpFrontResult createSuccess(String msg){
        return createSuccess(msg,null);
    }
    public static HttpFrontResult createSuccess(Object content){
        return createSuccess(null,content);
    }
    public static HttpFrontResult createFAIL(String msg, Object content){
        return new HttpFrontResult(STATE_FAIL,msg,content);
    }
    public static HttpFrontResult createFAIL(String msg){
        return createFAIL(msg,null);
    }
    public static HttpFrontResult createFAIL(Object content){
        return createFAIL(null,content);
    }
}
