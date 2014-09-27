package com.jenjinstudios.world.io;

import com.google.gson.Gson;
import com.jenjinstudios.world.World;

import java.io.*;
import java.security.NoSuchAlgorithmException;

/**
 * This class handles the reading of and construction from world xml files.
 * @author Caleb Brinkman
 */
public class WorldDocumentReader
{
	private final InputStream inputStream;
	private byte[] worldFileChecksum;
	private byte[] worldFileBytes;

	public WorldDocumentReader(InputStream inputStream) { this.inputStream = inputStream; }

	public World read() throws WorldDocumentException {
		ByteArrayOutputStream bao = toByteArrayOutputStream(inputStream);
		worldFileBytes = bao.toByteArray();
		worldFileChecksum = createDocumentChecksum();

		ByteArrayInputStream bis = new ByteArrayInputStream(worldFileBytes);

		return new Gson().fromJson(new InputStreamReader(bis), World.class);
	}

	private ByteArrayOutputStream toByteArrayOutputStream(InputStream inputStream) throws WorldDocumentException {
		try
		{
			int bytesRead;
			byte[] buff = new byte[8000];
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			while ((bytesRead = inputStream.read(buff)) != -1)
			{
				bao.write(buff, 0, bytesRead);
			}
			return bao;
		} catch (IOException e)
		{
			throw new WorldDocumentException("Unable to read input stream.", e);
		}
	}

	private byte[] createDocumentChecksum() throws WorldDocumentException {
		try
		{
			return ChecksumUtil.getMD5Checksum(worldFileBytes);
		} catch (NoSuchAlgorithmException e)
		{
			throw new WorldDocumentException("Unable to create world file checksum.", e);
		}
	}

	public byte[] getWorldFileChecksum() { return worldFileChecksum; }

	public byte[] getWorldFileBytes() { return worldFileBytes; }
}
