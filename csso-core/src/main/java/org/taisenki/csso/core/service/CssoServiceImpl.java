package org.taisenki.csso.core.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.taisenki.csso.core.app.App;
import org.taisenki.csso.core.app.AppService;
import org.taisenki.csso.core.authentication.Authentication;
import org.taisenki.csso.core.authentication.AuthenticationManager;
import org.taisenki.csso.core.authentication.Credential;
import org.taisenki.csso.core.authentication.status.UserLoggedStatusStore;
import org.taisenki.csso.core.exception.InvalidCredentialException;
import org.taisenki.csso.core.model.UserLoggedStatus;

/**
 * @author Administrator
 * @version 1.0
 * @created 30-五月-2013 21:32:24
 */
public class CssoServiceImpl implements CssoService {
	
	private Logger logger = Logger.getLogger(CssoServiceImpl.class);

	private AuthenticationManager authenticationManager;
	
	private AppService appService;
	
	private UserLoggedStatusStore userLoggedStatusStore;
	
	private LogoutAppService logoutAppService;

	public void setLogoutAppService(LogoutAppService logoutAppService) {
		this.logoutAppService = logoutAppService;
	}

	public void setAppService(AppService appService) {
		this.appService = appService;
	}

	public void setUserLoggedStatusStore(UserLoggedStatusStore userLoggedStatusStore) {
		this.userLoggedStatusStore = userLoggedStatusStore;
	}


	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	public CssoServiceImpl(){}

	/**
	 * 实现登录逻辑。
	 */
	@Override
	public LoginResult login(Credential credential){
		//若没有凭据，则返回空。
		if(credential==null){
			return null;
		}
		LoginResult loginResult = new LoginResult();
		loginResult.setSuccess(false);
		//调用认证处理器进行认证。
		try{
			Authentication authentication = authenticationManager.authenticate(credential);
			//登录成功。
			loginResult.setSuccess(true);
			loginResult.setAuthentication(authentication);
		}catch (InvalidCredentialException e) {
			//登录失败。
			loginResult.setSuccess(false);
			//设置异常代码和异常信息键值。
			loginResult.setCode(e.getCode());
			loginResult.setMsgKey(e.getMsgKey());
			logger.error("login failed!", e);
		}
		return loginResult;
	}

	@Override
	public void logout(Credential credential, String service) {
		try{
			if(credential==null){
				logger.info("the credential is null");
				return;
			}
			//对凭据做一次认证。
			Authentication authentication = authenticationManager.authenticate(credential);
			//登出所有的应用。
			logoutAppService.logoutApp(authentication.getPrincipal().getId(), service);
			
			//清除用户登录状态。
			if(authentication!=null && authentication.getPrincipal()!=null){
				this.userLoggedStatusStore.clearUpUserLoggedStatus(authentication.getPrincipal().getId());
			}
		}catch (InvalidCredentialException e) {
			logger.error("logout error", e);
		}
	}

	@Override
	public List<App> getAppList(Credential credential){
		List<App> apps = new ArrayList<App>();
		if(credential==null){
			return apps;
		}
		try{
			//对凭据做一次认证。
			Authentication authentication = authenticationManager.authenticate(credential);
			if(authentication!=null && authentication.getPrincipal()!=null){
				List<UserLoggedStatus> list = this.userLoggedStatusStore.findUserLoggedStatus(authentication.getPrincipal().getId());
				//批量查询对应的应用信息。
				if(list!=null&& list.size()>0){
					for(UserLoggedStatus status:list){
						App app = appService.findAppById(status.getAppId());
						if(app!=null){
							apps.add(app);
						}
					}
				}
			}
			
		}catch (InvalidCredentialException e) {
		}
		return apps;
	}

}