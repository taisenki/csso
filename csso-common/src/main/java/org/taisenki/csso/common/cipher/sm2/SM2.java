package org.taisenki.csso.common.cipher.sm2;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class SM2 
{
	
	//正式参数
	public static String[] ecc_param = { 
		"FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF",
		"FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC",
		"28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93",
		"FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123",
		"32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7",
		"BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0"
	};

	public static SM2 Instance() 
	{
		return new SM2();
	}

	public final BigInteger ecc_p;
	public final BigInteger ecc_a;
	public final BigInteger ecc_b;
	public final BigInteger ecc_n;
	public final BigInteger ecc_gx;
	public final BigInteger ecc_gy;
	public final ECCurve ecc_curve;
	public final ECPoint ecc_point_g;
	public final ECDomainParameters ecc_bc_spec;
	public final ECKeyPairGenerator ecc_key_pair_generator;

	public SM2() 
	{
		this.ecc_p = new BigInteger(ecc_param[0], 16);
		this.ecc_a = new BigInteger(ecc_param[1], 16);
		this.ecc_b = new BigInteger(ecc_param[2], 16);
		this.ecc_n = new BigInteger(ecc_param[3], 16);
		this.ecc_gx = new BigInteger(ecc_param[4], 16);
		this.ecc_gy = new BigInteger(ecc_param[5], 16);

		this.ecc_curve = new ECCurve.Fp(this.ecc_p, this.ecc_a, this.ecc_b);
		this.ecc_point_g = ecc_curve.createPoint(this.ecc_gx, this.ecc_gy);

		this.ecc_bc_spec = new ECDomainParameters(this.ecc_curve, this.ecc_point_g, this.ecc_n);

		ECKeyGenerationParameters ecc_ecgenparam;
		ecc_ecgenparam = new ECKeyGenerationParameters(this.ecc_bc_spec, random());

		this.ecc_key_pair_generator = new ECKeyPairGenerator();
		this.ecc_key_pair_generator.init(ecc_ecgenparam);
	}
	
	private final static byte[] keyBytes = { 0x11, 0x22, 0x4F, 0x58,
			(byte) 0x88, 0x10, 0x40, 0x38, 0x28, 0x25, 0x79,
			0x51, (byte) 0xCB, (byte) 0xDD, 0x55, 0x66, 0x77,
			0x29, 0x74, (byte) 0x98, 0x30, 0x40, 0x36,
			(byte) 0xE2 };// 24字节的密钥
	
	public static SecureRandom random(){
		SecureRandom ss;
		try {
			ss = SecureRandom.getInstance("SHA1PRNG");
//			ss.setSeed(keyBytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			ss = new SecureRandom(keyBytes);
		}
		return ss;
	}
}

