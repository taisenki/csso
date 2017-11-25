package org.taisenki.csso.core.authentication.handler.auth;

import org.taisenki.csso.core.authentication.Credential;
import org.taisenki.csso.core.exception.AuthenticationException;

/** 
 * 认证处理器类，该类检查用户的凭证是否合法。 <br/> 
 *
 * @date: 2017年11月21日 上午11:19:09
 * @author taisenki 
 * @version  
 * @since 
 */  
public interface AuthenticationHandler {

	/**
	 * 认证方法，返回true表示认证成功，false表示认证失败
	 * 
	 * @param credential 用户凭据
	 * @return 是否认证通过。
	 * @throws 若认证失败，则抛出合适的认证错误异常对象。
	 */
	public boolean authenticate(Credential credential) throws AuthenticationException;

	/**
	 * 是否支持用户凭证credential的认证处理，返回值true表示支持，
	 * false表示不支持，若不支持该凭证，则忽略。
	 * 
	 * @param credential 用户凭据
	 */
	public boolean supports(Credential credential);

}