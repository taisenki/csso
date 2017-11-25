package org.taisenki.csso.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.taisenki.csso.core.authentication.Credential;
import org.taisenki.csso.core.service.CssoService;
import org.taisenki.csso.core.service.LoginResult;
import org.taisenki.csso.web.resolver.CredentialResolver;
import org.taisenki.csso.web.utils.WebConstants;
import org.taisenki.csso.web.view.LoginResultToView;

/**
 * 登入web控制器类，处理登录的请求。
 * @author burgess yang
 *
 */
@Controller
public class LoginAction {
	
	private static final Logger LOGGER = Logger.getLogger(LoginAction.class);
	
	@Autowired
	protected CredentialResolver credentialResolver;
	
	@Autowired
	protected CssoService cssoService;
	
	@Autowired
	protected LoginResultToView loginResultToView;

	public void setLoginResultToView(LoginResultToView loginResultToView) {
		this.loginResultToView = loginResultToView;
	}

	public void setCssoService(CssoService cssoService) {
		this.cssoService = cssoService;
	}

	/**
	 * 设置用户凭据解析器。
	 * @param credentialResolver
	 */
	public void setCredentialResolver(CredentialResolver credentialResolver) {
		this.credentialResolver = credentialResolver;
	}

	/**
	 * 登录接口，该接口处理所有与登录有关的请求。 包括以下几种情况： 1.
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/login")
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response) {
		
		ModelAndView mv = new ModelAndView("login");
		LOGGER.debug("enter login action");
		//解析用户凭据。
		Credential credential = credentialResolver.resolveCredential(request);
		//没有提供任何认证凭据。
		if(credential==null){
			//设置serivce地址到session中。
			String service = request.getParameter(WebConstants.SERVICE_PARAM_NAME);
			LOGGER.debug("the servcie is "+service);
			if(!StringUtils.isEmpty(service)){
				request.getSession().setAttribute(WebConstants.CSSO_SERVICE_KEY_IN_SESSION, service);
			}
			LOGGER.info("no credential, return login page");
			//返回到登录页面，索取用户凭据。
			return mv;
		}
		//提供了用户凭据
		else{
			//调用核心结果进行凭据认证。
			LoginResult result = cssoService.login(credential);
			//将验证结果转换为视图输出结果。
			mv = loginResultToView.loginResultToView(mv, result, request, response);
		}
		return mv;
	}
	

}
