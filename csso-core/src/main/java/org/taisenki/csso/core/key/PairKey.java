/** 
 * Project Name:csso-core 
 * File Name:PairKey.java 
 * Package Name:org.taisenki.csso.core.key 
 * Date:2017年11月24日下午3:36:05 
 * Copyright (c) 2017, taisenki@dareway.com.cn All Rights Reserved. 
 * 
 */  
  
package org.taisenki.csso.core.key;

import java.io.Serializable;

/** 
 * 用于存储非对称密钥 <br/> 
 *
 * @Date:    2017年11月24日 下午3:36:05 <br/> 
 * @author   taisenki 
 * @version  
 * @since    JDK 1.8
 * @see       
 */
public class PairKey implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8673821624657924001L;	
		
	/**
	 * 应用ID
	 */
	private String appId;
	
	/**
	 * 公钥值。
	 */
	private String pubKey;
	
	/**
	 * 私钥值
	 */
	private String privateKey;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}	

	public String getPubKey() {
		return pubKey;
	}

	public void setPubKey(String pubKey) {
		this.pubKey = pubKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
	
	public CasKey toPubKey(){
		return buildCasKey(pubKey);
	}
	
	public CasKey toPrivateKey(){
		return buildCasKey(privateKey);
	}
	
	private CasKey buildCasKey(String key){
		CasKey caskey = new CasKey();
		caskey.setAppId(appId);
		caskey.setKeyId(appId);
		caskey.setValue(key);
		return caskey;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CasKey [appId=").append(appId)
		  .append(", pubKey=").append(pubKey)
	      .append(", privateKey=").append(privateKey)
		  .append("]");
		return sb.toString();
	}
}
  