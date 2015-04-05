package com.jenjinstudios.world.client;

import java.util.Arrays;

/**
 * @author Caleb Brinkman
 */
public class WorldFileTracker
{
	private boolean waitingForChecksum;
	private byte[] checksum;
	private boolean waitingForFile;
	private byte[] bytes;

	public byte[] getChecksum() { return Arrays.copyOf(checksum, checksum.length); }

	public void setChecksum(byte[] checksum) { this.checksum = checksum; }

	public byte[] getBytes() { return Arrays.copyOf(bytes, bytes.length); }

	public void setBytes(byte[] bytes) { this.bytes = bytes; }

	public boolean isWaitingForChecksum() { return waitingForChecksum; }

	public void setWaitingForChecksum(boolean waiting) { this.waitingForChecksum = waiting; }

	public boolean isWaitingForFile() { return waitingForFile; }

	public void setWaitingForFile(boolean waiting) { this.waitingForFile = waiting; }

}
