package com.jenjinstudios.core.io;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * The MessageFileFinder class is used to discover Messages.xml files in the classpath and working directory.
 * <p>
 * This class is not mean to be referenced directly by your code.
 *
 * @author Caleb Brinkman
 */
public final class MessageFileFinder
{
	private static final String MESSAGE_FILE_NAME = "Messages.xml";
	private static final Logger LOGGER = Logger.getLogger(MessageFileFinder.class.getName());
	private final String rootDir;

	/**
	 * Construct a new MessageFileFinder, which works recursively from the current working directory and classpath to
	 * find Message files.
	 */
	public MessageFileFinder() {
		this.rootDir = Paths.get("").toAbsolutePath() + File.separator;
	}

	private static Iterable<String> findJarMessageEntries() {
		Collection<String> jarMessageEntries = new LinkedList<>();
		String classPath = System.getProperty("java.class.path");
		String[] pathElements = classPath.split(System.getProperty("path.separator"));
		for (String fileName : pathElements)
		{
			if (!isCoreJar(fileName))
			{
				seachJarFile(jarMessageEntries, fileName);
			}

		}
		return jarMessageEntries;
	}

	private static boolean isCoreJar(String fileName) {
		String javaHome = System.getProperty("java.home");
		return fileName.contains(javaHome);
	}

	private static void seachJarFile(Collection<String> jarMessageEntries, String fileName) {
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

	private static void searchZipEntries(Collection<String> jarMessageEntries, ZipInputStream zip) throws IOException {
		ZipEntry ze;
		while ((ze = zip.getNextEntry()) != null)
		{
			String entryName = ze.getName();
			if (entryName.endsWith("Messages.xml")) { jarMessageEntries.add(entryName); }
		}
	}

	/**
	 * Recursively search the given directory for files that exactly matches the file with the given name.
	 *
	 * @param dir The root directory in which to begin the search.
	 * @param fileName The name of the file for which to search.  Note that the filenames must match <i>exactly.</i>
	 *
	 * @return A list of all discovered files that match {@code fileName}.
	 */
	private static Collection<File> search(File dir, String fileName) {
		List<File> files = new ArrayList<>(10);
		File[] contents = dir.listFiles();
		if (contents != null)
			for (File file : contents)
			{
				if (file.isDirectory())
					files.addAll(search(file, fileName));
				if (file.getName().equals(fileName))
					files.add(file);
			}

		return files;
	}

	private Iterable<File> findMessageFiles() {
		File rootFile = new File(rootDir);
		return search(rootFile, MESSAGE_FILE_NAME);
	}

	private Map<String, InputStream> findMessageFileStreams() {
		Map<String, InputStream> inputStreams = new HashMap<>(10);
		Iterable<File> messageFiles = findMessageFiles();
		for (File file : messageFiles)
		{
			LOGGER.log(Level.INFO, "Registering XML file {0}", file);
			try
			{
				//noinspection ObjectAllocationInLoop
				inputStreams.put(file.getAbsolutePath(), new FileInputStream(file));
			} catch (FileNotFoundException ex)
			{
				LOGGER.log(Level.WARNING, "Unable to create input stream for " + file, ex);
			}
		}
		return inputStreams;
	}

	private static Map<String, InputStream> findMessageJarStreams() {
		Map<String, InputStream> inputStreams = new HashMap<>(10);
		Iterable<String> jarMessageEntries = findJarMessageEntries();
		for (String entry : jarMessageEntries)
		{
			LOGGER.log(Level.INFO, "Registering XML entry {0}", entry);
			inputStreams.put(entry, MessageFileFinder.class.getClassLoader().getResourceAsStream(entry));
		}
		return inputStreams;
	}

	Map<String, InputStream> findXmlRegistries() {
		Map<String, InputStream> streamsToRead = new HashMap<>(10);
		streamsToRead.putAll(findMessageJarStreams());
		streamsToRead.putAll(findMessageFileStreams());
		return streamsToRead;
	}
}
