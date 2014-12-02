
package com.loopj.android.http.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Aes {
    private final static int KEY_LENGTH = 16;
    
    private static byte [] key = null;

    public static byte[] encrypt(byte[] data) {
        byte [] result = data;
        try {
            byte[] key = getKey();
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            result = cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String encrypt(String str) {
        String result = str;
        try {
            byte [] eString = encrypt(str.getBytes("utf-8"));
            result = byte2Hex(eString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] decrypt(byte[] data) {
        byte [] result = data;
        if (data.length % 16 != 0 || data.length == 0) {
            return result;
        }
        byte[] key = getKey();
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            result = cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static String decrypt(String str) {
        String result = str;
        try {
            byte [] encryptBytes = hex2byte(str);
            byte [] eString = decrypt(encryptBytes);
            result = new String(eString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static byte[] getKey() {
        if (key != null) {
            return key;
        }
        key = new byte[KEY_LENGTH];
        int dwHashType = 1;
        long seed1 = 0x7FED7FED, seed2 = 0xEEEEEEEE;
        for (int i = 0; i < KEY_LENGTH; i++) {
            key[i] = (byte) (((dwHashType << 8) + i) ^ (seed1 + seed2));
        }
        return key;
    }
    
    private static String byte2Hex(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    private static byte[] hex2byte(String hexStr) {
        if (hexStr == null) {
            return null;
        }
        int l = hexStr.length();
        if (l % 2 == 1) {
            return null;
        }
        byte[] b = new byte[l / 2];
        for (int i = 0; i != l / 2; i++) {
            b[i] = (byte) Integer.parseInt(hexStr.substring(i * 2, i * 2 + 2),
                    16);
        }
        return b;
    }
}
