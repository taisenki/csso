package org.taisenki.csso.web.view;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.taisenki.csso.core.authentication.Authentication;
import org.taisenki.csso.core.authentication.handler.post.AuthenticationPostHandler;
import org.taisenki.csso.core.message.MessageUtils;
import org.taisenki.csso.core.service.LoginResult;
import org.taisenki.csso.web.utils.WebConstants;

/**
 * 默认的实现类。
 * 
 * @author burgess yang
 * 
 */
public class DefaultLoginResultToView implements LoginResultToView {

	@Override
	public ModelAndView loginResultToView(ModelAndView mv, LoginResult result, HttpServletRequest request,
			HttpServletResponse response) {
		// 若登录成功，则返回成功页面。
		if(mv==null){
			mv = new ModelAndView();
		}
		if(result==null || request==null || response==null){
			return mv;
		}
		if (result.isSuccess()) {
			//登录结果对象。
			Authentication authentication = result.getAuthentication();

			//清除session中的状态信息service值。
			request.getSession().removeAttribute(WebConstants.CSSO_SERVICE_KEY_IN_SESSION);
			
			// 如果有加密凭据信息，则写入加密凭据值到cookie中。
			if (authentication != null
					&& authentication.getAttributes() != null) {
				Map<String, Object> attributes = authentication.getAttributes();
				// CSSO服务端加密的凭据存在，则写入cookie中。
				if (attributes
						.get(AuthenticationPostHandler.CSSO_SERVER_EC_KEY) != null) {
					response.addCookie(new Cookie(
							WebConstants.CSSO_SERVER_ENCRYPTED_CREDENTIAL_COOKIE_KEY,
							attributes
									.get(AuthenticationPostHandler.CSSO_SERVER_EC_KEY)
									.toString()));
				}
				// CSSO客户端加密的凭据和参数service存在，则跳转到对应的页面中。
				if (attributes
						.get(AuthenticationPostHandler.CSSO_CLIENT_EC_KEY) != null
						&& !StringUtils.isEmpty(attributes.get(WebConstants.SERVICE_PARAM_NAME))) {
					mv.getModel().put("authentication", authentication);
					mv.setView(this
							.buildRedirectView(
									attributes.get(WebConstants.SERVICE_PARAM_NAME).toString(),
									attributes
											.get(AuthenticationPostHandler.CSSO_CLIENT_EC_KEY)
											.toString()));
					return mv;
				}
			}
			mv.getModel().put("authentication", authentication);
			mv.setViewName("loginSucess");
		} else {
			//删除以前不合法的凭据信息。
			//清除cookie值。
			Cookie[] cookies = request.getCookies();
			if(cookies!=null && cookies.length>0){
				for(Cookie cookie:cookies){
					if(WebConstants.CSSO_SERVER_ENCRYPTED_CREDENTIAL_COOKIE_KEY.equals(cookie.getName())){
						//设置过期时间为立即。
						cookie.setMaxAge(0);
						response.addCookie(cookie);
					}
				}
			}
			// 放置不合法cookie清除后无法自动跳转
			String service = request.getParameter(WebConstants.SERVICE_PARAM_NAME);
			if(service != null){
				request.getSession().setAttribute(WebConstants.CSSO_SERVICE_KEY_IN_SESSION, service);
			}
			mv.getModel().put("code", result.getCode());
			mv.getModel().put("msg",
					MessageUtils.getMessage(result.getMsgKey()));			
		}
		return mv;
	}

	/**
	 * 构造跳转的URL地址。
	 * 
	 * @return
	 */
	private RedirectView buildRedirectView(String service,
			String encryCredential) {
		StringBuffer sb = new StringBuffer(service);
		if (service.contains("?")) {
			sb.append("&");
					
		} else {
			sb.append("?");
		}
		sb.append(WebConstants.CSSO_CLIENT_ENCRYPTED_CREDENTIAL_COOKIE_KEY)
		.append("=").append(encryCredential);
		return new RedirectView(sb.toString());
	}

}
