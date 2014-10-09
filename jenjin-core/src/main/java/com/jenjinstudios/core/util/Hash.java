package com.jenjinstudios.core.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash
{
	private static String getHashedString(String input) {
		String hashedString;
		try
		{
			//Convert the pass to an md5 hash string
			byte[] passBytes = input.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] encryption = md.digest(passBytes);
			StringBuilder hexString = new StringBuilder();
			for (byte anEncryption : encryption)
			{ // Convert back to a string, making sure to include leading zeros.
				String hex = Integer.toHexString(0xff & anEncryption);
				if (hex.length() == 1)
				{
					hexString.append('0');
				}
				hexString.append(hex);
			}
			hashedString = hexString.toString();
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException ex)
		{
			hashedString = null;
		}
		return hashedString;
	}

	public static String getHashedString(String input, String salt) {
		return getHashedString(salt + input);
	}
}
