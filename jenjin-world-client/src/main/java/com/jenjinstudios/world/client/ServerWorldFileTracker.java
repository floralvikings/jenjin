package com.jenjinstudios.world.client;

/**
 * @author Caleb Brinkman
 */
public class ServerWorldFileTracker
{
	private boolean waitingForChecksum;
	private byte[] checksum;
	private boolean waitingForFile;
	private byte[] bytes;

	public byte[] getChecksum() { return checksum; }

	public void setChecksum(byte[] checksum) { this.checksum = checksum; }

	public byte[] getBytes() { return bytes; }

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public boolean isWaitingForChecksum() { return waitingForChecksum; }

	public void setWaitingForChecksum(boolean bool) {
		this.waitingForChecksum = bool;
	}

	public boolean isWaitingForFile() { return waitingForFile; }

	public void setWaitingForFile(boolean waiting) {
		this.waitingForFile = waiting;
	}
}
