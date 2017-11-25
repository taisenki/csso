package org.taisenki.csso.web.resolver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import org.taisenki.csso.core.authentication.Credential;
import org.taisenki.csso.core.authentication.EncryCredential;
import org.taisenki.csso.web.utils.WebConstants;

/**
 * 经过认证加密后的凭据信息解析器，从http请求的cookie中解析出对应的加密后的凭据信息。
 * @author burgess yang
 *
 */
public class EncryCredentialResolver implements CredentialResolver {
	
	private static final Logger LOGGER = Logger.getLogger(EncryCredentialResolver.class);

	@Override
	public Credential resolveCredential(HttpServletRequest request) {
		if(request!=null){
			//查找请求中的cookie。
			Cookie[] cookies = request.getCookies();
			if(cookies!=null){
				String value = null;
				for(Cookie cookie:cookies){
					//若查找到CSSO写入的cookie值。
					if(cookie!=null && cookie.getName().equalsIgnoreCase(WebConstants.CSSO_SERVER_ENCRYPTED_CREDENTIAL_COOKIE_KEY)){
						value = cookie.getValue();
						break;
					}
				}
				//如果cookie中没有凭据值，则从请求参数中获取凭据值。
				if(StringUtils.isEmpty(value)){
					LOGGER.info("the CSSO_SERVER_EC value is: "+value);
					value = request.getParameter(WebConstants.CSSO_SERVER_ENCRYPTED_CREDENTIAL_COOKIE_KEY);
				}
				//最终如果加密凭据有值，则直接返回凭据对象。
				if(!StringUtils.isEmpty(value)){
					//去除空串后返回。
					return new EncryCredential(value.trim());
				}
			}
		}
		return null;
	}

}
