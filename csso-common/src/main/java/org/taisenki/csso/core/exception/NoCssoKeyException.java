package org.taisenki.csso.core.exception;

public class NoCssoKeyException extends AuthenticationException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 115935182258831210L;

	public static final NoCssoKeyException INSTANCE = new NoCssoKeyException();
	
	/**
	 * 异常代码值。
	 */
	public static final String CODE = "NO.CSSO.KEY.CODE";
	
	/**
	 * 异常信息键值，要转换为具体的语言值。
	 */
	public static final String MSG_KEY = "NO.CSSO.KEY.MSG";
	
	public NoCssoKeyException(String code, String msgKey) {
		super(code, msgKey);
	}
	
	public NoCssoKeyException() {
		super(CODE, MSG_KEY);
	}

}
