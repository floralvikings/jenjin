package com.jenjinstudios.clientutil.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code FileUtil} is used to get the MD5 hash checksum from a file.
 *
 * @author Caleb Brinkman
 */
public class FileUtil
{
	/** The logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(FileUtil.class.getName());

	/**
	 * Recursively get the contents of a given directory.
	 *
	 * @param root The root directory to search.
	 * @return A TreeSet containing every File in the given directory and subdirectories.
	 */
	public static TreeSet<File> getContentsRecursive(File root)
	{
		TreeSet<File> r = new TreeSet<>();
		File[] contents = root.listFiles();

		if (contents == null) contents = new File[0];
		for (File current : contents)
		{
			if (current.isDirectory())
				r.addAll(getContentsRecursive(current));
			else if (!current.isHidden())
				r.add(current);
		}
		return r;
	}

	/**
	 * Strip the given directory name from the given array of Files.
	 *
	 * @param directory The directory name to strip.
	 * @param targets   The array of Files.
	 * @return A String array containing the stripped filenames.
	 */
	public static String[] stripDirectory(String directory, File[] targets)
	{
		String[] newFiles = new String[targets.length];
		for (int i = 0; i < targets.length; i++)
		{
			newFiles[i] = stripDirectory(directory, targets[i]);
		}
		return newFiles;
	}

	/**
	 * Strip the directory name from a File.
	 *
	 * @param directory The directory name to strip.
	 * @param target    The File to be stripped.
	 * @return The new file name.
	 */
	private static String stripDirectory(String directory, File target)
	{
		return target.getPath().replace(directory, "");
	}


	/**
	 * Create a byte array containing the MD5 checksum of the given file.
	 *
	 * @param f The file for which to retrieve a checksum.
	 * @return The file checksum in the form of a byte array.
	 * @throws IOException              If there is an error reading from the file.
	 * @throws NoSuchAlgorithmException If there is an error creating the checksum
	 */
	private static byte[] createMD5Checksum(File f) throws IOException, NoSuchAlgorithmException
	{
		InputStream fis = new FileInputStream(f);
		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;

		do
		{
			numRead = fis.read(buffer);
			if (numRead > 0)
				complete.update(buffer, 0, numRead);
		} while (numRead != -1);

		fis.close();
		return complete.digest();
	}

	/**
	 * Get a String representation of the given file's MD5 checksum.
	 *
	 * @param f The file for which to retrieve a checksum.
	 * @return A String representation of the MD5 checksum.
	 */
	public static String getMD5Checksum(File f)
	{
		byte[] b;
		try
		{
			b = createMD5Checksum(f);
		} catch (IOException | NoSuchAlgorithmException e)
		{
			LOGGER.log(Level.SEVERE, "Error creating MD5 checksum for file: " + f.getAbsolutePath(), e);
			return null;
		}
		String result = "";
		for (byte aB : b)
		{
			result += Integer.toString((aB & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	/**
	 * Get the content of the specified file as a array of bytes.
	 *
	 * @param f The file to be parsed into a byte array.
	 * @return An array of {@code byte}s containing the content of the file.
	 * @throws java.io.IOException If the number of bytes in the file is different from the bytes read into the array.
	 *                             Recommend another attempt, or closing link with client and destruction of client handler.
	 */
	public static byte[] getFileBytes(File f) throws IOException
	{
		byte[] bytes;
		int offset = 0;  // The beginning of the current piece of data being read from the file.
		int numRead; // The number of
		//Read the file into an array of bytes so that it can be sent easily over socket.
		if (!f.exists())
		{
			// This bit of nonsense is because UNIX systems include a '/' at the beginning of all filepaths.
			f = new File(File.separatorChar + f.getPath());
		}
		long fileLength = f.length();
		FileInputStream is = new FileInputStream(f);

		if (fileLength > Integer.MAX_VALUE)
		{
			// This is like... a 2GB file.
			throw new IOException("File is too large to be read into bytes.");
		}

		bytes = new byte[(int) fileLength];

		// Safely read the file into a byte array.  Doing it this way lets us check the size of the file after it's
		// read in to the byte array, giving some redimentary verification if necessary.
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
		{
			offset += numRead;
		}
		if (offset < bytes.length) throw new IOException("File was read incorrectly");
		return bytes;
	}

	/**
	 * Search a directory and subdirectories for files with the given name.
	 * @param dir The directory in which to start looking.
	 * @param fileName The name of the file(s) for which to look.
	 * @return An ArrayList of files in the given directory or a subdirectory, with the supplied file name.
	 */
	public static ArrayList<File> findFilesWithName(File dir, String fileName)
	{
		ArrayList<File> files = new ArrayList<>();
		File[] contents = dir.listFiles();
		if(contents != null)
			for(File f : contents)
			{
				if(f.isDirectory())
					files.addAll(findFilesWithName(f, fileName));
				if(f.getName().equals(fileName))
					files.add(f);
			}

		return files;
	}
}