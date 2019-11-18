package com.shaungyu.eyes;

import android.util.Log;

public class TransformUtil {

    private final String TAG = TransformUtil.this.getClass().getSimpleName();

    private TransformUtil() {
    }

    private static class SingleHoler {
        private final static TransformUtil instance = new TransformUtil();
    }

    public static TransformUtil getInstance() {
        return SingleHoler.instance;
    }

    public byte[] stringToHexStrToBytes(String data) {
        return hexStringToByte(stringToHexStr(data));
    }

    /**
     * 字符串转化成为16进制字符串
     */
    private String stringToHexStr(String s) {
        if (s == null || s.length() == 0)
            return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            sb.append(Integer.toHexString(ch));
        }
        Log.e(TAG, "String-->HexStr：" + (s + "-->" + sb.toString()));
        return sb.toString();
    }

    private byte[] hexStringToByte(String hexString) {
        if (hexString == null || hexString.length() == 0)
            return null;
        int len = (hexString.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hexString.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        Log.e(TAG, "hexStr-->byte[]：length=" + result.length);
        return result;
    }

    public byte[] hexStringToByteArray(String hexString) {
        hexString = hexString.toUpperCase();
//        hexString = hexString.replace(" ", "");
        int len = (hexString.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hexString.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    public String bytesToHexStringToString(byte[] bytes) {
        return hexStringToString(bytesToHexString(bytes));
    }

    private String bytesToHexString(byte[] src) {
        if (src == null || src.length <= 0) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        Log.e(TAG, "byte[]-->hexStr：length=" + src.length + "  content=" + stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * 16进制转换成为string类型字符串
     */
    private String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
//        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "UTF-8");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }


    public String byte2stringHex(byte[] buf) {
        StringBuffer strBuffer = new StringBuffer();
        String str = null;
        for (int i = 0; i < buf.length; i++) {
            strBuffer.append(String.format(("%02X"), buf[i]));
        }
        return strBuffer.toString();
    }

    /*将十六进制序列转换为字符串*/
    public String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    //字符串转换为16进制字符串
    public String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }
}
