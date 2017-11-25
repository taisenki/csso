package org.taisenki.csso.common.cipher.sm4;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bouncycastle.util.encoders.Base64;
import org.taisenki.csso.common.util.BaseUtil;
 
public class SM4Utils
{
	private final static String IV_DEFAULT = "UISwD9fW6cFh9SNS";
    private final String secretKey;
    private final String iv;
    private final boolean hexString;

    public SM4Utils(String secretKey){
    	this(secretKey, IV_DEFAULT, false);
    }
    
    public SM4Utils(String secretKey, boolean hexString){
    	this(secretKey, IV_DEFAULT, hexString);
    }
    
    public SM4Utils(String secretKey, String iv){
    	this(secretKey, iv, false);
    }
    
    public SM4Utils(String secretKey, String iv, boolean hexString){
    	this.secretKey = secretKey;
    	this.iv = iv;
    	this.hexString = hexString;
    }
     
    public String encryptData_ECB(String plainText)
    {
        try
        {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_ENCRYPT;
             
            byte[] keyBytes;
            if (hexString)
            {
                keyBytes = BaseUtil.hexStringToBytes(secretKey);
            }
            else
            {
                keyBytes = secretKey.getBytes();
            }
             
            SM4 sm4 = new SM4();
            sm4.sm4_setkey_enc(ctx, keyBytes);
            byte[] encrypted = sm4.sm4_crypt_ecb(ctx, plainText.getBytes("GBK"));
            String cipherText = Base64.toBase64String(encrypted);
            if (cipherText != null && cipherText.trim().length() > 0)
            {
                Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                Matcher m = p.matcher(cipherText);
                cipherText = m.replaceAll("");
            }
            return cipherText;
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return null;
        }
    }
     
    public String decryptData_ECB(String cipherText)
    {
        try
        {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_DECRYPT;
             
            byte[] keyBytes;
            if (hexString)
            {
                keyBytes = BaseUtil.hexStringToBytes(secretKey);
            }
            else
            {
                keyBytes = secretKey.getBytes();
            }
             
            SM4 sm4 = new SM4();
            sm4.sm4_setkey_dec(ctx, keyBytes);
            byte[] decrypted = sm4.sm4_crypt_ecb(ctx, Base64.decode(cipherText));
            return new String(decrypted, "GBK");
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return null;
        }
    }
     
    public String encryptData_CBC(String plainText)
    {
        try
        {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_ENCRYPT;
             
            byte[] keyBytes;
            byte[] ivBytes;
            if (hexString)
            {
                keyBytes = BaseUtil.hexStringToBytes(secretKey);
                ivBytes = BaseUtil.hexStringToBytes(iv);
            }
            else
            {
                keyBytes = secretKey.getBytes();
                ivBytes = iv.getBytes();
            }
             
            SM4 sm4 = new SM4();
            sm4.sm4_setkey_enc(ctx, keyBytes);
            byte[] encrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, plainText.getBytes("GBK"));
            
            String cipherText = Base64.toBase64String(encrypted);
            if (cipherText != null && cipherText.trim().length() > 0)
            {
                Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                Matcher m = p.matcher(cipherText);
                cipherText = m.replaceAll("");
            }
            return cipherText;
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return null;
        }
    }
     
    public String decryptData_CBC(String cipherText)
    {
        try
        {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_DECRYPT;
             
            byte[] keyBytes;
            byte[] ivBytes;
            if (hexString)
            {
                keyBytes = BaseUtil.hexStringToBytes(secretKey);
                ivBytes = BaseUtil.hexStringToBytes(iv);
            }
            else
            {
                keyBytes = secretKey.getBytes();
                ivBytes = iv.getBytes();
            }
             
            SM4 sm4 = new SM4();
            sm4.sm4_setkey_dec(ctx, keyBytes);
            byte[] decrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, Base64.decode(cipherText));
            return new String(decrypted, "GBK");
        } 
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
