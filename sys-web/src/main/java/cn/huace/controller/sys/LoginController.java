package cn.huace.controller.sys;


import cn.huace.common.bean.HttpResult;
import cn.huace.sys.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@Api(value = "/",description = "登录")
public class LoginController {

    @Autowired
    protected AccountService accountService;

    @RequestMapping(value = "/toLogin",method = RequestMethod.GET)
    public HttpResult toLogin(){
		Map<String,Object> resp = new HashMap<>();
		resp.put("content",new ArrayList<>());
		resp.put("login",true);
		return HttpResult.createFAIL("登录已过期！",resp);
	}
	/**
	 * Go login
	 * @param
	 * @return
	 */
	@RequestMapping(value="login", method=RequestMethod.POST)
	@ApiOperation(value = "登录接口", notes = "登录接口 account，password必填")
	public HttpResult login(@RequestParam String account,@RequestParam String password) {
		System.out.println("account:"+account+" "+"password:"+password);
//		account = "admin1";password="admin1";
		UsernamePasswordToken upt = new UsernamePasswordToken(account, password);
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.login(upt);
		} catch (AuthenticationException e) {
			e.printStackTrace();
			return HttpResult.createFAIL("您的账号或密码输入错误!");
		}
		return HttpResult.createSuccess("登录成功!",accountService.findUserByAccount(account));
	}

	/**
	 * Exit
	 * @return
	 */
	@ApiOperation(value = "登出接口")
	@RequestMapping(value="logout",method=RequestMethod.POST)
	public HttpResult logout() {
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
		return HttpResult.createSuccess("登出成功!");
	}




}
