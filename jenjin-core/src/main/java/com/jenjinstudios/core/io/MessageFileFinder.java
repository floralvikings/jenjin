package com.jenjinstudios.core.io;

import com.jenjinstudios.core.util.FileUtil;
import com.jenjinstudios.core.xml.MessageGroup;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Paths;
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

    private static Collection<MessageGroup> readXmlStreams(Iterable<InputStream> streamsToRead) {
        Collection<MessageGroup> foundMessages = new LinkedList<>();
        for (InputStream inputStream : streamsToRead)
        {
            try
            {
                JAXBContext jaxbContext = JAXBContext.newInstance(MessageGroup.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                MessageGroup collection = (MessageGroup) jaxbUnmarshaller.unmarshal(inputStream);
                if (foundMessages.contains(collection))
                {
                    LOGGER.log(Level.FINE, "Found duplicate message file; ignoring.");
                } else
                {
                    foundMessages.add(collection);
                }
            } catch (JAXBException | RuntimeException ex)
            {
                LOGGER.log(Level.INFO, "Unable to parse XML file", ex);
            } finally
            {
                closeQuietly(inputStream);
            }
        }
        return foundMessages;
    }

    private static void closeQuietly(Closeable inputStream) {
        try
        {
            inputStream.close();
        } catch (IOException ex)
        {
            LOGGER.log(Level.INFO, "Unable to close stream", ex);
        }
    }

    private Iterable<File> findMessageFiles() {
        File rootFile = new File(rootDir);
        return FileUtil.search(rootFile, MESSAGE_FILE_NAME);
    }

    private Collection<InputStream> findMessageFileStreams() {
        Collection<InputStream> inputStreams = new LinkedList<>();
        Iterable<File> messageFiles = findMessageFiles();
        for (File file : messageFiles)
        {
            LOGGER.log(Level.INFO, "Registering XML file {0}", file);
            try
            {
                //noinspection ObjectAllocationInLoop
                inputStreams.add(new FileInputStream(file));
            } catch (FileNotFoundException ex)
            {
                LOGGER.log(Level.WARNING, "Unable to create input stream for " + file, ex);
            }
        }
        return inputStreams;
    }

    private static Collection<InputStream> findMessageJarStreams() {
        Collection<InputStream> inputStreams = new LinkedList<>();
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
        return readXmlStreams(streamsToRead);
    }
}
