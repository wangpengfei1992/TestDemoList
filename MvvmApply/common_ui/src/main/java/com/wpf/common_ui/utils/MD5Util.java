package com.anker.common.utils;

import java.security.MessageDigest;

/**
 * Created by anker on 2020/11/5.
 *
 * @author nina.ma
 */
public class MD5Util {
    private MD5Util() {
    }

    /**
     * md5 encrypt
     *
     * @param str
     * @param isUp
     * @return
     */
    public static String md5Encrypt(String str, boolean isUp) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = (md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        if (isUp) {
            return (hexValue.toString()).toUpperCase();
        } else {
            return hexValue.toString();
        }
    }
}
