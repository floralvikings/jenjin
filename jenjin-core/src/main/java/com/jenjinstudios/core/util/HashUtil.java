package com.jenjinstudios.core.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class used to hash data.
 *
 * @author Caleb Brinkman
 */
public class HashUtil
{
	// TODO Modify this method to be more flexible.
	// TODO Rename to something that reflects that this function returns a SHA-256 hash.
	private static String getHashedString(String input) {
		try
		{
			String hashedString;
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
			return hashedString;
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException ex)
		{
			throw new RuntimeException("Unable to find SHA-256 Algorithm");
		}
	}

	/**
	 * Get a SHA-256 hash of the {@code String} created by combining {@code salt} and {@code hash}.
	 *
	 * @param input The string to be salted and hashed.
	 * @param salt The salt to prepend to the string before hashing.
	 *
	 * @return The hashed, salted string.
	 */
	// TODO Rename to reflect that this function uses SHA-256
	public static String getHashedString(String input, String salt) {
		return getHashedString(salt + input);
	}
}
