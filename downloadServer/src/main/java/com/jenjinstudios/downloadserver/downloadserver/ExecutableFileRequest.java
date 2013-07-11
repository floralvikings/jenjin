package com.jenjinstudios.downloadserver.downloadserver;

import com.jenjinstudios.clientutil.file.FileUtil;
import com.jenjinstudios.jgsf.ClientHandler;
import com.jenjinstudios.jgsf.ExecutableMessage;
import com.jenjinstudios.downloadclient.FileListMessage;
import com.jenjinstudios.downloadclient.FileMessage;
import com.jenjinstudios.downloadclient.FileRequest;

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
	private final ClientHandler clientHandler;
	/** The server which will execute this message. */
	private final DownloadServer server;
	/** The message sent by the client. */
	private final FileRequest message;
	/** The requested file. */
	private final String requestedFile;

	/**
	 * Cosntruct a new ExecutableFileRequest with the specified information.
	 *
	 * @param clientHandler The client handler that created this executable message.
	 * @param message       The FileRequest from the client.
	 */
	public ExecutableFileRequest(ClientHandler clientHandler, FileRequest message)
	{
		super(clientHandler, message);
		this.message = message;
		this.clientHandler = clientHandler;
		this.server = (DownloadServer) clientHandler.getServer();
		requestedFile = this.message.fileName;
	}

	/** Determine and queue the appropriate message(s) to send to the client. */
	@Override
	public void runSynced()
	{
		if (requestedFile.equals(""))
		{
			// If the file message is requesting a blank string, it means to send a file list.
			String[] fileArray = server.getFileListArray();
			String[] hashArray = server.getFileHashArray();
			FileListMessage listMessage = new FileListMessage(fileArray, hashArray);
			clientHandler.queueMessage(listMessage);
		} else
		{
			// We have to append the server's root directory to the beginning of the requested file name
			String fileName = server.getRootDirectory() + message.fileName;
			try
			{
				// This grabs the bytes from the file
				byte[] bytes = FileUtil.getFileBytes(new File(fileName));
				// Makes the message
				FileMessage fileMessage = new FileMessage(requestedFile, bytes);
				// And adds it to the client handler's "outgoing" queue.
				clientHandler.queueMessage(fileMessage);
			} catch (IOException e)
			{
				// Client requested invalid file.  Ignore.
			}
		}
	}


	@Override
	public void runASync()
	{

	}

}
