package cn.huace.controller;

import cn.huace.common.bean.HttpResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Date:2018/4/10
 */
@RestController
public class LoginController {

    @RequestMapping(value = "/toLogin",method = RequestMethod.GET)
    public HttpResult toLogin(){
        Map<String,Object> resp = new HashMap<>();
        resp.put("content",new ArrayList<>());
        resp.put("login",true);
        return HttpResult.createFAIL("登录已过期！",resp);
    }
}
