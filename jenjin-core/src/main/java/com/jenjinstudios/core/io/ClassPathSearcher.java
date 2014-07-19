package com.jenjinstudios.core.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Caleb Brinkman
 */
public class ClassPathSearcher
{
	public static Collection<String> getResources(final Pattern pattern) {
		final ArrayList<String> foundResources = new ArrayList<>();
		final String classPath = System.getProperty("java.class.path", ".");
		final String[] classPathElements = classPath.split(":");
		for (final String element : classPathElements)
		{
			foundResources.addAll(getResources(element, pattern));
		}
		return foundResources;
	}

	private static Collection<String> getResources(final String element, final Pattern pattern) {
		final ArrayList<String> foundResources = new ArrayList<>();
		final File file = new File(element);
		if (file.isDirectory())
		{
			foundResources.addAll(getResourcesFromDirectory(file, pattern));
		} else
		{
			foundResources.addAll(getResourcesFromJarFile(file, pattern));
		}
		return foundResources;
	}

	private static Collection<String> getResourcesFromJarFile(final File file, final Pattern pattern) {
		final ArrayList<String> foundResources = new ArrayList<>();
		ZipFile zf = tryGetZipFile(file);
		searchZipFile(pattern, foundResources, zf);
		tryCloseZipFile(zf);
		return foundResources;
	}

	private static void tryCloseZipFile(ZipFile zf) {
		try
		{
			zf.close();
		} catch (IOException e)
		{
			throw new Error(e);
		}
	}

	private static void searchZipFile(Pattern pattern, ArrayList<String> foundResources, ZipFile zf) {
		final Enumeration entries = zf.entries();
		while (entries.hasMoreElements())
		{
			final ZipEntry ze = (ZipEntry) entries.nextElement();
			final String fileName = ze.getName();
			final boolean accept = pattern.matcher(fileName).matches();
			if (accept)
			{
				foundResources.add(fileName);
			}
		}
	}

	private static ZipFile tryGetZipFile(File file) {
		ZipFile zf;
		try
		{
			zf = new ZipFile(file);
		} catch (IOException e)
		{
			throw new Error(e);
		}
		return zf;
	}

	private static Collection<String> getResourcesFromDirectory(final File directory, final Pattern pattern) {
		final ArrayList<String> retval = new ArrayList<>();
		final File[] fileList = directory.listFiles();
		for (final File file : fileList != null ? fileList : new File[0])
		{
			searchRecursively(pattern, retval, file);
		}
		return retval;
	}

	private static void searchRecursively(Pattern pattern, ArrayList<String> retval, File file) {
		if (file.isDirectory())
		{
			retval.addAll(getResourcesFromDirectory(file, pattern));
		} else
		{
			searchDirectory(pattern, retval, file);
		}
	}

	private static void searchDirectory(Pattern pattern, ArrayList<String> retval, File directory) {
		try
		{
			final String fileName = directory.getCanonicalPath();
			final boolean accept = pattern.matcher(fileName).matches();
			if (accept)
			{
				retval.add(fileName);
			}
		} catch (final IOException e)
		{
			throw new Error(e);
		}
	}
}
