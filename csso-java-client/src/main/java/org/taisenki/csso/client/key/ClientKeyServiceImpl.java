/** 
 * Project Name:csso-java-client 
 * File Name:ClientKeyServiceImpl.java 
 * Package Name:com.github.ebnew.csso.client.key 
 * Date:2017年11月17日上午11:38:31 
 * Copyright (c) 2017, taisenki@dareway.com.cn All Rights Reserved. 
 * 
 */  
  
package org.taisenki.csso.client.key;

import static org.taisenki.csso.common.coder.Coder.BASE64;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.taisenki.csso.common.cipher.sm2.SM2Utils;
import org.taisenki.csso.common.util.BaseUtil;
import org.taisenki.csso.core.exception.CryptoException;
import org.taisenki.csso.core.exception.NoCssoKeyException;
import org.taisenki.csso.core.key.CasKey;
import org.taisenki.csso.core.key.KeyService;

import com.alibaba.fastjson.JSON;

/** 
 * 默认的客户端keyService接口实现，与应用app为1-1 <br/> 
 *
 * @Date:    2017年11月17日 上午11:38:31 <br/> 
 * @author   taisenki 
 * @version  
 * @since    JDK 1.8
 * @see       
 */
public class ClientKeyServiceImpl implements KeyService {

	private static final Logger logger = LoggerFactory.getLogger(ClientKeyServiceImpl.class
			.getName());

	private static final String DEFAULT_APP = "fetchApp.do";
	private static final String DEFAULT_KEY = "fetchKey.do";
	
	
	private String cssoFetchKeyUrl;
	
	private String cssoFetchTokenKeyUrl;
	/**
	 * 本应用的秘钥信息。
	 */
	private CasKey casKey;

	/**
	 * 本应用的应用id.
	 */
	private String appId;

	private static CloseableHttpClient httpClient;

    static {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(60000).setSocketTimeout(15000).build();
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }
	
	//默认无参构造方法
	public ClientKeyServiceImpl(){
		
	}
	
	public ClientKeyServiceImpl(String cssoServerUrl, String appId) {
		this(cssoServerUrl+DEFAULT_APP, cssoServerUrl+DEFAULT_KEY, appId);
	}
	
	public ClientKeyServiceImpl(String cssoFetchKeyUrl, String cssoFetchTokenKeyUrl, String appId) {
		super();
		this.cssoFetchKeyUrl = cssoFetchKeyUrl;
		this.cssoFetchTokenKeyUrl = cssoFetchTokenKeyUrl;
		this.appId = appId;
	}

	@Override
	public CasKey findKeyByKeyId(String keyId) {
		if(BaseUtil.isEmpty(keyId))
			return null;
		return fetchTokenKeyFromCssoServer(keyId);
	}
	
	private boolean checkAppId(String appId){
		return (appId == null || !appId.equals(this.appId));
	}

	@Override
	public CasKey findKeyByAppId(String appId) {
		if(checkAppId(appId)){
			return null;
		}
		
		if (this.casKey == null) {
			this.casKey = fetchKeyFromCssoServer();
		}
		
		if(this.casKey == null){
			throw NoCssoKeyException.INSTANCE;
		}
		return casKey;
	}
	
	private CasKey fetchKeyFromCssoServer() {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("appId", this.appId));
		logger.debug("==== getAppKey提交参数 ======{}", nvps);
		CasKey csso = null;
		try {
			String content = requestUrl(cssoFetchKeyUrl, nvps);
			if(content != null){
				csso = JSON.parseObject(content, CasKey.class);
				return csso;
			}
		} catch (Exception e) {
			logger.error("fetch app key from server error, the url is [{}]", cssoFetchKeyUrl, e);
		} 
		return null;
	}
	
	private CasKey fetchTokenKeyFromCssoServer(String keyId) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("appId", this.appId));
		nvps.add(new BasicNameValuePair("keyId", keyId));
        logger.debug("==== getTokenKey提交参数 ======{}", nvps);
		CasKey csso = null;
		try {
			String content = requestUrl(cssoFetchTokenKeyUrl, nvps);
			if(content != null){
				csso = JSON.parseObject(content, CasKey.class);
				csso.setValue(doCrypto(this.appId, csso.getValue()));
				return csso;
			}
		} catch (Exception e) {
			logger.error("fetch token key from server error, the url is [{}]", cssoFetchTokenKeyUrl, e);
		} 
		return null;
	}
	
	/** 
	 * requestUrl:请求某个URL，带着参数列表。. <br/> 
	 * 
	 * @author taisenki 
	 * @param url
	 * @param nameValuePairs
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException 
	 * @since 
	 */  
	protected String requestUrl(String url, List<NameValuePair> nameValuePairs) throws ClientProtocolException, IOException {
		HttpPost httpPost = new HttpPost(url);
		if(nameValuePairs!=null && nameValuePairs.size()>0){
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		}
		CloseableHttpResponse response = httpClient.execute(httpPost);  
		try {  
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				String content = EntityUtils.toString(entity);
				EntityUtils.consume(entity);
				return content;
			}
			else{
				logger.warn("request the url: {} , but return the status code is {}, error: {}", url, 
						response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
				
				return null;
			}
		}
		finally{
			response.close();
			httpPost.releaseConnection();
		}
	}

	@Override
	public String doCrypto(String appId, String str) {
		if(checkAppId(appId)){
			throw new CryptoException("Can not decrypt cause of invaild appId:"+appId);
		}
		if(this.casKey == null){
			findKeyByAppId(appId);
		}
		String decryptKey = null;
		try {
			byte[] decdata = SM2Utils.decrypt(this.casKey.toSecurityKey(), str);
			decryptKey = BASE64.decode2S(decdata);
		} catch (Exception e) {
			throw new CryptoException(e.getMessage());
		}
		return decryptKey;
	}

	@Override
	public void close() throws IOException {
		if(httpClient != null){
			httpClient.close();
		}		
	}

}
  