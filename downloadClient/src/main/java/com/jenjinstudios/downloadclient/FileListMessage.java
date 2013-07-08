package com.jenjinstudios.downloadclient;

import com.jenjinstudios.io.BaseMessage;

/**
 * Contains a list of files and file hashes for the client to process.
 *
 * @author Caleb Brinkman
 */
public class FileListMessage extends BaseMessage
{
	/** The Message Registration ID of this message type. */
	public static final short ID = 7;
	/** The list of files. */
	public final String[] FILE_LIST;
	/** The list of file hashes. */
	public final String[] MD5_HASH_LIST;

	/**
	 * Cosntruct a new FileListMessage.
	 *
	 * @param fileList The list of files.
	 * @param hashes   The list of hashes.
	 */
	public FileListMessage(String[] fileList, String[] hashes)
	{
		super(fileList, hashes);
		this.FILE_LIST = fileList;
		this.MD5_HASH_LIST = hashes;
	}

	@Override
	public short getID()
	{
		return ID;
	}
}
