package com.jenjinstudios.downloadserver;

import com.jenjinstudios.clientutil.file.FileUtil;
import com.jenjinstudios.jgsf.Server;

import java.io.File;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of a download server used to send a group of files.
 *
 * @author Caleb Brinkman
 */
public class DownloadServer extends Server
{
	/** The Logger for this class. */
	protected static final Logger LOGGER = Logger.getLogger(DownloadServer.class.getName());
	/** The updates-per-second of this server. */
	public static final int UPS = 50;
	/** The port on which this server listens. */
	public static final int PORT = 51018;
	/** The directory in which to look for files to send to the client. */
	private final String rootDirectory;
	/** The array of file hashes from the root directory. */
	private final String[] fileHashArray;
	/** The array of filenames, with the root directory stripped from the path. */
	private final String[] strippedArray;

	/**
	 * Construct a {@code DownloadServer} with the given root directory.
	 *
	 * @param clientFileDirectory The directory in which the client-needed files are stored, relative to the working
	 *                            directory.
	 */
	public DownloadServer(String clientFileDirectory)
	{
		super(UPS);
		rootDirectory = clientFileDirectory;
		File rootFile = new File(rootDirectory);
		String path = rootFile.getAbsolutePath();
		TreeSet<File> fileList = FileUtil.getContentsRecursive(rootFile);
		if (fileList.isEmpty())
			LOGGER.log(Level.WARNING, "No files found in specified directory... Maybe you forgot the slash? {0}", path);
		File[] fileListArray = fileList.toArray(new File[fileList.size()]);
		strippedArray = FileUtil.stripDirectory(rootDirectory, fileListArray);
		fileHashArray = new String[fileListArray.length];
		for (int i = 0; i < fileListArray.length; i++)
			fileHashArray[i] = FileUtil.getMD5Checksum(fileListArray[i]);
		addListener(PORT);
	}

	@Override
	public void addListener(int port)
	{
		synchronized (clientListeners)
		{
			try
			{
				clientListeners.add(new DownloadListener(this, port));
			} catch (Exception ex)
			{
				LOGGER.log(Level.SEVERE, "Error adding client", ex);
			}
		}
	}

	/**
	 * Get the array of MD5 checksums for the files in the client file directory.  The indices of these checksums
	 * match those of the file list.
	 *
	 * @return The array of MD5 checksums
	 */
	public String[] getFileHashArray()
	{
		return fileHashArray;
	}

	/**
	 * Get the list of files with the JAR path stripped from the filename.
	 *
	 * @return The list of files with the JAR path stripped from the file names.
	 */
	public String[] getFileListArray()
	{
		return strippedArray;
	}

	/**
	 * Get the root directory in which client-needed files are stored.
	 *
	 * @return The root directory in which client-needed files are stored.
	 */
	public String getRootDirectory()
	{
		return rootDirectory;
	}
}
