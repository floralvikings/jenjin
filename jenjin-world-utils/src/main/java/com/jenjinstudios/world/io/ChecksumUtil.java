package com.jenjinstudios.world.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Used to get file checksums.
 * @author Caleb Brinkman
 */
public class ChecksumUtil
{
	/**
	 * Get the md5 checksum of the given file.
	 * @param file The file.
	 * @return The md5 checksum.
	 * @throws java.io.IOException If there's an error reading the file.
	 * @throws java.security.NoSuchAlgorithmException If the MD5 digest algorithm is not found.
	 */
	public static byte[] getMD5Checksum(File file) throws IOException, NoSuchAlgorithmException {
		return getMD5Checksum(new FileInputStream(file));
	}

	/**
	 * Get the md5 checksum of the given input stream.
	 * @param stream The input stream.
	 * @return The md5 checksum.
	 * @throws java.io.IOException If there's an error reading the file.
	 * @throws java.security.NoSuchAlgorithmException If the MD5 digest algorithm is not found.
	 */
	public static byte[] getMD5Checksum(InputStream stream) throws IOException, NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		byte[] md5sum = new byte[]{Byte.MIN_VALUE};
		byte[] buffer = new byte[8192];
		int read;
		try
		{
			while ((read = stream.read(buffer)) > 0)
			{
				digest.update(buffer, 0, read);
			}
			md5sum = digest.digest();
		} finally
		{
			stream.close();
		}
		return md5sum;
	}
}
