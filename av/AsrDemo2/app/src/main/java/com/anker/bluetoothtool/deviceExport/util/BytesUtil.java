//
// * Copyright © 2015-2018 Anker Innovations Technology Limited All Rights Reserved.
// * The program and materials is not free. Without our permission, any use, including but not
// limited to reproduction, retransmission, communication, display, mirror, download,
// modification, is expressly prohibited. Otherwise, it will be pursued for legal liability.

//
package com.anker.bluetoothtool.deviceExport.util;

import android.text.TextUtils;

import com.anker.bluetoothtool.deviceExport.util.LogUtil;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by ocean on 2018/1/12.
 */

public class BytesUtil {

    public static final String TAG = BytesUtil.class.getSimpleName();

    private BytesUtil() {
    }

    /**
     * byte array to hex string
     *
     * @param bytes
     * @param length
     * @return
     */
    public static String bytesToHexString(byte[] bytes, int length) {

        if (bytes == null) return null;

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int v = bytes[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * byte array to hex string
     *
     * @param bytes
     */
    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return bytesToHexString(bytes, bytes.length);
    }

    /**
     * byte array to hex string
     *
     * @param bytes
     * @param length
     * @return
     */
    public static String bytesToHexString(Byte[] bytes, int length) {
        if (bytes == null) return null;

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int v = bytes[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }

    /**
     * byte to hex string
     *
     * @param data
     * @return
     */
    public static String byteToHexString(byte data) {

        StringBuilder stringBuilder = new StringBuilder();
        int v = data & 0xFF;
        String hv = Integer.toHexString(v);
        if (hv.length() < 2) {
            stringBuilder.append(0);
        }
        stringBuilder.append(hv);
        return stringBuilder.toString().toUpperCase();
    }

    /**
     * decode hex string to string
     *
     * @param bytes
     * @return
     */
    public static String decodeHexString(String bytes) {

        int length = bytes.length() / 2;
        byte[] data = new byte[length];

        for (int i = 0; i < length; i++) {

            int index = i * 2;
            int v = Integer.parseInt(bytes.substring(index, index + 2), 16);
            data[i] = (byte) v;
        }

        return new String(data);
    }

    /**
     * 16 hex string to byte[]
     *
     * @param str
     * @return
     */
    public static byte[] toBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }

    public static int byteToInt(byte data) {

        return data & 0xFF;
    }

    public static byte[] intToByteArray(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static byte[] intToByteArrayLH(int a) {
        return new byte[]{
                (byte) (a & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 24) & 0xFF)
        };
    }

    public static byte[] longToByteArray(long a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    public static byte[] longToByteArrayLH(long a) {
        return new byte[]{
                (byte) (a & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 24) & 0xFF)
        };
    }

    public static byte[] shortToByteArrayLH(long a) {
        return new byte[]{
                (byte) (a & 0xFF),
                (byte) ((a >> 8) & 0xFF),
        };
    }

    public static byte[] longToReversalByteArray(long a) {
        return new byte[]{
                (byte) (a & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 24) & 0xFF),
        };
    }

    public static short getShort(byte[] buf, boolean bBigEnding) {
        if (buf == null) {
            throw new IllegalArgumentException("byte array is null!");
        }
        if (buf.length > 2) {
            throw new IllegalArgumentException("byte array size > 2 !");
        }
        short r = 0;
        if (bBigEnding) {
            for (int i = 0; i < buf.length; i++) {
                r <<= 8;
                r |= (buf[i] & 0x00ff);
            }
        } else {
            for (int i = buf.length - 1; i >= 0; i--) {
                r <<= 8;
                r |= (buf[i] & 0x00ff);
            }
        }

        return r;
    }

    public static int getIntByBytes(byte[] buf, boolean bBigEnding) {
        int intByteCount = 4;
        if (buf == null) {
            throw new IllegalArgumentException("byte array is null!");
        }
        if (buf.length > intByteCount) {
            throw new IllegalArgumentException("byte array size > 2 !");
        }
        int r = 0;
        if (bBigEnding) {
            for (int i = 0; i < buf.length; i++) {
                r <<= 8;
                r |= (buf[i] & 0x00ff);
            }
        } else {
            for (int i = buf.length - 1; i >= 0; i--) {
                r <<= 8;
                r |= (buf[i] & 0x00ff);
            }
        }

        return r;
    }

    public static short getShort(byte low, byte hight) {
        short r = 0;
        r |= (hight & 0x00ff);
        r <<= 8;
        r |= (low & 0x00ff);

        return r;
    }

    public static int getInt(byte low, byte hight) {
        int r = 0;
        r |= (hight & 0x00ff);
        r <<= 8;
        r |= (low & 0x00ff);

        return r;
    }

    public static byte[] cutOutBytes(byte[] data1, int startPostion, int length) {
        if (startPostion < 0) {
            startPostion = 0;
        }
        if (length <= 0) {
            return null;
        }

        byte[] data2 = new byte[length];
        System.arraycopy(data1, startPostion, data2, 0, length);
//        LogUtil.v("cutOutBytes data1.length " + data1.length + "  data2.length " + data2.length);
        return data2;
    }

    public static int byteWithSignToInt(byte data) {

        int result = data & 0xFF;
        if (result > (0x7f)) {
            result = result - 256;
        }
        return result;
    }

    public static byte intWithSignTobyte(int data) {
        byte result;
        if (data > 127) {
            result = (byte) 0;
        } else if (data < -128) {
            result = (byte) 0;
        } else {
            result = (byte) data;
        }
        return result;
    }

    public static byte intToByte(int data) {

        return (byte) (data & 0xFF);
    }

