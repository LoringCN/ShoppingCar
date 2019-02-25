package cn.huace.filter;

/**
 * Created by huangdan on 2017/5/20.
 */

import cn.huace.common.bean.HttpFrontResult;
import cn.huace.controller.base.BaseFrontController;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
public class DeviceSessionFilter implements Filter{

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest=(HttpServletRequest)servletRequest;
        String uri=httpServletRequest.getRequestURI();
        System.out.println("uri:"+uri);
        if(uri.contains("register") || uri.contains("led/info") || uri.contains("/goodsRenewal")){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        HttpSession session=httpServletRequest.getSession();
        Object devId= session.getAttribute(BaseFrontController.DEV_ID);
        if (devId==null)
        {
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.setContentType("application/json; charset=utf-8");
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            ObjectMapper mapper = new ObjectMapper();

            HttpFrontResult resultMsg = new HttpFrontResult(HttpFrontResult.STATE_UNAUTHORIZED, "请重新注册设备", null);
            httpResponse.getWriter().write(mapper.writeValueAsString(resultMsg));
            return;
        }
        else
        {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    public void destroy() {

    }
}
