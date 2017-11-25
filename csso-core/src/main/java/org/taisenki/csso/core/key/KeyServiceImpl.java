package org.taisenki.csso.core.key;

import static org.taisenki.csso.common.coder.Coder.HEX;
import static org.taisenki.csso.common.coder.Coder.BASE64;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.taisenki.csso.common.cipher.sm2.SM2Utils;
import org.taisenki.csso.core.dao.fs.FileSystemDao;
import org.taisenki.csso.core.exception.CryptoException;
import org.taisenki.csso.core.exception.NoCssoKeyException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

/**
 * 默认的key管理实现类，从classpath:/keys.js文件中
 * 读取key配置信息，是以json格式存储的。
 *
 */
public class KeyServiceImpl extends FileSystemDao implements KeyService {
	
	private static Logger logger = Logger.getLogger(KeyServiceImpl.class.getName());
	
	/**
	 * 外部数据文件地址，优先级更高。
	 * (用户可以配置)
	 */
	public static final String  DEFAULT_EXTERNAL_DATA =  "。\\resources\\keys.js";
	
	/**
	 * 默认的数据文件地址，在classpath下。
	 */
	public static final String DEFAULT_CLASSPATH_DATA = "keys.js";
	
    private KeyManager keyManager = null;
    	
	/**
	 * 秘钥映射表，key是appId,value是Key对象。
	 */
	private Map<String, PairKey> appIdMap = null;
	
	public KeyServiceImpl(){
		this.externalData = DEFAULT_EXTERNAL_DATA;
		this.classPathData = DEFAULT_CLASSPATH_DATA;
		//加载数据。
		loadAppData();
	}	
	
	public KeyManager getKeyManager() {
		return keyManager;
	}

	public void setKeyManager(KeyManager keyManager) {
		this.keyManager = keyManager;
	}

	@Override
	protected void loadAppData(){
		try{
			String s = this.readDataFromFile();
			//将读取的应用列表转换为应用map。
			List<PairKey> keys = JSON.parseObject(s, new TypeReference<List<PairKey>>(){});
			if(keys!=null){
				appIdMap = new HashMap<String, PairKey>(keys.size());
				for(PairKey key:keys){
					appIdMap.put(key.getAppId(), key);
				}
				keys = null;
			}
		}catch (Exception e) {
			logger.log(Level.SEVERE, "load app data file error.", e);
		}
	}

	@Override
	public CasKey findKeyByKeyId(String keyId) {		
		return keyManager.findTokenKey(keyId);
	}
	
	@Override
	public CasKey findKeyByAppId(String appId) {
		//loadAppData();	//重新加载数据
		if(this.appIdMap!=null){
			PairKey pk = this.appIdMap.get(appId);			
			return pk.toPrivateKey();
		}
		return null;
	}
	
	/**
	 * 使用应用公钥将key加密
	 * @param appId 
	 * @param keyValue 需要加密的key
	 * @return 加密后的key
	 */
	@Override
	public String doCrypto(String appId, String str) {
		String encryptKey = null;
		PairKey key = this.appIdMap.get(appId);
		if(key != null){
			try {
				byte[] pubkey = HEX.decodeS(key.getPubKey());
				encryptKey = SM2Utils.encrypt(pubkey, BASE64.encodeS(str));
			} catch (Exception e) {
				throw new CryptoException(e.getMessage());
			}
		}else{
			throw NoCssoKeyException.INSTANCE;
		}
		return encryptKey;
	}

	@Override
	public void close() throws IOException {
		if(keyManager != null){
			keyManager.close();
			keyManager = null;
		}
	}
}
