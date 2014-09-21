package com.jenjinstudios.core.io;

import com.jenjinstudios.core.util.Files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Caleb Brinkman
 */
public class MessageFileFinder
{
	/** The file name of message registry classed. */
	private static final String messageFileName = "Messages.xml";
	private static final Logger LOGGER = Logger.getLogger(MessageFileFinder.class.getName());

	/**
	 * Find the Messages.xml ZipEntry objects in the classpath.
	 * @return The list of found entries.
	 */
	private static LinkedList<String> findJarMessageEntries() {
		LinkedList<String> jarMessageEntries = new LinkedList<>();
		String classPath = System.getProperty("java.class.path");
		String[] pathElements = classPath.split(System.getProperty("path.separator"));
		for (String fileName : pathElements)
		{
			if (isCoreJar(fileName))
			{
				continue;
			}
			seachJarFile(jarMessageEntries, fileName);
		}
		return jarMessageEntries;
	}

	private static boolean isCoreJar(String fileName) {
		String javaHome = System.getProperty("java.home");
		return fileName.contains(javaHome);
	}

	private static void seachJarFile(LinkedList<String> jarMessageEntries, String fileName) {
		File file = new File(fileName);
		if (!file.isDirectory() && file.exists())
		{
			try (FileInputStream inputStream = new FileInputStream(file);
				 ZipInputStream zip = new ZipInputStream(inputStream))
			{
				searchZipEntries(jarMessageEntries, zip);
				inputStream.close();
				zip.close();
			} catch (IOException ex)
			{
				LOGGER.log(Level.WARNING, "Unable to read JAR entry " + fileName, ex);
			}
		}
	}

	private static void searchZipEntries(LinkedList<String> jarMessageEntries, ZipInputStream zip) throws IOException {
		ZipEntry ze;
		while ((ze = zip.getNextEntry()) != null)
		{
			String entryName = ze.getName();
			if (entryName.endsWith("Messages.xml")) { jarMessageEntries.add(entryName); }
		}
	}

	/**
	 * Look for files that match the message registry format.
	 * @return An ArrayList of message registry files.
	 */
	private static ArrayList<File> findMessageFiles() {
		String rootDir = Paths.get("").toAbsolutePath().toString() + File.separator;
		File rootFile = new File(rootDir);
		return Files.search(rootFile, messageFileName);
	}

	/**
	 * Add the Messages.xml entries in the working directory and add their InputStream to the given list.
	 */
	static LinkedList<InputStream> findMessageFileStreams() {
		LinkedList<InputStream> inputStreams = new LinkedList<>();
		ArrayList<File> messageFiles = findMessageFiles();
		for (File f : messageFiles)
		{
			LOGGER.log(Level.INFO, "Registering XML file {0}", f);
			try
			{
				inputStreams.add(new FileInputStream(f));
			} catch (IOException ex)
			{
				LOGGER.log(Level.WARNING, "Unable to create input stream for " + f, ex);
			}
		}
		return inputStreams;
	}

	/**
	 * Add the Messages.xml entries in the classpath and add their InputStream to the given list.
	 */
	static LinkedList<InputStream> findMessageJarStreams() {
		LinkedList<InputStream> inputStreams = new LinkedList<>();
		LinkedList<String> jarMessageEntries = findJarMessageEntries();
		for (String entry : jarMessageEntries)
		{
			LOGGER.log(Level.INFO, "Registering XML entry {0}", entry);
			inputStreams.add(MessageFileFinder.class.getClassLoader().getResourceAsStream(entry));
		}
		return inputStreams;
	}
}
