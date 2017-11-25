package org.taisenki.csso.client.web.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.taisenki.csso.client.handler.AppClientLoginHandler;
import org.taisenki.csso.client.key.ClientKeyServiceImpl;
import org.taisenki.csso.client.session.SessionStorage;
import org.taisenki.csso.common.util.BaseUtil;
import org.taisenki.csso.core.authentication.EncryCredential;
import org.taisenki.csso.core.authentication.EncryCredentialManagerImpl;
import org.taisenki.csso.core.key.CasKey;
import org.taisenki.csso.core.key.KeyService;
import org.taisenki.csso.core.model.EncryCredentialInfo;
import org.taisenki.csso.web.utils.WebConstants;

/**
 * csso客户端应用的过滤器，从而实现集成csso单点登录系统。
 * 此过滤器必须安装或者自己实现。
 * @author Administrator
 */
public class CssoClientFilter extends BaseClientFilter {
	
	private static Logger logger = LoggerFactory.getLogger(CssoClientFilter.class.getName());
	
	/**
	 * 在客户端的session中的用户信息，避免频繁认证，提高性能。
	 */
	public static final String USER_STATE_IN_SESSION_KEY = WebConstants.USER_STATE_IN_SESSION_KEY;
	
	/**
	 * 将csso服务器登出地址设置到request的属性中。
	 */
	public static final String CSSO_SERVER_LOGOUT_URL = "csso_server_logout_url";	
	
	/**
	 * csso服务器登录URL地址。
	 */
	protected String cssoServerLoginUrl = cssoServerHost+"login.do";

	/**
	 * csso服务器获取应用秘钥信息的URL地址。
	 */
	protected String cssoServerFetchKeyUrl = cssoServerHost+"fetchApp.do";
	
	/**
	 * csso服务器获取token key的URL地址。
	 */
	protected String cssoServerFetchTokenKeyUrl = cssoServerHost+"fetchKey.do";
	
	/**
	 * csso服务器登出URL地址。
	 */
	protected String cssoServerLogoutUrl = cssoServerHost+"logout.do";
	
	/**
	 * 本应用在csso服务器上的应用ID值。
	 */
	protected String cssoClientAppId = "1001";
	
	/**
	 * 登录本应用处理器类，由此类进行构造一个对象。
	 */
	protected String appClientLoginHandlerClass = "";
	
	
	/**
	 * 本应用对应的加密key.
	 */
	protected CasKey CasKey;
	
	/**
	 * 秘钥获取服务。
	 */
	protected KeyService keyService = null;
	
	/**
	 * 凭据管理器。
	 */
	protected EncryCredentialManagerImpl encryCredentialManager;
	
	/**
	 * 登录本应用的处理器。
	 */
	protected AppClientLoginHandler appClientLoginHandler;
	

