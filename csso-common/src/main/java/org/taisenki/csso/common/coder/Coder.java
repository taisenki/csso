/** 
 * Project Name:csso-common 
 * File Name:Coder.java 
 * Package Name:org.taisenki.csso.common.coder 
 * Date:2017年11月18日下午4:44:47 
 * Copyright (c) 2017, taisenki@dareway.com.cn All Rights Reserved. 
 * 
 */  
  
package org.taisenki.csso.common.coder;

import java.nio.charset.Charset;

import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.encoders.UrlBase64;

/** 
 * 常用编码类 <br/> 
 *
 * @Date:    2017年11月18日 下午4:44:47 <br/> 
 * @author   taisenki 
 * @version  
 * @since    JDK 1.8
 * @see       
 */
public enum Coder {

	HEX{
		public byte[] encode(byte[] data) { return Hex.encode(data); }
		public byte[] decode(byte[] data) { return Hex.decode(data); }
	},
	
	BASE64{
		public byte[] encode(byte[] data) { return Base64.encode(data); }
		public byte[] decode(byte[] data) { return Base64.decode(data); }		
	},
	
	URL_BASE64{
		public byte[] encode(byte[] data) { return UrlBase64.encode(data); }
		public byte[] decode(byte[] data) { return UrlBase64.decode(data); }		
	};
	
	public final static Charset UTF8 = Charset.forName("UTF-8");

	public final static Charset GBK = Charset.forName("GBK");
	
	/** 
	 * encode:对传入数据进行编码操作. <br/> 
	 * 
	 * @author taisenki 
	 * @param src
	 * @return 
	 * @since 
	 */  
	public byte[] encode(byte[] data){
		throw new AbstractMethodError();
	}
	
	public String encode2S(byte[] data){
		return Strings.fromByteArray(encode(data));
	}
	
	public byte[] encodeS(String data){
		return encodeS(data, UTF8);
	}
	
	/** 
	 * encodeS:编码传入字符串. <br/> 
	 * 
	 * @author taisenki 
	 * @param data
	 * @param charset 传入字符串编码
	 * @return 
	 * @since 
	 */  
	public byte[] encodeS(String data, Charset charset){
		return encode(data.getBytes(charset));
	}
	
	public String encodeS2S(String data){
		return encodeS2S(data, UTF8);
	}
	
	public String encodeS2S(String data, Charset charset){
		return encode2S(data.getBytes(charset));
	}
	
	
	
	/** 
	 * decode:对传入数据进行解码操作. <br/> 
	 * 
	 * @author taisenki 
	 * @param data
	 * @return 
	 * @since 
	 */  
	public byte[] decode(byte[] data){
		throw new AbstractMethodError();
	}
	
	public byte[] decodeS(String data){
		return decode(Strings.toByteArray(data));
	}
	
	public String decode2S(byte[] data){
		return decode2S(data, UTF8);
	}
	
	/** 
	 * decode2S:解码为字符串. <br/> 
	 * 
	 * @author taisenki 
	 * @param data
	 * @param charset 返回字符串编码
	 * @return 
	 * @since 
	 */  
	public String decode2S(byte[] data, Charset charset){
		return new String(decode(data), charset);
	}
	
	public String decodeS2S(String data){
		return decodeS2S(data, UTF8);
	}
	
	public String decodeS2S(String data, Charset charset){
		return new String(decodeS(data), charset);
	}
}
  