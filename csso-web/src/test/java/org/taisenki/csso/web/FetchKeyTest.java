/** 
 * Project Name:csso-web 
 * File Name:FetchKeyTest.java 
 * Package Name:org.taisenki.csso.web 
 * Date:2017年11月24日下午2:16:49 
 * Copyright (c) 2017, taisenki@163.com All Rights Reserved. 
 * 
 */  
  
package org.taisenki.csso.web;

import org.junit.Test;
import org.taisenki.csso.common.cipher.sm2.SM2Utils;
import org.taisenki.csso.core.key.CasKey;
import org.taisenki.csso.core.key.DefaultKeyManagerImpl;
import org.taisenki.csso.core.key.KeyManager;
import org.taisenki.csso.core.key.KeyServiceImpl;
import org.taisenki.csso.web.action.KeyAction;

/** 
 * 测试加密相关逻辑 <br/> 
 *
 * @Date:    2017年11月24日 下午2:16:49 <br/> 
 * @author   taisenki 
 * @version  
 * @since    JDK 1.8
 * @see       
 */
public class FetchKeyTest {

	@Test
	public void testFetchKey(){
		String appId = "1001";
		
		KeyManager keym = new DefaultKeyManagerImpl();
		CasKey key = keym.buildTokenKey(appId, "001", 60);
		System.out.println(key);
		
		KeyServiceImpl keys = new KeyServiceImpl();
		keys.setKeyManager(keym);
		KeyAction keya = new KeyAction();
		keya.setKeyService(keys);
		
		CasKey keye = keya.fetchKey(appId, key.getKeyId());
		System.out.println(keye);
	}
	
	@Test
	public void testKeyFile(){
//		SM2Utils.generateKeyPair();		
	}
}
  