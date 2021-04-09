package com.atlantis.analytics.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

public class Decryption {

    private static final Logger LOGGER = Logger.getLogger(Decryption.class);

    public static final String AES = "AES";

//    private static String byteArrayToHexString(byte[] b) {
//        StringBuilder sb = new StringBuilder(b.length * 2);
//        for (int i = 0; i < b.length; i++) {
//            int v = b[i] & 0xff;
//            if (v < 16) {
//                sb.append('0');
//            }
//
//            sb.append(Integer.toHexString(v));
//        }
//
//        return sb.toString().toUpperCase();
//    }

    private static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }

        return b;
    }

    public String getPlainPwd(String pwd) {
        String originalPwd = null;
        try {
            LOGGER.debug("Trying to decrypt pwd: " + pwd);
            String tempkey = "C0C44E47B55D27367D0E0E3C7FAA38E0";
            byte[] bytekey = hexStringToByteArray(tempkey);
            SecretKeySpec sks = new SecretKeySpec(bytekey, Decryption.AES);
            Cipher cipher = Cipher.getInstance(Decryption.AES);
            cipher.init(Cipher.DECRYPT_MODE, sks);
            byte[] decrypted = cipher.doFinal(hexStringToByteArray(pwd));
            originalPwd = new String(decrypted);
        } catch (Exception e) {
            LOGGER.error("Password decryption error: " + e);
        }

        return originalPwd;
    }
}