/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.huace.controller.base;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * 
 * @author huangdan
 */

public class BaseFrontController {
	public static final String DEV_ID="dev_id";
	public static final String SHOP_Id="shop_id";

	protected Integer findShopId(HttpServletRequest request) {
		HttpSession session=request.getSession();
		return (Integer)session.getAttribute(SHOP_Id);
	}

	// 基于session做微信用户openId保存
	protected void setShopIdToSession(HttpServletRequest request,Integer shopId) {
		if (shopId != null) {
			HttpSession session=request.getSession();
			session.setAttribute(SHOP_Id,shopId);
		}
	}

	/* 基于session做商城会员memberId保存 预留 */
	protected void setDevIdToSession(HttpServletRequest request,String devId) {
		if (devId != null) {
			HttpSession session=request.getSession();
			session.setAttribute(DEV_ID,devId);
		}
	}
	
	//Integer
	protected String findDevId(HttpServletRequest request) {
		HttpSession session=request.getSession();
		return (String)session.getAttribute(DEV_ID);
	}

	protected void remove(HttpServletRequest request) {
		request.getSession().removeAttribute(DEV_ID);
		request.getSession().removeAttribute(SHOP_Id);
	}
	
	private void setCookie(HttpServletResponse response,String name,String value,int time) {
		Cookie cookie = new Cookie(name,value);
		cookie.setMaxAge(time);
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	/**
	 * 接口验证
	 * @return
	 */
	protected boolean checkDevId(HttpServletRequest request){
		String devId = findDevId(request);
		if(devId == null){
			return false;
		}
		return true;
	}
}
