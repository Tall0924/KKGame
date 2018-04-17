package com.xtone.game87873.general.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密
 */
public class MD5Utils {

	public static String getMd5Str(String message) {   
    	MessageDigest md = null;
    	String digestCode = null;
        try {   
            md = MessageDigest.getInstance("MD5");  
            md.update(message.getBytes("utf-8"));
             
            digestCode = digest2HexString(md);
        } catch (NoSuchAlgorithmException e) {   
            e.printStackTrace();   
        } catch (UnsupportedEncodingException e) {   
            e.printStackTrace();   
        }finally{
        	md = null;
        }
        return digestCode;   
    }   
	
	public static String digest2HexString(MessageDigest digest) {
    	if(digest==null){
    		return null;
    	}
    	byte[] byteArray = digest.digest();
        StringBuffer md5StrBuff = new StringBuffer();   
  
        for (int i = 0; i < byteArray.length; i++) {   
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)   
                md5StrBuff.append("0").append(   
                        Integer.toHexString(0xFF & byteArray[i]));   
            else  
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));   
        }   
        byteArray = null;
        return md5StrBuff.toString();   
    }  
	
}
