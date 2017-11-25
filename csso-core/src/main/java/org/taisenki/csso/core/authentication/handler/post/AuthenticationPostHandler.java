package org.taisenki.csso.core.authentication.handler.post;

import org.taisenki.csso.core.authentication.Authentication;
import org.taisenki.csso.core.authentication.Credential;
import org.taisenki.csso.core.model.Principal;

/** 
 * 认证成功后的处理器，该接口的职责是将
 * 用户认证主体，用户凭据转换为一个合适的
 * 认证结果对象。根据用户凭据中的信息，参数
 * 进行合适的转换。 <br/> 
 *
 * @date: 2017年11月21日 上午11:41:01
 * @author taisenki 
 * @version  
 * @since 
 */  
public interface AuthenticationPostHandler {
	
	/**
	 * csso服务本身的加密凭据信息存储在验证结果对象
	 * authentication的动态属性attributes的key值。
	 */
	public static final String CSSO_SERVER_EC_KEY = "csso_ser_ec_key";
	
	/**
	 * csso服务本身的加密凭据信息存储在验证结果对象
	 * authentication的动态属性attributes的key值。
	 */
	public static final String CSSO_CLIENT_EC_KEY = "csso_cli_ec_key";
	
	/**
	 * 认证后的处理方法，将用户的凭据和主体转换为一个认证结果对象。
	 * @param credential 用户凭据。
	 * @param principal 用户主体。
	 * @return 认证结果对象信息。
	 */
	public Authentication postAuthentication(Credential credential, Principal principal);

}
