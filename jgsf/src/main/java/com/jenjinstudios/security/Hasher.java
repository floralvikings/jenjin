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
	public static final long BIT_MASK = 0xffffffffL;
	public static final long FNV_BASIS = 0x811c9dc5L;
	public static final long FNV_PRIME = (1 << 24) + 0x193;

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

	/**
	 * Get the FNV-1a_32 Hash of a given array of bytes.
	 *
	 * @param bytes The bytes which will be hashed.
	 * @return The hashed value.
	 */
	public static long getFNV1aHash(byte[] bytes)
	{
		long hash = FNV_BASIS;
		for (byte aByte : bytes)
		{
			hash ^= 0xFF & aByte;
			hash *= FNV_PRIME;
			hash &= BIT_MASK;
		}

		return (hash == 0) ? Integer.MAX_VALUE : hash;
	}

	/**
	 * Get a String representation of a FNV-1a_32 hash of a given String.
	 *
	 * @param input The string to hash.
	 * @return The String representation of the hash.
	 */
	public static String getFNV1aString(String input)
	{
		return Long.toHexString(getFNV1aHash(input.getBytes())).toUpperCase();
	}
}
