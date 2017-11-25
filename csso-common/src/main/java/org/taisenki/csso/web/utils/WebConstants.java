package org.taisenki.csso.web.utils;

/**
 * web相关的常量类，定义了与web相关的所有常量值。
 *
 */
public interface WebConstants {
	
	/**
	 * CSSO中心认证服务器写入到用户web客户端cookie中的认证加密后的凭据的键名称。
	 */
	public static final String CSSO_SERVER_ENCRYPTED_CREDENTIAL_COOKIE_KEY = "CSSO_SERVER_EC";
	
	/**
	 * CSSO客户端应用服务器写入到用户web客户端cookie中的认证加密后的凭据的键名称。
	 */
	public static final String CSSO_CLIENT_ENCRYPTED_CREDENTIAL_COOKIE_KEY = "CSSO_CLIENT_EC";
	
	/**
	 * 目的服务地址service的参数名。
	 */
	public static final String SERVICE_PARAM_NAME = "service";
	
	/**
	 * 目的服务地址存储在session中的key值。
	 */
	public static final String CSSO_SERVICE_KEY_IN_SESSION = "CSSO_SERVICE_KEY";
	
	/**
	 * 用户标识的参数名。
	 */
	public static final String USER_ID_PARAM_NAME = "userId";
	

	/**
	 * 在客户端的session中的用户信息，避免频繁认证，提高性能。
	 */
	public static final String USER_STATE_IN_SESSION_KEY = "csso_client_user_info_session_key";

}
