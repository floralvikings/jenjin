package com.jenjinstudios.server.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to get salted, hashed strings.
 *
 * @author Caleb Brinkman
 */
public final class SHA256Hasher
{
	private static final int HEX_CONVERSION_CONSTANT = 0xff;
	private static final int SHA256_STRING_LENGTH = 64;
	private static final Logger LOGGER = Logger.getLogger(SHA256Hasher.class.getName());

	private SHA256Hasher() {}

	private static String getSHA256String(String input) {
		return getFullHexString(getSHA256Hash(input));
	}

	private static byte[] getSHA256Hash(String input) {
		byte[] passBytes = input.getBytes();
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			passBytes = md.digest(passBytes);
		} catch (NoSuchAlgorithmException e)
		{
			LOGGER.log(Level.SEVERE, "No SHA-256 algorithm found; are you using a valid Java implementation?");
		}
		return passBytes;
	}

	private static String getFullHexString(byte... bytes) {
		StringBuilder hexString = new StringBuilder(SHA256_STRING_LENGTH);
		for (byte anEncryption : bytes)
		{ // Convert back to a string, making sure to include leading zeros.
			String hex = Integer.toHexString(HEX_CONVERSION_CONSTANT & anEncryption);
			if (hex.length() == 1)
			{
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

	/**
	 * Get a SHA-256 hash of the {@code String} created by combining {@code salt} and {@code hash}.
	 *
	 * @param input The string to be salted and hashed.
	 * @param salt The salt to prepend to the string before hashing.
	 *
	 * @return The hashed, salted string.
	 */
	public static String getSaltedSHA256String(String input, String salt) { return getSHA256String(salt + input); }
}
