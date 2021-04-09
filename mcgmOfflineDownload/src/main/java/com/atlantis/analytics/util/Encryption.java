package com.atlantis.analytics.util;

import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {

    public static final String AES = "AES";

    private static String byteArrayToHexString(byte[] b) {
	StringBuffer sb = new StringBuffer(b.length * 2);
	for (int i = 0; i < b.length; i++) {
	    int v = b[i] & 0xff;
	    if (v < 16) {
		sb.append('0');
	    }
	    sb.append(Integer.toHexString(v));
	}
	return sb.toString().toUpperCase();
    }

    private static byte[] hexStringToByteArray(String s) {
	byte[] b = new byte[s.length() / 2];
	for (int i = 0; i < b.length; i++) {
	    int index = i * 2;
	    int v = Integer.parseInt(s.substring(index, index + 2), 16);
	    b[i] = (byte) v;
	}
	return b;
    }

    /**
     * 
     * @param password
     * @return
     */
    public String getEncryptedPassword(String password) {
	String encryptedpwd = null;
	try {
	    KeyGenerator keyGen = KeyGenerator.getInstance(Encryption.AES);
	    keyGen.init(128);
	    String key = "C0C44E47B55D27367D0E0E3C7FAA38E0";
	    byte[] bytekey = hexStringToByteArray(key);
	    SecretKeySpec sks = new SecretKeySpec(bytekey, Encryption.AES);
	    Cipher cipher = Cipher.getInstance(Encryption.AES);
	    cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());
	    byte[] encrypted = cipher.doFinal(password.getBytes());
	    encryptedpwd = byteArrayToHexString(encrypted);
	    return encryptedpwd;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new RuntimeException(e.getMessage(), e);
	}
    }

    public static void main(String args[]) {
	Encryption encryption = new Encryption();
	System.out.println("Enter Password:");
	Scanner scanner = new Scanner(System.in);
	String password = scanner.next();
	String encPwd = encryption.getEncryptedPassword(password);
	System.out.println("Encrypted Data is:\n" + encPwd);
    }

}
