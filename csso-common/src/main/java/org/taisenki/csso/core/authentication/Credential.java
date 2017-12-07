/** 
 * Project Name:csso-common 
 * File Name:Credential.java 
 * Package Name:org.taisenki.csso.core.authentication 
 * Date:2017年11月20日下午4:17:31 
 * Copyright (c) 2017, taisenki@163.com All Rights Reserved. 
 * 
 */  
  
package org.taisenki.csso.core.authentication;  

/** 
 * 用户凭据，代表了一个用户的身份，这是一个抽象概念，
 * 这种凭据可以是一个用户名和密码对，也可以是一个加密后的信息，
 * 也可以是一个任何可以识别用户身份的信息。 <br/> 
 *
 * @Date:    2017年11月20日 下午4:17:31 <br/> 
 * @author   taisenki 
 * @version  
 * @since    JDK 1.8
 * @see       
 */
public interface Credential {
	
	/**
	 * 是否原始凭据，即未认证过的原始信息。
	 * @return 是否原始凭据，true：原始凭据，false:加密后的凭据。
	 */
	public boolean isOriginal();

}
  