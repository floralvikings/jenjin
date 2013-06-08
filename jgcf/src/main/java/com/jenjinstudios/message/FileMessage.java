package com.jenjinstudios.message;

import com.jenjinstudios.io.BaseMessage;

/**
 * Contains a file and filename to send to the client.
 *
 * @author Caleb Brinkman
 */
public class FileMessage extends BaseMessage
{
	/** The Message Registration id of this message type. */
	public static final short ID = 8;
	/** The Filename. */
	public final String FILENAME;
	/** The file, as an array of bytes. */
	public final byte[] BYTES;

	/**
	 * Construct a new FileMessage.
	 *
	 * @param filename  The name of the file.
	 * @param fileBytes The bytes of the file.
	 */
	public FileMessage(String filename, byte[] fileBytes)
	{
		super(filename, fileBytes);
		FILENAME = filename;
		BYTES = fileBytes;
	}

	@Override
	public short getID()
	{
		return ID;
	}
}
