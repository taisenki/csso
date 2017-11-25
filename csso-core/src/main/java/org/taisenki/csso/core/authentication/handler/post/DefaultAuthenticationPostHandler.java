package org.taisenki.csso.core.authentication.handler.post;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.taisenki.csso.core.app.App;
import org.taisenki.csso.core.app.AppService;
import org.taisenki.csso.core.authentication.AbstractParameter;
import org.taisenki.csso.core.authentication.Authentication;
import org.taisenki.csso.core.authentication.AuthenticationImpl;
import org.taisenki.csso.core.authentication.Credential;
import org.taisenki.csso.core.authentication.EncryCredentialManager;
import org.taisenki.csso.core.authentication.status.UserLoggedStatusStore;
import org.taisenki.csso.core.exception.NoCssoKeyException;
import org.taisenki.csso.core.key.CasKey;
import org.taisenki.csso.core.key.KeyManager;
import org.taisenki.csso.core.model.EncryCredentialInfo;
import org.taisenki.csso.core.model.Principal;
import org.taisenki.csso.core.model.UserLoggedStatus;
import org.taisenki.csso.web.utils.WebConstants;

/** 
 * 默认的认证后处理器实现类，提供抽象方法由具体子类实现。 <br/> 
 *
 * @date: 2017年11月21日 上午11:48:15
 * @author taisenki 
 * @version  
 * @since 
 */  
public class DefaultAuthenticationPostHandler implements
		AuthenticationPostHandler {
	
	private static Logger logger = LoggerFactory.getLogger(DefaultAuthenticationPostHandler.class);
	
	/**
	 * 服务端持续过期时间，3个月。
	 */
	private static final long SERVER_DURATION = 3L*30*24*60*60*1000;
	
	/**
	 * 客户端端持续过期时间，60秒钟。
	 */
	private static final long CLIENT_DURATION = 60*1000;
	
	private EncryCredentialManager encryCredentialManager;
	
	private KeyManager keyManager;
	
	private AppService appService;
	
	private UserLoggedStatusStore userLoggedStatusStore;

	public void setUserLoggedStatusStore(UserLoggedStatusStore userLoggedStatusStore) {
		this.userLoggedStatusStore = userLoggedStatusStore;
	}

	public AppService getAppService() {
		return appService;
	}

	public void setAppService(AppService appService) {
		this.appService = appService;
	}

	public KeyManager getKeyManager() {
		return keyManager;
	}

	public void setKeyManager(KeyManager keyManager) {
		this.keyManager = keyManager;
	}

	public EncryCredentialManager getEncryCredentialManager() {
		return encryCredentialManager;
	}

	public void setEncryCredentialManager(
			EncryCredentialManager encryCredentialManager) {
		this.encryCredentialManager = encryCredentialManager;
	}

	@Override
	public Authentication postAuthentication(Credential credential, Principal principal){
		Date createTime = new Date();
		//若认证通过，则返回认证结果对象。
		AuthenticationImpl authentication = new AuthenticationImpl();
		authentication.setAuthenticatedDate(createTime);
		authentication.setPrincipal(principal);
		encryCredentialWithCssoKey(authentication, credential, principal);
		encryCredentialWithAppKey(authentication, credential, principal);
		return authentication;
	}
	
	/*
	 * 使用csso服务器本身的key对凭据信息进行加密处理。
	 */
	private void encryCredentialWithCssoKey(AuthenticationImpl authentication, Credential credential, Principal principal){
		//如果是原始凭据，则需要进行加密处理。
		if(credential!=null && credential.isOriginal()){
			//查找csso服务对应的应用信息。
			App cssoApp = appService.findCssoServerApp();
			if(cssoApp==null){
				logger.error("no csso key info for appServer[{}].", cssoApp);
				throw NoCssoKeyException.INSTANCE; 
			}
			String encryCredential = encryCredentialManager.encrypt(buildEncryCredentialInfo(cssoApp.getAppId(), authentication, principal, SERVER_DURATION));
			//加密后的凭据信息写入到动态属性中。
			Map<String, Object> attributes = authentication.getAttributes();
			if(attributes==null){
				attributes = new HashMap<String, Object>();
			}
			attributes.put(CSSO_SERVER_EC_KEY, encryCredential);
			authentication.setAttributes(attributes);
		}
	}
	
	/**
	 * 使用csso服务器本身的key对凭据信息进行加密处理。
	 * @param duration 密钥持续时间。
	 */
	private void encryCredentialWithAppKey(AuthenticationImpl authentication, Credential credential, Principal principal){
		//获得登录的应用信息。
		AbstractParameter abstractParameter = null;
		if(credential!=null && credential instanceof AbstractParameter){
			abstractParameter = (AbstractParameter)credential;
		}
		//若登录对应的服务参数service的值不为空，则使用该service对应的应用的key进行加密。
		if(authentication!=null && abstractParameter!=null && abstractParameter.getParameterValue(WebConstants.SERVICE_PARAM_NAME)!=null){
			String service = abstractParameter.getParameterValue(WebConstants.SERVICE_PARAM_NAME).toString().trim().toLowerCase();
			//service不为空，且符合Http协议URL格式，则继续加密。
			if(service.length()>0){
				//查找csso服务对应的应用信息。
				App clientApp = appService.findAppByHost(service);
				if(clientApp!=null){
					String encryCredential = encryCredentialManager.encrypt(buildEncryCredentialInfo(clientApp.getAppId(), authentication, principal, CLIENT_DURATION));
					//加密后的凭据信息写入到动态属性中。
					Map<String, Object> attributes = authentication.getAttributes();
					if(attributes==null){
						attributes = new HashMap<String, Object>();
					}
					attributes.put(CSSO_CLIENT_EC_KEY, encryCredential);
					attributes.put(WebConstants.SERVICE_PARAM_NAME, service);
					authentication.setAttributes(attributes);
					
					//更新用户登录状态到存储器中。
					UserLoggedStatus status = new UserLoggedStatus(principal.getId(), clientApp.getAppId(), authentication.getAuthenticatedDate());
					userLoggedStatusStore.addUserLoggedStatus(status);
				}
			}
		}
	}

	private EncryCredentialInfo buildEncryCredentialInfo(String appId, AuthenticationImpl authentication,
			Principal principal, long duration) {
		EncryCredentialInfo encryCredentialInfo = new EncryCredentialInfo();
		if (authentication == null || principal == null) {
			return encryCredentialInfo;
		}
		CasKey cssoKey = keyManager.buildTokenKey(appId, principal.getId(), duration);
		if (cssoKey == null) {
			logger.error("build token key fail !");
			throw NoCssoKeyException.INSTANCE;
		}
		
		encryCredentialInfo.setAppId(appId);
		encryCredentialInfo.setCreateTime(authentication.getAuthenticatedDate());
		encryCredentialInfo.setUserId(principal.getId());
		encryCredentialInfo.setKeyId(cssoKey.getKeyId());
		Date expiredTime = new Date((authentication.getAuthenticatedDate().getTime() + duration));
		encryCredentialInfo.setExpiredTime(expiredTime);
		return encryCredentialInfo;
	}

}
