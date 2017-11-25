package org.taisenki.csso.common.cipher.sm2;

import java.io.IOException;
import java.math.BigInteger;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;
import org.taisenki.csso.common.util.BaseUtil;

public class SM2Utils 
{
	//生成随机秘钥对
	public static String[] generateKeyPair(){
		SM2 sm2 = SM2.Instance();
		AsymmetricCipherKeyPair key = sm2.ecc_key_pair_generator.generateKeyPair();
		ECPrivateKeyParameters ecpriv = (ECPrivateKeyParameters) key.getPrivate();
		ECPublicKeyParameters ecpub = (ECPublicKeyParameters) key.getPublic();
		BigInteger privateKey = ecpriv.getD();
		ECPoint publicKey = ecpub.getQ();
		
		System.out.println("公钥: " + Hex.toHexString(publicKey.getEncoded(false)));
		System.out.println("私钥: " + Hex.toHexString(privateKey.toByteArray()));
		return new String[]{Hex.toHexString(publicKey.getEncoded(false)),Hex.toHexString(privateKey.toByteArray())};
	}
	
	//数据加密
	public static String encrypt(byte[] publicKey, byte[] data) throws IOException
	{
		if (publicKey == null || publicKey.length == 0)
		{
			return null;
		}
		
		if (data == null || data.length == 0)
		{
			return null;
		}
		
		byte[] source = new byte[data.length];
		System.arraycopy(data, 0, source, 0, data.length);
		
		Cipher cipher = new Cipher();
		SM2 sm2 = SM2.Instance();
		ECPoint userKey = sm2.ecc_curve.decodePoint(publicKey);
		
		ECPoint c1 = cipher.Init_enc(sm2, userKey);
		cipher.Encrypt(source);
		byte[] c3 = new byte[32];
		cipher.Dofinal(c3);
		
		//C1 C2 C3拼装成加密字串
		return Hex.toHexString(c1.getEncoded(false)) + Hex.toHexString(source) + Hex.toHexString(c3);
		
	}
	
	public static byte[] decrypt(String privateKey, String endata) throws IOException{

		if (privateKey == null || privateKey.length() == 0)
		{
			return null;
		}
		
		if (endata == null || endata.length() == 0)
		{
			return null;
		}
		
		return decrypt(privateKey, Hex.decode(endata));
	}
	
	//数据解密
	public static byte[] decrypt(String privateKey, byte[] encryptedData) throws IOException
	{
		/***分解加密字节组
		 * （C1 = C1标志位1位 + C1实体部分64位 = 65）
		 * （C3 = C3实体部分32位  = 32）
		 * （C2 = encryptedData.length  - C1长度  - C3长度）
		 */
		int c2Len = encryptedData.length - 65 - 32;
		
		byte[] c1Bytes = BaseUtil.subByte(encryptedData, 0, 65);
		byte[] c2 = BaseUtil.subByte(encryptedData, 65, c2Len);
		byte[] c3 = BaseUtil.subByte(encryptedData, 65 + c2Len, 32);
		
		SM2 sm2 = SM2.Instance();
		BigInteger userD = new BigInteger(privateKey, 16);		
		
		//通过C1实体字节来生成ECPoint
		ECPoint c1 = sm2.ecc_curve.decodePoint(c1Bytes);
		Cipher cipher = new Cipher();
		cipher.Init_dec(userD, c1);
		cipher.Decrypt(c2);
		
		//通过c3部分进行校验
		byte[] c31 = new byte[32];
		cipher.Dofinal(c31);		
		
		System.out.println(new String(c2));
		assert (Arrays.areEqual(c3, c31));		
		
		//返回解密结果
		return (Arrays.areEqual(c3, c31)?c2: new byte[0]);
	}
}
