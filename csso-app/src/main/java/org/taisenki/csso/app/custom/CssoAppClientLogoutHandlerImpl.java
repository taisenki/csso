package org.taisenki.csso.app.custom;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.taisenki.csso.client.handler.AppClientLogoutHandler;
import org.taisenki.csso.common.util.BaseUtil;

public class CssoAppClientLogoutHandlerImpl implements AppClientLogoutHandler {
	
	private static Logger logger = Logger.getLogger(CssoAppClientLogoutHandlerImpl.class.getName());

	@Override
	public void logoutClient(HttpServletRequest request,
			HttpServletResponse response, String userId) {
		//若已经登录，则作相关处理。
		if(!BaseUtil.isEmpty(userId)){
			//remove the exception
			logger.info("the user id is userId has logined out the app");
			request.getSession().removeAttribute(CssoAppClientLoginHandlerImpl.USER_KEY);
		}
	}

}