	@Override
	public void doInit(FilterConfig filterConfig) throws ServletException {
		cssoClientAppId = getInitParameterWithDefalutValue(filterConfig, "cssoClientAppId", cssoClientAppId);
		cssoServerLoginUrl = getInitParameterWithDefalutValue(filterConfig, "cssoServerLoginUrl", cssoServerHost+"login.do");
		cssoServerLogoutUrl = getInitParameterWithDefalutValue(filterConfig, "cssoServerLogoutUrl", cssoServerHost+"logout.do");
		cssoServerFetchKeyUrl = getInitParameterWithDefalutValue(filterConfig, "cssoServerFetchKeyUrl", cssoServerHost+"fetchApp.do");
		cssoServerFetchTokenKeyUrl = getInitParameterWithDefalutValue(filterConfig, "cssoServerFetchTokenKeyUrl", cssoServerHost+"fetchKey.do");
		appClientLoginHandlerClass = getInitParameterWithDefalutValue(filterConfig, "appClientLoginHandlerClass", appClientLoginHandlerClass);
		//构造key服务等相关对象。
		//构造登录本应用的处理器对象。
		if(!BaseUtil.isEmpty(appClientLoginHandlerClass)){
			try{
				this.appClientLoginHandler = (AppClientLoginHandler)Class.forName(appClientLoginHandlerClass).newInstance();
			}catch (Exception e) {
				logger.error("Initialization appClientLoginHandler failed, please check config !", e);
			}
		}
		keyService = new ClientKeyServiceImpl(cssoServerFetchKeyUrl, cssoServerFetchTokenKeyUrl, cssoClientAppId);
		this.encryCredentialManager = new EncryCredentialManagerImpl();
		this.encryCredentialManager.setKeyService(keyService);
		logger.info("the csso sever is :"+this.cssoServerHost+", please check this service is ok.");
	}

	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse servletResponse = (HttpServletResponse)response;
		HttpServletRequest servletRequest = (HttpServletRequest)request;
		HttpSession session = servletRequest.getSession();
		try{
			// 供jsp调用
			servletRequest.setAttribute(CSSO_SERVER_LOGOUT_URL, cssoServerLogoutUrl);
			//本地应用未登录。
			if(session.getAttribute(USER_STATE_IN_SESSION_KEY)==null){
				//查找参数中是否存在csso_client_ec值，若没有则重定向到登录页面。
				String csso_client_ec = getClientEC(servletRequest);
				if(BaseUtil.isEmpty(csso_client_ec)){
					//跳转到csso登录页面。
					servletResponse.sendRedirect(buildRedirectTocssoServer(servletRequest));
					return;
				}
				//如果没有key，则重试获取一次。
				if(CasKey==null){
					try{
						CasKey = keyService.findKeyByAppId(cssoClientAppId);
					}catch (Exception e) {
						logger.error("fetch App key info error", e);
					}
				}
				//解密凭据信息。
				EncryCredentialInfo encryCredentialInfo = this.encryCredentialManager.decrypt(new EncryCredential(csso_client_ec));
				if(encryCredentialInfo!=null){
					//检查凭据合法性。
					boolean valid = this.encryCredentialManager.checkEncryCredentialInfo(encryCredentialInfo);
					//如果合法，则继续其它处理。
					if(valid){
						//设置登录状态到session中。
						session.setAttribute(USER_STATE_IN_SESSION_KEY, encryCredentialInfo);
						//触发登录本应用的处理。
						if(appClientLoginHandler!=null){
							//登录本应用。
							appClientLoginHandler.loginClient(encryCredentialInfo, servletRequest, servletResponse);
						}
						
						//重新定位到原请求，去除EC参数。
						String url = servletRequest.getRequestURL().toString();
						if(!BaseUtil.isEmpty(url)){
							//如果请求中存在EC参数，则去除这个参数，重定位。
							if(url.contains(WebConstants.CSSO_CLIENT_ENCRYPTED_CREDENTIAL_COOKIE_KEY)){
								url = url.substring(0, url.indexOf(WebConstants.CSSO_CLIENT_ENCRYPTED_CREDENTIAL_COOKIE_KEY));
								//去除末尾的问号。
								if(url.endsWith("?")){
									url = url.substring(0, url.length()-1);
								}
								
								//去除末尾的&符号。
								if(url.endsWith("&")){
									url = url.substring(0, url.length()-1);
								}
							}
						}
						//保存用户和session的关系
						SessionStorage.put(encryCredentialInfo.getUserId(),session);
//						session.setMaxInactiveInterval(interval);
//						session.getMaxInactiveInterval()
						//重新定位请求，避免尾部出现长参数。
						servletResponse.sendRedirect(url);
						return;
					}
				}
				//否则凭据信息不合法，跳转到csso登录页面。
				servletResponse.sendRedirect(buildRedirectTocssoServer(servletRequest));
				return;
			}
			
			//若已经登录过，则直接返回，继续其它过滤器。
			chain.doFilter(request, response);
			return;
		}
		//处理异常信息。
		catch (Exception e) {
			
			//否则凭据信息不合法，跳转到csso登录页面。
			servletResponse.sendRedirect(buildRedirectTocssoServer(servletRequest));
			return;
		}
		
	}
	
	protected String buildRedirectTocssoServer(HttpServletRequest servletRequest){
		StringBuffer sb = new StringBuffer(this.cssoServerLoginUrl);
		if(this.cssoServerLoginUrl.contains("?")){
			sb.append("&");
		}
		else{
			sb.append("?");
		}
		sb.append("service=").append(servletRequest.getRequestURL().toString());
		return sb.toString();
	}

	@Override
	public void destroy() {
		this.CasKey = null;
		if(this.keyService != null)
			try {
				keyService.close();
			} catch (IOException e) {
				logger.error("密钥服务关闭失败：{}", e.getMessage());
			}
	}
	
	
	/**
	 * 从客户端参数或者cookie中获取EC值。
	 * @param request http请求对象。
	 * @return EC值。
	 */
	protected String getClientEC(HttpServletRequest request){
		String ec = null;
		if(request!=null){
			ec = request.getParameter(WebConstants.CSSO_CLIENT_ENCRYPTED_CREDENTIAL_COOKIE_KEY);
		}
		return ec;
	}
	
}
