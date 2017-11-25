/** 
 * Project Name:csso-common 
 * File Name:CoderTest.java 
 * Package Name:org.taisenki.csso.common.coder 
 * Date:2017年11月20日下午1:35:12 
 * Copyright (c) 2017, taisenki@dareway.com.cn All Rights Reserved. 
 * 
 */  
  
package org.taisenki.csso.common.coder;  

import static org.taisenki.csso.common.coder.Coder.*;

import org.junit.Test;

/** 
 * Coder test class <br/> 
 *
 * @Date:    2017年11月20日 下午1:35:12 <br/> 
 * @author   taisenki 
 */
public class CoderTest {

	@Test
	public void testHex(){
		String src = "HEX,你好！";
		System.out.printf("HEX src is %s \n", src);
		String enc = HEX.encodeS2S(src);
		System.out.printf("HEX enc is %s \n", enc);
		String dec = HEX.decodeS2S(enc);
		System.out.printf("HEX dec is %s \n", dec);
	}
	
	@Test
	public void testBase64(){
		String src = "BASE64,你好！";
		System.out.printf("BASE64 src is %s \n", src);
		String enc = BASE64.encodeS2S(src);
		System.out.printf("BASE64 enc is %s \n", enc);
		String dec = BASE64.decodeS2S(enc);
		System.out.printf("BASE64 dec is %s \n", dec);
	}
	
	@Test
	public void testUrlBase64(){
		String src = "URLBASE64,你好！";
		System.out.printf("URLBASE64 src is %s \n", src);
		String enc = URL_BASE64.encodeS2S(src);
		System.out.printf("URLBASE64 enc is %s \n", enc);
		String dec = URL_BASE64.decodeS2S(enc);
		System.out.printf("URLBASE64 dec is %s \n", dec);
	}
}
  