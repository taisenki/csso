package org.taisenki.csso.core.key;

import java.io.Closeable;

import org.taisenki.csso.core.exception.CryptoException;

/** 
 * 密钥服务接口 <br/>
 * 除非分布式实现，最好一个应用只实例化一个 
 *
 * @date: 2017年11月22日 下午1:57:42
 * @author taisenki 
 * @version  
 * @since 
 */  
public interface KeyService extends Closeable{
	
	/**
	 * 根据密钥ID查找对应的密钥信息。<br/>
	 * 该方法返回加密后的对称密钥
	 * 
	 * @param keyId 密钥ID.
	 * @return 密钥信息。
	 */
	public CasKey findKeyByKeyId(String keyId);
	
	/**
	 * 根据应用ID查找对应的密钥信息。<br/>
	 * 该方法返回非对称密钥对之一
	 * 
	 * @param appId 应用ID.
	 * @return 密钥信息。
	 */
	public CasKey findKeyByAppId(String appId);
	
	/** 
	 * doCrypt:使用app密钥对字符串进行加解密处理. <br/> 
	 * 
	 * @author taisenki 
	 * @param appId
	 * @param str
	 * @throws CryptoException 
	 * @since 
	 */  
	public String doCrypto(String appId, String str) throws CryptoException;
}
