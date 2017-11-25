/** 
 * Project Name:csso-app 
 * File Name:HomeAction.java 
 * Package Name:org.taisenki.csso.app.web.action 
 * Date:2017年11月23日下午7:00:45 
 * Copyright (c) 2017, taisenki@dareway.com.cn All Rights Reserved. 
 * 
 */  
  
package org.taisenki.csso.app.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.taisenki.csso.app.custom.CssoAppClientLoginHandlerImpl;

/** 
 * App示例，简单设置参数 <br/> 
 *
 * @Date:    2017年11月23日 下午7:00:45 <br/> 
 * @author   taisenki 
 * @version  
 * @since    JDK 1.8
 * @see       
 */
@Controller
public class HomeAction {
	
	@RequestMapping("home")
	public ModelAndView home(HttpServletRequest request, HttpSession session){
		ModelAndView mv = new ModelAndView();
		mv.addObject("user", session.getAttribute(CssoAppClientLoginHandlerImpl.USER_KEY));
		mv.addObject("ip", session.getAttribute(CssoAppClientLoginHandlerImpl.LOCAL_IP_KEY));
		mv.addObject("port", session.getAttribute(CssoAppClientLoginHandlerImpl.LOCAL_PORT_KEY));
		return mv;
	}
}
  