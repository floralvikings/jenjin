package com.jenjinstudios.core.io;

import com.jenjinstudios.core.util.FileUtil;
import com.jenjinstudios.core.xml.MessageGroup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
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
	private static final String messageFileName = "Messages.xml";
	private static final Logger LOGGER = Logger.getLogger(MessageFileFinder.class.getName());

	private MessageFileFinder() { }

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

	private static ArrayList<File> findMessageFiles() {
		String rootDir = Paths.get("").toAbsolutePath().toString() + File.separator;
		File rootFile = new File(rootDir);
		return FileUtil.search(rootFile, messageFileName);
	}

	private static LinkedList<InputStream> findMessageFileStreams() {
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

	private static LinkedList<InputStream> findMessageJarStreams() {
		LinkedList<InputStream> inputStreams = new LinkedList<>();
		LinkedList<String> jarMessageEntries = findJarMessageEntries();
		for (String entry : jarMessageEntries)
		{
			LOGGER.log(Level.INFO, "Registering XML entry {0}", entry);
			inputStreams.add(MessageFileFinder.class.getClassLoader().getResourceAsStream(entry));
		}
		return inputStreams;
	}

	static Collection<MessageGroup> findXmlRegistries() {
		LinkedList<InputStream> streamsToRead = new LinkedList<>();
		streamsToRead.addAll(findMessageJarStreams());
		streamsToRead.addAll(findMessageFileStreams());
		return MessageRegistryReader.readXmlStreams(streamsToRead);
	}
}