    public static Byte[] byteArrToByteArr(byte[] data) {
        if (data == null || data.length <= 0) {
            return null;
        }
        Byte[] bytes = new Byte[data.length];
        for (int i = 0; i < data.length; i++) {
            bytes[i] = data[i];
        }
        return bytes;
    }

    /**
     * string 转ascii
     *
     * @param string
     * @return
     */
    public static byte[] stringConvertToASCII(String string) {

        char[] ch = string.toCharArray();
        byte[] bytes = new byte[ch.length];
        for (int i = 0; i < ch.length; i++) {
            bytes[i] = intToByte(Integer.valueOf(ch[i]).intValue());
        }
        return bytes;
    }

    /**
     * string 转 byte[]
     *
     * @param string
     * @param charsetName 编码格式
     * @return
     */
    public static byte[] stringToBytes(String string, String charsetName) {
        if (TextUtils.isEmpty(string)) {
            return new byte[0];
        }
        try {
            return string.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * byte[] 转string
     *
     * @param bytes
     * @param charsetName
     * @return
     */
    public static String bytesToString(byte[] bytes, String charsetName) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        try {
            return new String(bytes, charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 将bytes 转成Ascii 字符串
     *
     * @param bytes
     * @param offset
     * @param dateLen
     */
    public static String bytesToAscii(byte[] bytes, int offset, int dateLen) {
        if ((bytes == null) || (bytes.length == 0) || (offset >= bytes.length) || (dateLen <= 0)) {
            return "";
        }
        if (offset < 0) offset = 0;
        String asciiStr = "";
        try {
            for (int i = offset; i < Math.min(bytes.length, offset + dateLen); i++) {
                asciiStr += (char) bytes[i];
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "bytesToAscii(): bytes to asscii error");
        }
        return asciiStr;
    }

    /**
     * 将bytes 转成Ascii 字符串
     *
     * @param bytes
     */
    public static String bytesToAscii(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        return bytesToAscii(bytes, 0, bytes.length);
    }


    /**
     * @param bytes
     * @param from
     * @param to
     * @return
     */
    public static byte[] copyOfRange(byte[] bytes, int from, int to) {
        int newLength = to - from;
        return copyOfRange(bytes, from, to, new byte[newLength < 0 ? 0 : newLength]);
    }

    /**
     * @param bytes
     * @param from
     * @param to
     * @param defaultDatas
     * @return
     */
    public static byte[] copyOfRange(byte[] bytes, int from, int to, byte[] defaultDatas) {
        int newLength = to - from;
        if (newLength <= 0 || bytes == null || bytes.length == 0 || from >= bytes.length) {
            return defaultDatas;
        }

        //
        if (to > bytes.length && defaultDatas != null) {
            return defaultDatas;
        }

        if (to >= bytes.length && defaultDatas == null) {
            byte[] datas = new byte[newLength];
            System.arraycopy(bytes, from, datas, 0, Math.min(bytes.length - from, newLength));
            return datas;
        }

        return Arrays.copyOfRange(bytes, from, to);
    }

    public static int byteArrayToInt(byte[] b, int offset) {
        return b[offset++] & 0xFF |
                (b[offset++] & 0xFF) << 8 |
                (b[offset++] & 0xFF) << 16 |
                (b[offset++] & 0xFF) << 24;
    }

    //低字节在前
    public static int byteArrayToIntLH(byte[] b) {
        int res = 0;
        for (int i = 0; i < b.length; i++) {
            res += (b[i] & 0xff) << (i * 8);
        }
        return res;
    }

    //低字节在前
    public static long byteArrayToLongLH(byte[] b) {
        long res = 0;
        for (int i = 0; i < b.length; i++) {
            res += (b[i] & 0xff) << (i * 8);
        }
        return res;
    }

    public static int byteArrayToShort(byte[] b, int offset) {
        return b[offset++] & 0xFF |
                (b[offset++] & 0xFF) << 8;
    }

    /**
     * 获取一个byte的高四位和低四位
     * 当前用于A3951系列customBtnModel在TWS/非TWS下拼接的数组返回
     *
     * @param data byte
     * @return int
     */
    public static int getHighFour(byte data) {
        return ((data & 0xf0) >> 4);
    }

    public static int getLowFour(byte data) {
        return (data & 0x0f);
    }

    /**
     * 将两个int型数组拼接为byte     * @param highFourInt
     *
     * @param highFourInt 高四位，lowFourInt 低四位
     * @return byte
     */
    public static byte spliceByteWithInt(int highFourInt, int lowFourInt) {
        byte highFourByte = intToByte(highFourInt);
        byte lowFourByte = intToByte(lowFourInt);
        return intToByte(highFourByte << 4 | (lowFourByte & 0x0F));
    }

    //System.arraycopy()方法
    public static byte[] byteMerger(byte[] bt1, byte[] bt2) {
        byte[] bt3 = new byte[bt1.length + bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    public static String toHexStringForLog(byte[] data) {
        StringBuilder sb = new StringBuilder();
        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                String tempHexStr = Integer.toHexString(data[i] & 0xff) + " ";
                tempHexStr = tempHexStr.length() == 2 ? "0" + tempHexStr : tempHexStr;
                sb.append(tempHexStr);
            }
        }
        return sb.toString();
    }

    public static String getStringAddSpace(String replace) {
        String regex = "(.{2})";
        replace = replace.replaceAll(regex, "$1 ");
        return replace;
    }

    /**
     * byte数组转换为二进制字符串,每个字节以","隔开
     **/
    public static String byteArrToBinStr(byte[] b) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            result.append(Long.toString(b[i] & 0xff, 2) + ",");
        }
        return result.toString().substring(0, result.length() - 1);
    }
}
