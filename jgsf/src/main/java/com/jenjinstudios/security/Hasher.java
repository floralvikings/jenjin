package com.jenjinstudios.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The {@code Hasher} class contains methods for getting encrypted strings.
 *
 * @author Caleb Brinkman
 */
public class Hasher
{
	/**
	 * Return a SHA1 hash of the given string.
	 *
	 * @param pw The string to hash.
	 * @return a SHA1 hash of the given string.
	 */
	public static String getHashedString(String pw)
	{
		try
		{
			//Convert the pass to an md5 hash string
			byte[] pwbytes = pw.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] encryption = md.digest(pwbytes);
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
			return hexString.toString();
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException ex)
		{
			return null;
		}
	}
}
