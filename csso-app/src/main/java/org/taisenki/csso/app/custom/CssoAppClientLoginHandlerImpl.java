package org.taisenki.csso.app.custom;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.taisenki.csso.client.handler.AppClientLoginHandler;
import org.taisenki.csso.core.model.EncryCredentialInfo;

/**
 * 默认的登录认证实现。
 * @author burgess yang
 *
 */
public class CssoAppClientLoginHandlerImpl implements AppClientLoginHandler {
	
	private static Logger logger = Logger.getLogger(CssoAppClientLoginHandlerImpl.class.getName());

	public static final String USER_KEY = "USER_KEY_SESSON";
	public static final String LOCAL_IP_KEY = "LOCAL_IP_KEY_SESSON";
	public static final String LOCAL_PORT_KEY = "LOCAL_PORT_KEY_SESSON";
	
	@Override
	public void loginClient(EncryCredentialInfo encryCredentialInfo, HttpServletRequest request, HttpServletResponse response) {
		String host = request.getServerName();
		int port = request.getServerPort();
		request.getSession().setAttribute(USER_KEY, encryCredentialInfo);
		request.getSession().setAttribute(LOCAL_IP_KEY, host);
		request.getSession().setAttribute(LOCAL_PORT_KEY, port);
		logger.info("the user id is "+encryCredentialInfo.getUserId() +" has logined in the app");
	}

}
