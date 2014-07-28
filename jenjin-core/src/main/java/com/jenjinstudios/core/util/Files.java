package com.jenjinstudios.core.util;

import java.io.File;
import java.util.ArrayList;

/**
 * The {@code Files} is used to get the MD5 hash checksum from a file.
 * @author Caleb Brinkman
 */
public class Files
{
	/**
	 * Search a directory and subdirectories for files with the given name.
	 * @param dir The directory in which to start looking.
	 * @param fileName The name of the file(s) for which to look.
	 * @return An ArrayList of files in the given directory or a subdirectory, with the supplied file name.
	 */
	public static ArrayList<File> search(File dir, String fileName) {
		ArrayList<File> files = new ArrayList<>();
		File[] contents = dir.listFiles();
		if (contents != null)
			for (File f : contents)
			{
				if (f.isDirectory())
					files.addAll(search(f, fileName));
				if (f.getName().equals(fileName))
					files.add(f);
			}

		return files;
	}

}