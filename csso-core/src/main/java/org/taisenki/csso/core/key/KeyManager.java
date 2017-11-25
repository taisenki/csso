/** 
 * Project Name:csso-core 
 * File Name:KeyManager.java 
 * Package Name:org.taisenki.csso.core.key 
 * Date:2017年11月22日下午1:56:58 
 * Copyright (c) 2017, taisenki@dareway.com.cn All Rights Reserved. 
 * 
 */  
  
package org.taisenki.csso.core.key;

import java.io.Closeable;

import org.taisenki.csso.core.exception.BuildTokenKeyException;

/** 
 * key的生成、管理接口 <br/> 
 *
 * @Date:    2017年11月22日 下午1:56:58 <br/> 
 * @author   taisenki 
 * @version  
 * @since    JDK 1.8
 * @see       
 */
public interface KeyManager extends Closeable{
	
	/** 
	 * buildTokenKey:获取新的Token加密密钥. <br/> 
	 * 
	 * @author taisenki 
	 * @param appId
	 * @param userId
	 * @param expire 过期时间
	 * @return
	 * @throws BuildTokenKeyException 
	 * @since 
	 */  
	public CasKey buildTokenKey(String appId, String userId, long expire) throws BuildTokenKeyException;
	
	/** 
	 * findTokenKey:根据keyId获得Token key. <br/> 
	 * 
	 * @author taisenki 
	 * @param keyId
	 * @return 
	 * @since 
	 */  
	public CasKey findTokenKey(String keyId);

}