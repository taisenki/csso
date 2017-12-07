/** 
 * Project Name:csso-common 
 * File Name:SM2Test.java 
 * Package Name:org.taisenki.csso.common.cipher 
 * Date:2017年11月20日下午1:29:20 
 * Copyright (c) 2017, taisenki@163.com All Rights Reserved. 
 * 
 */  
  
package org.taisenki.csso.common.cipher;

import java.security.SecureRandom;

import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECCurve;
import org.junit.Test;
import org.taisenki.csso.common.util.BaseUtil;

/** 
 * Test SM2 <br/> 
 *
 * @Date:    2017年11月20日 下午1:29:20 <br/> 
 * @author   taisenki 
 */
public class SM2Test {

	@Test
	public void testBCSm2() {
		// BC中已经定义好的SM2曲线
		ECCurve localECCurve = GMNamedCurves.getByName("sm2p256v1").getCurve();
		// 公钥der格式有前缀公钥名称、公钥参数，后面是XY，xy前面必须有04，表示是x和y，03表示x压缩，02表示x不压缩
		String s1 = "04F58EFDF08811BC76F52D08ABB6B62D2F9C3BFD5FACDAA7B6A7660F52236A5B8A2A3712FB6ACC90BE0519217BAED6EA47A1146610575924CCAB30B148FCEAE220";
		// 构造公钥点ECPoint
		X9ECPoint localX9ECPoint = new X9ECPoint(localECCurve, BaseUtil.hexStringToBytes(s1));
		System.out.println(localX9ECPoint.getPoint());
		// 构造domainParams，都可以从定义好的SM2曲线中获得
		ECDomainParameters domainParams = new ECDomainParameters(GMNamedCurves.getByName("sm2p256v1").getCurve(), GMNamedCurves
				.getByName("sm2p256v1").getG(), GMNamedCurves.getByName("sm2p256v1").getN());
		// 有公钥点ECPoint和domainParams构造成公钥参数
		ECPublicKeyParameters pk = new ECPublicKeyParameters(localX9ECPoint.getPoint(), domainParams);
		System.out.println(pk.getQ());

		// 利用BC提供的lightweight  API进行加密
		SM2Engine sm2Engine = new SM2Engine();
		byte[] m = "encryption standard中午".getBytes();
		sm2Engine.init(true, new ParametersWithRandom(pk, new SecureRandom()));
		try {
			byte[] enc = sm2Engine.processBlock(m, 0, m.length);
			System.out.println("enc:" + BaseUtil.byteToHex(enc));
		} catch (InvalidCipherTextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
  