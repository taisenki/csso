/** 
 * Project Name:csso-core 
 * File Name:KeyManagerImpl.java 
 * Package Name:org.taisenki.csso.core.key 
 * Date:2017年11月22日下午4:49:48 
 * Copyright (c) 2017, taisenki@163.com All Rights Reserved. 
 * 
 */  
  
package org.taisenki.csso.core.key;

import static org.taisenki.csso.common.coder.Coder.BASE64;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.UUID;

import org.bouncycastle.jcajce.provider.digest.SM3;
import org.taisenki.csso.core.exception.BuildTokenKeyException;
import org.taisenki.csso.core.thread.TimeLimitBag;

/** 
 * KeyManager接口的默认实现类，使用默认的keyCache <br/> 
 *
 * @Date:    2017年11月22日 下午4:49:48 <br/> 
 * @author   taisenki 
 * @version  
 * @since    JDK 1.8
 * @see       
 */
public class DefaultKeyManagerImpl implements KeyManager {	

	/**
	 * 秘钥映射表，key是keyId,value是Key对象。
	 */
	private TimeLimitBag<String, CasKey> keyCache = null;
	
	private Thread hook = null;
	
	public DefaultKeyManagerImpl(){
		keyCache = new TimeLimitBag<String, CasKey>();
		// 资源回收注册
		hook = new Thread(new Runnable() {			
			@Override
			public void run() {
				try {
					if(keyCache != null)
						keyCache.close();
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
		});
		Runtime.getRuntime().addShutdownHook(hook);
	}
	
	/** 
	 * InitializeCache:初始化超时keyCache. <br/>
	 * 默认实现为${@link org.taisenki.csso.core.thread.TimeLimitBag}使用内存存储
	 * 
	 * @author taisenki 
	 * @return 
	 * @since 
	 */  
	protected TimeLimitBag<String, CasKey> InitializeCache(){
		return new TimeLimitBag<String, CasKey>();
	}
	
	/** 
	 * makeMd:制作信息摘要. <br/> 
	 * 
	 * @author taisenki 
	 * @param appId
	 * @param userId
	 * @return 
	 * @since 
	 */  
	protected String makeMd(String appId, String userId){
		MessageDigest digest = getMessageDigest();
		
		digest.update(BASE64.encodeS(appId));
		digest.update(BASE64.encodeS(userId));
		digest.update(BASE64.encodeS(System.currentTimeMillis()+""));
		
		return BASE64.encode2S(digest.digest());		
	}
	
	/** 
	 * getMessageDigest:获取信息摘要算法. <br/> 
	 * 
	 * @author taisenki 
	 * @return 
	 * @since 
	 */  
	protected MessageDigest getMessageDigest(){
		return new SM3.Digest();
	}

	@Override
	public CasKey buildTokenKey(String appId, String userId, long expire) throws BuildTokenKeyException {
		CasKey key = new CasKey();
		// 身份摘要 作为Token的密钥
		String md = makeMd(appId, userId);
		
		//默认存放到缓存中
		String keyId = UUID.randomUUID().toString();
		key.setKeyId(keyId);
		key.setAppId(appId);
		key.setUserId(userId);
		key.setValue(md);
		keyCache.put(keyId, key, expire);
		return key;
	}

	@Override
	public CasKey findTokenKey(String keyId) {
		return keyCache.get(keyId);
	}

	@Override
	public void close() throws IOException {
		if(this.keyCache != null){
			this.keyCache.close();		
			this.keyCache = null;
			Runtime.getRuntime().removeShutdownHook(hook);
		}
	}

}
  