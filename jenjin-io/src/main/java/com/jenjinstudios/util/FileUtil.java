package com.jenjinstudios.util;

import java.io.File;
import java.util.ArrayList;

/**
 * The {@code FileUtil} is used to get the MD5 hash checksum from a file.
 * @author Caleb Brinkman
 */
public class FileUtil
{
	/**
	 * Search a directory and subdirectories for files with the given name.
	 * @param dir The directory in which to start looking.
	 * @param fileName The name of the file(s) for which to look.
	 * @return An ArrayList of files in the given directory or a subdirectory, with the supplied file name.
	 */
	public static ArrayList<File> findFilesWithName(File dir, String fileName) {
		ArrayList<File> files = new ArrayList<>();
		File[] contents = dir.listFiles();
		if (contents != null)
			for (File f : contents)
			{
				if (f.isDirectory())
					files.addAll(findFilesWithName(f, fileName));
				if (f.getName().equals(fileName))
					files.add(f);
			}

		return files;
	}

	/**
	 * Delete the specified file, recursively if the file is a directory.
	 * @param file The file or directory to be deleted.
	 * @return Whether the file or directory was successfully deleted.
	 */
	public static boolean deleteRecursively(File file) {
		boolean deleted = false;
		if (file.isDirectory())
		{
			if (file.list().length == 0)
			{
				deleted = (file.delete());
			} else
			{
				String files[] = file.list();
				for (String temp : files)
				{
					File fileDelete = new File(file, temp);
					deleted = deleteRecursively(fileDelete);
				}
				if (file.list().length == 0)
				{
					deleted = file.delete();
				}
			}

		} else
		{
			deleted = file.delete();
		}
		return deleted;
	}
}