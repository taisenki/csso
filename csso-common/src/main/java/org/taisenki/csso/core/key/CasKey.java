/** 
 * Project Name:csso-common 
 * File Name:CasKey.java 
 * Package Name:org.taisenki.csso.core.key 
 * Date:2017年11月20日下午3:45:00 
 * Copyright (c) 2017, taisenki@163.com All Rights Reserved. 
 * 
 */  
  
package org.taisenki.csso.core.key;

import java.io.Serializable;

import org.taisenki.csso.common.util.BaseUtil;

/** 
 * 非对称密钥类 <br/> 
 *
 * @Date:    2017年11月20日 下午3:45:00 <br/> 
 * @author   taisenki 
 * @version  
 * @since    JDK 1.8
 * @see       
 */
public class CasKey implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8673821624657924001L;	
	
	/**
	 * 秘钥ID
	 */
	private String keyId;	
	
	/**
	 * 应用ID
	 */
	private String appId;
	
	/**
	 * 用户ID
	 */
	private String userId;
	
	/**
	 * 秘钥值。
	 */
	private String value;
	
	/**
	 * 私钥文件存放路径
	 */
	private String keyPath;

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	public String getKeyPath() {
		return keyPath;
	}

	public void setKeyPath(String keyPath) {
		this.keyPath = keyPath;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * 将本对象转换为对应的key对象。
	 * @return Key对象。
	 * @throws Exception 
	 */
	public String toSecurityKey() throws Exception{
		return value;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CasKey [keyId=").append(keyId).append(", appId=").append(appId);
		if(!BaseUtil.isEmpty(userId)) sb.append(", userId=").append(userId);
		sb.append(", value=").append(value);
		if(!BaseUtil.isEmpty(keyPath)) sb.append(",keyPath=").append(keyPath);
		sb.append("]");
		return sb.toString();
	}
}
  