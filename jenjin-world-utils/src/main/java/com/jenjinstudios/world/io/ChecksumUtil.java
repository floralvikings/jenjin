package com.jenjinstudios.world.io;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Used to get file checksums.
 * @author Caleb Brinkman
 */
public class ChecksumUtil
{
	/**
	 * Get the MD5 Checksum for the given byte array.
	 * @param bytes The byte array.
	 * @return The MD5 checksum.
	 * @throws NoSuchAlgorithmException If the MD5 algorith cannot be found.
	 */
	public static byte[] getMD5Checksum(byte[] bytes) throws NoSuchAlgorithmException {
		return MessageDigest.getInstance("MD5").digest(bytes);
	}
}
