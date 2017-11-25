package org.taisenki.csso.web.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.taisenki.csso.core.key.CasKey;
import org.taisenki.csso.core.key.KeyService;

/**
 * 与秘钥相关的web请求处理类，处理查询应用的秘钥等信息。
 *
 */
@Controller
public class KeyAction {

	/**
	 * 秘钥服务。
	 */
	@Autowired
	private KeyService keyService;
	
	public void setKeyService(KeyService keyService) {
		this.keyService = keyService;
	}

	/**
	 * 根据应用ID，查询对应的秘钥信息，非对称密钥，之后需扩展为通过证明才能获取
	 * @param appId 应用ID.
	 * @return 对应的秘钥。
	 */
	@RequestMapping("/fetchApp")
	@ResponseBody
	public CasKey fetchApp(String appId){
		return keyService.findKeyByAppId(appId);
	}
	
	/**
	 * 根据key ID，查询对应的秘钥信息，默认的实现是不加密的，未实现认证，
	 * 请自行增加该服务的安全性。
	 * @param appId 应用ID.
	 * @return 对应的秘钥。
	 */
	@RequestMapping("/fetchKey")
	@ResponseBody
	public CasKey fetchKey(String appId, String keyId){
		CasKey key = keyService.findKeyByKeyId(keyId);
		if (key != null) {
			String encryptKey = keyService.doCrypto(appId, key.getValue());
			key.setValue(encryptKey);
		}
		return key;
	}
}
