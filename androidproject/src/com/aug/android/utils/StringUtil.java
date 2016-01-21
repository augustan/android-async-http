
package com.aug.android.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtil {
    private static final String UTF8 = "utf-8";

    // MD5 加密
    public static String toMd5(String src) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(src.getBytes());
            return toHexString(algorithm.digest(), "");
        } catch (NoSuchAlgorithmException e) {
            return "error";
        }
    }

    public static String toHexString(byte[] bytes, String separator) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            int byteValue = 0xFF & b;
            if (byteValue < 0x10) {
                hexString.append("0" + Integer.toHexString(0xFF & b)).append(separator);
            } else {
                hexString.append(Integer.toHexString(0xFF & b)).append(separator);
            }
        }
        return hexString.toString();
    }

    public static String getImageFileName(String url) {
        String fileName = "";
        if (null != url && url.length() > 7) {
            fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
        }
        return fileName;
    }

    public static String urlEncode(String str) throws UnsupportedEncodingException {
        if (str == null) {
            str = "";
        }
        return URLEncoder.encode(str, UTF8).replaceAll("\\+", "%20").replaceAll("%7E", "~")
                .replaceAll("\\*", "%2A");
    }

    public static String urlDecode(String str) throws UnsupportedEncodingException {
        return URLDecoder.decode(str, UTF8);
    }

}
