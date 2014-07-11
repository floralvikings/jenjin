package com.jenjinstudios.core.io;

import com.jenjinstudios.core.util.Files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Handles the registration of message classes and the information on how to reconstruct them from raw data.
 * @author Caleb Brinkman
 */
public class MessageRegistry
{
	/** The logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(MessageRegistry.class.getName());
	/** The file name of message registry classed. */
	private static final String messageFileName = "Messages.xml";
	/** A map that stores messages types sorted by ID. */
	private final Map<Short, MessageType> messageTypesByID = new TreeMap<>();
	/** A map that stores message types sorted by name. */
	private final Map<String, MessageType> messageTypesByName = new TreeMap<>();
	private static MessageRegistry messageRegistry;

	public static MessageRegistry getInstance() {
		if (messageRegistry == null)
		{
			messageRegistry = new MessageRegistry();
		}
		return messageRegistry;
	}

	/**
	 * Construct a new MessageRegistry.
	 */
	private MessageRegistry() {
		registerXmlMessages();
	}

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
		if (file.isDirectory() || !file.exists()) { return; }
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
	 * Get the message type with the given name.
	 * @param name The name of the message type.
	 * @return The MessageType with the given name.
	 */
	public MessageType getMessageType(String name) {
		synchronized (messageTypesByName)
		{
			return messageTypesByName.get(name);
		}
	}

	/**
	 * Get the MessageType with the given ID.
	 * @param id The id.
	 * @return The MessageType with the given ID.
	 */
	public MessageType getMessageType(short id) {
		synchronized (messageTypesByID)
		{
			return messageTypesByID.get(id);
		}
	}

	/**
	 * Get the class names of argumentTypes for the class with the given registration ID.
	 * @param id The ID to lookup.
	 * @return A LinkedList of class names.
	 */
	public LinkedList<Class> getArgumentClasses(short id) throws MessageTypeException {
		LinkedList<Class> temp = new LinkedList<>();
		MessageType type;
		synchronized (messageTypesByID)
		{
			type = messageTypesByID.get(id);
		}
		if (type == null)
		{
			throw new MessageTypeException(id);
		} else
		{
			for (int i = 0; i < type.argumentTypes.length; i++)
				temp.add(type.argumentTypes[i].type);
		}

		return temp;
	}

	/**
	 * Disable the ExecutableMessage invoked by the message with the given name.
	 * @param messageName The name of the message.
	 */
	void disableExecutableMessage(String messageName) {
		LOGGER.log(Level.INFO, "Disabling message: {0}", messageName);
		MessageType type;
		synchronized (messageTypesByName)
		{
			type = messageTypesByName.get(messageName);
		}
		short id = type.id;
		ArgumentType[] argumentTypes = type.argumentTypes;
		MessageInfo info = new MessageInfo(id, messageName, argumentTypes);
		MessageType newMessageType = new MessageType(info);
		messageTypesByName.put(messageName, newMessageType);
		messageTypesByID.put(id, newMessageType);

	}

	/** Register all messages found in registry files.  Also checks the JAR file. */
	private void registerXmlMessages() {
		LinkedList<InputStream> streamsToRead = new LinkedList<>();
		addJarMessageEntries(streamsToRead);
		addMessageFiles(streamsToRead);
		readXmlStreams(streamsToRead);
	}

	/**
	 * Parse the XML streams and register the discovered MessageTypes.
	 * @param streamsToRead The streams containing the XML data to be parsed.
	 */
	private void readXmlStreams(Iterable<InputStream> streamsToRead) {
		LinkedList<String> disabled = new LinkedList<>();
		for (InputStream inputStream : streamsToRead)
		{
			try
			{
				MessageXmlReader reader = new MessageXmlReader(inputStream);
				addAllMessages(reader.readMessageTypes());
				disabled.addAll(reader.readDisabledMessages());
				inputStream.close();
			} catch (Exception ex)
			{
				LOGGER.log(Level.INFO, "Unable to parse XML file", ex);
			}
		}
		disableExecutableMessages(disabled);
	}

	private void disableExecutableMessages(LinkedList<String> disabled) {
		for (String disabledMessageName : disabled)
		{
			disableExecutableMessage(disabledMessageName);
		}
	}

	/**
	 * Add the Messages.xml entries in the working directory and add their InputStream to the given list.
	 * @param streamsToRead The list to which to add the input streams.
	 */
	private void addMessageFiles(List<InputStream> streamsToRead) {
		ArrayList<File> messageFiles = findMessageFiles();
		for (File f : messageFiles)
		{
			LOGGER.log(Level.INFO, "Registering XML file {0}", f);
			try
			{
				streamsToRead.add(new FileInputStream(f));
			} catch (IOException ex)
			{
				LOGGER.log(Level.WARNING, "Unable to create input stream for " + f, ex);
			}
		}
	}

	/**
	 * Add the Messages.xml entries in the classpath and add their InputStream to the given list.
	 * @param streamsToRead The list to which to add the input streams.
	 */
	private void addJarMessageEntries(List<InputStream> streamsToRead) {
		LinkedList<String> jarMessageEntries = findJarMessageEntries();
		for (String entry : jarMessageEntries)
		{
			LOGGER.log(Level.INFO, "Registering XML entry {0}", entry);
			streamsToRead.add(getClass().getClassLoader().getResourceAsStream(entry));
		}
	}

	/**
	 * Register all message types within the given list.
	 * @param messageTypes The list of message types to add.
	 */
	private void addAllMessages(List<MessageType> messageTypes) {
		for (MessageType messageType : messageTypes)
		{
			if (messageType != null && !messageTypesByID.containsKey(messageType.id) && !messageTypesByName
				  .containsKey(messageType.name))
			{
				// Add the message type to the two trees.
				messageTypesByID.put(messageType.id, messageType);
				messageTypesByName.put(messageType.name, messageType);
			}
		}
	}

	public Message createMessage(String name) { return new Message(name, this); }
}
