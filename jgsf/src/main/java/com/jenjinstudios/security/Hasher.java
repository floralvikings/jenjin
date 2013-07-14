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
	 * @param input The string to hash.
	 * @return a SHA1 hash of the given string.
	 */
	private static String getHashedString(String input)
	{
		try
		{
			//Convert the pass to an md5 hash string
			byte[] pwbytes = input.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("SHA-256");
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

	/**
	 * Get a salted, SHA256 hashed string for the supplied string and salt.
	 * @param input The string to be hashed.
	 * @param salt The salt to be applied before hashing.
	 * @return The hashed and salted string.
	 */
	public static String getHashedString(String input, String salt)
	{
		return getHashedString(salt + input);
	}
}
