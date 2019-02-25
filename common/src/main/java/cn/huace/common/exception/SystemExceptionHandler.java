package cn.huace.common.exception;


import cn.huace.common.bean.HttpResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hd on 2016/12/13.
 */
@Slf4j
public class SystemExceptionHandler implements HandlerExceptionResolver {

    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        log.error("****** 错误信息：URI:" + httpServletRequest.getRequestURI() + ",异常:" + e.getMessage(),e);
        ModelAndView mav = new ModelAndView();
        MappingJackson2JsonView mappingJackson2JsonView = new MappingJackson2JsonView();
        mav.setView(mappingJackson2JsonView);
        HttpResult result= HttpResult.createFAIL(e.getMessage());
        mav.addObject(result);
        return mav;
    }
}
