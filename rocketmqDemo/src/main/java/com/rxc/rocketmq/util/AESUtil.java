package com.rxc.rocketmq.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
	  private static final String ALGORITHM = "AES";
	    private static final String SECRET_KEY = "1234567890123456"; // 密钥，16位
	 
	    public static String decrypt(String encrypted) {
	       try {
				SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
		        Cipher cipher = Cipher.getInstance(ALGORITHM);
		        cipher.init(Cipher.DECRYPT_MODE, keySpec);
		        byte[] original = cipher.doFinal(hexToByteArray(encrypted));
		        return new String(original);
			} catch (Exception e) {
				 return encrypted;
			}
	    }
	 
	    // 从十六进制字符串到字节数组转换
	    public static byte[] hexToByteArray(String hex) {
	        if (hex == null || hex.length() == 0) {
	            return null;
	        }
	        byte[] ba = new byte[hex.length() / 2];
	        for (int i = 0; i < ba.length; i++) {
	            ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
	        }
	        return ba;
	    }
}
