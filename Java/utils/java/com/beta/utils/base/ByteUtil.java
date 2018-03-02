package com.beta.utils.base;

/**
 * 记住：1B = 4b
 * 
 * @author sucre
 *
 */
public class ByteUtil {

    public static void main(String[] args) {

        String s1 = "1";
        String s2 = "20";

        System.out.println(or(s1, s2));
        System.out.println(and(s1, s2));

    }

    public static String or(String s1, String s2) {
        byte[] b1 = hex2byte(s1);
        byte[] b2 = hex2byte(s2);
        int length = b1.length;
        for (int i = 0; i < length; i++) {
            b1[i] = (byte) (b1[i] | b2[i]);
        }
        return byte2hex(b1);
    }

    public static String and(String s1, String s2) {
        byte[] b1 = hex2byte(s1);
        byte[] b2 = hex2byte(s2);
        int length = b1.length;
        for (int i = 0; i < length; i++) {
            b1[i] = (byte) (b1[i] & b2[i]);
        }
        return byte2hex(b1);
    }

    public static String byte2hex(byte[] b) { // 二进制转字符串
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
        }
        return hs;
    }

    public static byte[] hex2byte(String str) { // 字符串转二进制
        if (str == null)
            return null;
        str = str.trim();
        int len = str.length();
        if (len == 0 || len % 2 == 1)
            return null;

        byte[] b = new byte[len / 2];
        try {
            for (int i = 0; i < str.length(); i += 2) {
                b[i / 2] = (byte) Integer.decode("0x" + str.substring(i, i + 2)).intValue();
            }
            return b;
        } catch (Exception e) {
            return null;
        }
    }
}
