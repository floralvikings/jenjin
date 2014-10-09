package com.jenjinstudios.core.util;

import java.io.File;
import java.util.ArrayList;

/**
 * Utility class used for file searching.
 *
 * @author Caleb Brinkman
 */
public class Files
{
	private Files() { }

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