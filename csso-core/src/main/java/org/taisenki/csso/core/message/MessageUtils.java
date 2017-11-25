package org.taisenki.csso.core.message;

import java.util.HashMap;
import java.util.Map;

import org.taisenki.csso.core.exception.BuildTokenKeyException;
import org.taisenki.csso.core.exception.EmptyCredentialException;
import org.taisenki.csso.core.exception.InvalidCredentialException;
import org.taisenki.csso.core.exception.InvalidEncryCredentialException;
import org.taisenki.csso.core.exception.NoAuthenticationPostHandlerException;
import org.taisenki.csso.core.exception.NoCssoKeyException;
import org.taisenki.csso.core.exception.PasswordInvalidException;
import org.taisenki.csso.core.exception.UnsupportedCredentialsException;
import org.taisenki.csso.core.exception.UsernameOrPasswordEmptyException;
import org.taisenki.csso.core.exception.UsernameOrPasswordInvalidException;

/**
 * 使用简单的国际化策略，只支持中文，以后可以改成更加灵活的
 * 能够支持多种语言的消息提示。
 *
 */
public class MessageUtils {
	
	/**
	 * 信息表，其中key是消息键，value是具体对应的中文的消息内容。
	 */
	private static Map<String, String> msgMap = null;
	
	static{
		//初始化消息表。
		msgMap = new HashMap<String, String>();
		msgMap.put(InvalidCredentialException.MSG_KEY, "不合法的凭据");
		msgMap.put(InvalidEncryCredentialException.MSG_KEY, "不合法的加密凭据");
		msgMap.put(UsernameOrPasswordEmptyException.MSG_KEY, "用户名或者密码为空");
		msgMap.put(UsernameOrPasswordInvalidException.MSG_KEY, "用户名或者密码不正确");
		msgMap.put(PasswordInvalidException.MSG_KEY, "密码错误");
		msgMap.put(EmptyCredentialException.MSG_KEY, "凭据为空");
		msgMap.put(UnsupportedCredentialsException.MSG_KEY, "不支持的用户凭据");
		msgMap.put(NoAuthenticationPostHandlerException.MSG_KEY, "无合法的认证后处理器");
		msgMap.put(NoCssoKeyException.MSG_KEY, "系统没有配置csso服务器本身的key信息，请检查配置。");
		msgMap.put(BuildTokenKeyException.MSG_KEY, "系统生成token key失败，请检查日志。");
	}
	
	
	/**
	 * 查询消息键对应的消息提示内容。
	 * @param key 消息键
	 * @return 消息内容。
	 */
	public static String getMessage(String key){
		return msgMap.get(key);
	}
}
