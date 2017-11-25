package org.taisenki.csso.core.exception;

public class BuildTokenKeyException extends AuthenticationException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 115935182258831210L;

	public static final BuildTokenKeyException INSTANCE = new BuildTokenKeyException();
	
	/**
	 * 异常代码值。
	 */
	public static final String CODE = "ERROR.TOKEN.KEY.CODE";
	
	/**
	 * 异常信息键值，要转换为具体的语言值。
	 */
	public static final String MSG_KEY = "ERROR.TOKEN.KEY.MSG";
	
	public BuildTokenKeyException(String code, String msgKey) {
		super(code, msgKey);
	}
	
	public BuildTokenKeyException() {
		super(CODE, MSG_KEY);
	}

}
