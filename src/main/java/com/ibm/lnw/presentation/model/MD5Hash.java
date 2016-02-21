package com.ibm.lnw.presentation.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Jan Valentik on 11/20/2015.
 */
public class MD5Hash {
	public static String encrypt(String passwordToHash) {
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			return passwordToHash;
		}
		messageDigest.update(passwordToHash.getBytes());
		byte[] bytes = messageDigest.digest();
		StringBuilder stringBuilder = new StringBuilder();
		for (byte singleByte : bytes) {
			stringBuilder.append(Integer.toString((singleByte & 0xff) + 0x100, 16).substring(1));
		}
		return stringBuilder.toString();
	}
}
