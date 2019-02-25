package cn.huace.common.bean;


import cn.huace.common.config.SystemConfig;
import lombok.Data;

/**
 *
 * Created by Administrator on 2016/12/7.
 */
@Data
public class HttpResult {

    private int state;

    private String msg;

    private Object content;

    private Object extra;

    private int type;

    private static int STATE_SUCCESS=0;

    private static int STATE_FAIL=1;

    private static int STATE_UNAUTHORIZED=401;

    private String filePre= SystemConfig.getInstance().getFilePre();

    public  HttpResult(int state,String msg,Object content,Object extra){
        this.state=state;
        this.msg=msg;
        this.content=content;
        this.extra=extra;
    }

    public static HttpResult createSuccess(String msg,Object content){
        return new HttpResult(STATE_SUCCESS,msg,content,null);
    }
    public static HttpResult createSuccess(String msg,Object content,Object extra){
        return new HttpResult(STATE_SUCCESS,msg,content,extra);
    }
    public static HttpResult createSuccess(Object content,Object extra){
        return new HttpResult(STATE_SUCCESS,null,content,extra);
    }
    public static HttpResult createSuccess(String msg){
        return createSuccess(msg,null);
    }
    public static HttpResult createSuccess(Object content){
        return createSuccess(null,content);
    }
    public static HttpResult createFAIL(String msg,Object content){
        return new HttpResult(STATE_FAIL,msg,content,null);
    }
    public static HttpResult createFAIL(String msg){
        return createFAIL(msg,null);
    }
    public static HttpResult createFAIL(Object content){
        return createFAIL(null,content);
    }
}
