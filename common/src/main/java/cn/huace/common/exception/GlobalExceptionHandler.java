package cn.huace.common.exception;

import cn.huace.common.bean.HttpResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * created by Loring on 2018-10-15
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 系统异常处理，比如：404,500
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public HttpResult defaultErrorHandler(Exception e) throws Exception {
        if (e instanceof org.springframework.web.servlet.NoHandlerFoundException) {
            return HttpResult.createFAIL("404",((NoHandlerFoundException) e).getRequestURL());
        } else {
            throw e ;
        }
    }
}
