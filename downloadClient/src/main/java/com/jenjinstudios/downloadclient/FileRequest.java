package com.jenjinstudios.downloadclient;

import com.jenjinstudios.io.BaseMessage;

/**
 * Requests a file from the server.
 *
 * @author Caleb Brinkman
 */
public class FileRequest extends BaseMessage
{
	/** The message registration id used by this message type. */
	public static final short ID = 100;
	/** The name of the file being requested. */
	public final String fileName;

	/**
	 * Construct a new FileRequest.
	 *
	 * @param fileName The name of the file to request.
	 */
	public FileRequest(String fileName)
	{
		super(ID, fileName);
		this.fileName = fileName;
	}
}
