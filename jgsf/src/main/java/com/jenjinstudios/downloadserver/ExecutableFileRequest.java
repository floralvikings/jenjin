package com.jenjinstudios.downloadserver;

import com.jenjinstudios.clientutil.file.FileUtil;
import com.jenjinstudios.jgsf.ExecutableMessage;
import com.jenjinstudios.message.FileListMessage;
import com.jenjinstudios.message.FileMessage;
import com.jenjinstudios.message.FileRequest;

import java.io.File;
import java.io.IOException;

/**
 * Used to handle a File request from the client.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public class ExecutableFileRequest extends ExecutableMessage
{
	/** The client handler which created this executable message. */
	private final DownloadClientHandler clientHandler;
	/** The server which will execute this message. */
	private final DownloadServer server;
	/** The message sent by the client. */
	private final FileRequest message;
	/** The requested file. */
	private final String requestedFile;
	/** Flags whether this request is for a specific file or a list. */
	private final boolean listRequest;

	/**
	 * Cosntruct a new ExecutableFileRequest with the specified information.
	 *
	 * @param clientHandler The client handler that created this executable message.
	 * @param message       The FileRequest from the client.
	 */
	public ExecutableFileRequest(DownloadClientHandler clientHandler, FileRequest message)
	{
		super(clientHandler, message);
		this.message = message;
		this.clientHandler = clientHandler;
		this.server = (DownloadServer) clientHandler.getServer();
		requestedFile = this.message.fileName;
		listRequest = requestedFile.equals("");
	}

	@Override
	public void runSynced()
	{

	}

	/** Determine and queue the appropriate message to send to the client. */
	@Override
	public void runASync()
	{
		if (listRequest)
		{
			clientHandler.queueMessage(new FileListMessage(server.getFileListArray(), server.getFileHashArray()));
		} else      // if requesting specific file
		{
			String fileName = server.getRootDirectory() + message.fileName;
			try
			{
				byte[] bytes = FileUtil.getFileBytes(new File(fileName));
				FileMessage fileMessage = new FileMessage(requestedFile, bytes);
				clientHandler.queueMessage(fileMessage);
			} catch (IOException e)
			{
				// Client requested invalid file.  Ignore.
			}
		}
	}

}
