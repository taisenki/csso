package org.taisenki.csso.core.service;

import java.util.List;

import org.taisenki.csso.core.app.App;
import org.taisenki.csso.core.authentication.Credential;

/** 
 * 核心服务接口，定义了所有的核心方法。
 * 该接口是一个门面类，定义了csso核心的对外
 * 提供的方法。 <br/> 
 *
 * @date: 2017年11月21日 上午10:41:47
 * @author taisenki 
 * @version  
 * @since 
 */  
public interface CssoService {
	
	/**
	 * 使用一个用户凭据登录csso中心认证服务。
	 * @param credential 用户凭据。
	 * @return 登录结果。
	 */
	public LoginResult login(Credential credential);
	
	/**
	 * 统一登出sso服务,该接口的职责是将credential代表的用户登录过的所有应用
	 * 全部统一登出。优先登出servcie对应的应用。
	 * @param credential 用户凭据。
	 * @param servcie 用户登出后要访问的应用，该应用对应的应用要同步登出，其它业务应用可以异步登出。
	 */
	public void logout(Credential credential, String servcie);
	
	/**
	 * 获得某个用户凭据对应的登录的应用列表。
	 * @param credential 用户凭据。
	 * @return 该用户登录的应用列表。
	 */
	public List<App> getAppList(Credential credential);
	

}
