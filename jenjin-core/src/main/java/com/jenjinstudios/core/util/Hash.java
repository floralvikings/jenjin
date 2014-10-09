package com.jenjinstudios.core.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class used to hash data.
 *
 * @author Caleb Brinkman
 */
public class Hash
{
	// TODO Modify this method to use a Stream instead of a string.
	// TODO Allow this method to throw a custom exception, instead of returning null
	// TODO Rename to something that reflects that this function returns a SHA-256 hash.
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
