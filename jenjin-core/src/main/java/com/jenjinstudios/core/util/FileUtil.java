package com.jenjinstudios.core.util;

import java.io.File;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Utility class used for file searching.
 *
 * @author Caleb Brinkman
 */
public class FileUtil
{
	private FileUtil() { }

	/**
	 * Recursively search the given directory for files that exactly matches the file with the given name.
	 *
	 * @param dir The root directory in which to begin the search.
	 * @param fileName The name of the file for which to search.  Note that the filenames must match <i>exactly.</i>
	 *
	 * @return A list of all discovered files that match {@code fileName}.
	 */
    public static Collection<File> search(File dir, String fileName) {
        AbstractList<File> files = new ArrayList<>(10);
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