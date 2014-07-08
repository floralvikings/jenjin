package com.jenjinstudios.world.client;

import com.jenjinstudios.core.io.Message;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.io.WorldDocumentException;
import com.jenjinstudios.world.io.WorldDocumentReader;

import java.io.*;
import java.util.Arrays;

/**
 * @author Caleb Brinkman
 */
public final class ServerWorldFileTracker
{
	private final File worldFile;
	private boolean waitingForChecksum;
	private byte[] checksum;
	private boolean waitingForFile;
	private byte[] bytes;
	private WorldDocumentReader worldDocumentReader;

	public ServerWorldFileTracker(File worldFile) {
		this.worldFile = worldFile;
	}

	public void getServerWorldFileChecksum(WorldClient worldClient) throws InterruptedException {
		Message worldFileChecksumRequest = worldClient.getMessageFactory().generateWorldChecksumRequest();
		worldClient.queueOutgoingMessage(worldFileChecksumRequest);
		waitForWorldFileChecksum();
	}

	public void readServerWorldFile(WorldClient worldClient) throws InterruptedException, WorldDocumentException {
		if (needsWorldFile())
		{
			worldClient.queueOutgoingMessage(worldClient.getMessageFactory().generateWorldFileRequest());
			waitForWorldFile();
		}
	}

	public void writeReceivedWorldToFile() throws WorldDocumentException {
		createNewFileIfNecessary();
		writeServerWorldToFile();
	}

	protected byte[] getChecksum() { return checksum; }

	public void setChecksum(byte[] checksum) { this.checksum = checksum; }

	protected byte[] getBytes() { return bytes; }

	public void setBytes(byte[] bytes) { this.bytes = bytes; }

	protected boolean isWaitingForChecksum() { return waitingForChecksum; }

	public void setWaitingForChecksum(boolean bool) { this.waitingForChecksum = bool; }

	protected boolean isWaitingForFile() { return waitingForFile; }

	public void setWaitingForFile(boolean waiting) { this.waitingForFile = waiting; }

	protected World readWorldFromServer() throws WorldDocumentException {
		World world = null;
		if (bytes != null)
		{
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
			worldDocumentReader = new WorldDocumentReader(byteArrayInputStream);
			world = worldDocumentReader.read();
		}
		return world;
	}

	protected World readWorldFromFile() throws WorldDocumentException {
		World world = null;
		if (worldFile.exists())
		{
			try
			{
				FileInputStream inputStream = new FileInputStream(worldFile);
				worldDocumentReader = new WorldDocumentReader(inputStream);
				world = worldDocumentReader.read();
				bytes = worldDocumentReader.getWorldFileBytes();
			} catch (FileNotFoundException e)
			{
				throw new WorldDocumentException("Couldn't find world file.", e);
			}
		}
		return world;
	}

	private boolean needsWorldFile() {
		boolean checksumsMatch = false;
		boolean readerNull = worldDocumentReader == null;
		if (!readerNull)
		{
			checksumsMatch = Arrays.equals(getChecksum(),
				  worldDocumentReader.getWorldFileChecksum());
		}
		return !checksumsMatch;
	}

	private void createNewFileIfNecessary() throws WorldDocumentException {
		if (!tryCreateWorldFileDirectory() || !tryCreateWorldFile())
		{
			throw new WorldDocumentException("Unable to create new world file!");
		}
	}

	private boolean tryCreateWorldFile() throws WorldDocumentException {
		try
		{
			return worldFile.exists() || worldFile.createNewFile();
		} catch (IOException e)
		{
			throw new WorldDocumentException("Unable to create new file.", e);
		}
	}

	private boolean tryCreateWorldFileDirectory() {
		return worldFile.getParentFile().exists() || worldFile.getParentFile().mkdirs();
	}

	private void waitForWorldFile() throws InterruptedException {
		setWaitingForFile(true);
		while (isWaitingForFile())
		{
			Thread.sleep(10);
		}
	}

	private void waitForWorldFileChecksum() throws InterruptedException {
		setWaitingForChecksum(true);
		while (isWaitingForChecksum())
		{
			Thread.sleep(10);
		}
	}

	private void writeServerWorldToFile() throws WorldDocumentException {
		try (FileOutputStream worldOut = new FileOutputStream(worldFile))
		{
			worldOut.write(bytes);
			worldOut.close();
		} catch (IOException ex)
		{
			throw new WorldDocumentException("Unable to write world file.", ex);
		} catch (NullPointerException ex)
		{
			throw new WorldDocumentException("Can't write world to file until world data has been received.");
		}
	}
}
