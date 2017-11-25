package org.taisenki.csso.core.exception;

/** 
 * 加解密相关异常 <br/> 
 *
 * @date: 2017年11月23日 上午11:43:31
 * @author taisenki 
 * @version  
 * @since 
 */  
public class CryptoException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CryptoException(String message){
		super(message);
	};
}
