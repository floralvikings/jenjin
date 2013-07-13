package com.jenjinstudios.downloadclient;

import com.jenjinstudios.clientutil.file.FileUtil;
import com.jenjinstudios.jgcf.Client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/** @author Caleb Brinkman */
public class DownloadClient extends Client
{
	/** The logger associated with this class. */
	private static final Logger LOGGER = Logger.getLogger(DownloadClient.class.getName());
	/** Counts the number of files downloaded. */
	private int numReceivedFiles;
	/** Flags whether the client has received files. */
	private boolean receivedAllFiles;
	/** Flags whether the file list has been received. */
	private volatile boolean hasReceivedFileList;
	/** The FileListMessage sent by the server, if any. */
	private ArrayList<String> fileList;

	public DownloadClient(String address, int port)
	{
		super(address, port);
	}

	/**
	 * Create the file sent by the file message.  This implementation assumes that if you have received this message,
	 * the file should be downloaded.  Therefore, it overwrites any files already in place.  This behavior is intended,
	 * as only needed files should actually be requested and therefore sent.
	 *
	 * @param message The {@code FileMessage} sent from the server.
	 * @throws java.io.IOException if there is an error downloading or writing the file.
	 */
	protected void processFileMessage(FileMessage message) throws IOException
	{
		// Get the file name from the message
		String fileName = message.FILENAME;
		File newFile = new File(fileName);
		// Create directories, if needed
		File parent = newFile.getParentFile();
		if (parent != null) //noinspection ResultOfMethodCallIgnored
			parent.mkdirs();
		// Delete the file if it already exists
		if (newFile.exists())
			if (!newFile.delete())
				throw new IOException("Unable to delete file: " + fileName);
		// Try and create the new file
		if (!newFile.createNewFile())
			throw new IOException("Error creating new file: " + fileName);
		// Write the bytes from the message into the file.
		FileOutputStream outputStream = new FileOutputStream(newFile);
		outputStream.write(message.BYTES);
		outputStream.close();
		// increment the number of received files.
		numReceivedFiles++;
		if (numReceivedFiles == fileList.size() - 1)
			receivedAllFiles = true;
	}

	/**
	 * Process a {@code FileListMessage} as sent from the server.  The file list is assumed to be an array of
	 * {@code File} objects, to be downloaded directly into the runtime directory.  If the file list has already
	 * been requested and received, this does nothing.
	 *
	 * @param message The message from the server.
	 */
	protected void processFileListMessage(FileListMessage message)
	{
		// We only want to process the file list once.
		if (fileList != null)
			return;
		// Initialize the file list.
		fileList = new ArrayList<>();
		String[] files = message.FILE_LIST;
		String[] hashes = message.MD5_HASH_LIST;
		// Iterate through the files and check if the hash for the client side file is correct.
		for (int i = 0; i < files.length; i++)
		{
			String currentFileName = files[i];
			File currentFile = new File(currentFileName);
			String currentHash = hashes[i];
			// Check to see if the file doesn't exist, and if it does, make sure the checksum is correct
			if (!currentFile.exists() || !FileUtil.getMD5Checksum(currentFile).equals(currentHash))
				fileList.add(currentFile.getPath());
		}
	}

	@Override
	public void processMessage(Object message) throws IOException
	{
		if (message instanceof FileMessage)
		{
			processFileMessage((FileMessage) message);
		} else if (message instanceof FileListMessage)
		{
			processFileListMessage((FileListMessage) message);
			hasReceivedFileList = true;
		} else
			super.processMessage(message);
	}

	/**
	 * Get whether the client has received files.
	 *
	 * @return true if the client has received all needed files from the server.
	 */
	public boolean hasReceivedAllFiles()
	{
		return receivedAllFiles;
	}

	/**
	 * Get the File List sent by the server, if any.
	 *
	 * @return The list of files sent by the server that this client will need to download.
	 */
	public ArrayList<String> getFileList()
	{
		return fileList;
	}

	/**
	 * Request the list of files that can be downloaded.  It is important to note that the message sent does nothing on
	 * the server by default.  The com.jenjinstudios.downloadserver.downloadserver module contains an implementation of Server that
	 * allows for this functionality.  This method blocks until the file list has been received.
	 */
	public final void requestFileList()
	{
		queueMessage(new FileRequest(""));
		while (!hasReceivedFileList)
			try
			{
				Thread.sleep(1);
			} catch (InterruptedException e)
			{
				LOGGER.log(Level.INFO, "Interrupted", e);
			}
	}

	/** Request all files needed that have been pulled from the FileListMessage sent from the server. */
	public final void requestNeededFiles()
	{
		if (fileList == null)
			return;
		for (String fileName : fileList)
			queueMessage(new FileRequest(fileName));
		while (!receivedAllFiles)
			try
			{
				Thread.sleep(1);
			} catch (InterruptedException e)
			{
				LOGGER.log(Level.INFO, "Interrupted", e);
			}
	}
}
