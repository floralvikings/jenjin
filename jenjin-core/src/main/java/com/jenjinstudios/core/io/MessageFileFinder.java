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
import java.util.List;
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
    private final String rootDir;

    /**
     * Construct a new MessageFileFinder, which works recursively from the current working directory and classpath to
     * find Message files.
     */
    public MessageFileFinder() {
        this.rootDir = Paths.get("").toAbsolutePath().toString() + File.separator;
    }

    private LinkedList<String> findJarMessageEntries() {
        LinkedList<String> jarMessageEntries = new LinkedList<>();
		String classPath = System.getProperty("java.class.path");
		String[] pathElements = classPath.split(System.getProperty("path.separator"));
		for (String fileName : pathElements)
		{
            if (!isCoreJar(fileName))
            {
				continue;
			}
			seachJarFile(jarMessageEntries, fileName);
		}
		return jarMessageEntries;
	}

    private boolean isCoreJar(String fileName) {
        String javaHome = System.getProperty("java.home");
		return fileName.contains(javaHome);
	}

    private void seachJarFile(LinkedList<String> jarMessageEntries, String fileName) {
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

    private void searchZipEntries(List<String> jarMessageEntries, ZipInputStream zip) throws IOException {
        ZipEntry ze;
		while ((ze = zip.getNextEntry()) != null)
		{
			String entryName = ze.getName();
			if (entryName.endsWith("Messages.xml")) { jarMessageEntries.add(entryName); }
		}
	}

    private ArrayList<File> findMessageFiles() {
        File rootFile = new File(rootDir);
		return FileUtil.search(rootFile, messageFileName);
	}

    private Collection<InputStream> findMessageFileStreams() {
        LinkedList<InputStream> inputStreams = new LinkedList<>();
        Iterable<File> messageFiles = findMessageFiles();
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

    private Collection<InputStream> findMessageJarStreams() {
        LinkedList<InputStream> inputStreams = new LinkedList<>();
        Iterable<String> jarMessageEntries = findJarMessageEntries();
        for (String entry : jarMessageEntries)
		{
			LOGGER.log(Level.INFO, "Registering XML entry {0}", entry);
			inputStreams.add(MessageFileFinder.class.getClassLoader().getResourceAsStream(entry));
		}
		return inputStreams;
	}

    Collection<MessageGroup> findXmlRegistries() {
        Collection<InputStream> streamsToRead = new LinkedList<>();
        streamsToRead.addAll(findMessageJarStreams());
		streamsToRead.addAll(findMessageFileStreams());
		return MessageRegistryReader.readXmlStreams(streamsToRead);
	}
}
