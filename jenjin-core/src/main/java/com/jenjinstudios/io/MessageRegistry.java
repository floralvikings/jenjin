package com.jenjinstudios.io;

import com.jenjinstudios.util.Files;

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
	/** Whether this registry is for a server or not. */
	private final boolean isServer;
	/** Flags whether messages have been registered. */
	private boolean messagesRegistered;

	/**
	 * Construct a new MessageRegistry.
	 * @param isServer Whether or not this registry is for a server.
	 */
	public MessageRegistry(boolean isServer) {
		this.isServer = isServer;
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
			File file = new File(fileName);
			if (file.isDirectory() || !file.exists()) { continue; }
			try (
					FileInputStream inputStream = new FileInputStream(file);
					ZipInputStream zip = new ZipInputStream(inputStream))
			{
				ZipEntry ze;
				while ((ze = zip.getNextEntry()) != null)
				{
					String entryName = ze.getName();
					if (entryName.endsWith("Messages.xml")) { jarMessageEntries.add(entryName); }
				}
			} catch (IOException ex)
			{
				LOGGER.log(Level.WARNING, "Unable to read JAR entry " + fileName, ex);
			}

		}
		return jarMessageEntries;
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
		if (!messagesRegistered)
		{
			LOGGER.log(Level.SEVERE, "Messages not registered!  Please remember to call MessageRegistry.registerXmlMessages()");
		}

		return messageTypesByName.get(name);
	}

	/**
	 * Get the MessageType with the given ID.
	 * @param id The id.
	 * @return The MessageType with the given ID.
	 */
	public MessageType getMessageType(short id) {
		if (!messagesRegistered)
		{
			LOGGER.log(Level.SEVERE, "Messages not registered!  Please remember to call MessageRegistry.registerXmlMessages()");
		}

		return messageTypesByID.get(id);
	}

	/**
	 * Get the class names of argumentTypes for the class with the given registration ID.
	 * @param id The ID to lookup.
	 * @return A LinkedList of class names.
	 */
	public LinkedList<Class> getArgumentClasses(short id) {
		if (!messagesRegistered)
		{
			LOGGER.log(Level.SEVERE, "Messages not registered!  Please remember to call MessageRegistry.registerXmlMessages()");
		}

		LinkedList<Class> temp = new LinkedList<>();

		MessageType type = messageTypesByID.get(id);
		if (type == null)
		{
			String message = "Message " + id + " not registered.";
			LOGGER.log(Level.SEVERE, message);
			throw new RuntimeException(message);
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
	public void disableExecutableMessage(String messageName) {
		LOGGER.log(Level.FINE, "Disabling message: {0}", messageName);
		MessageType type = messageTypesByName.get(messageName);
		short id = type.id;
		ArgumentType[] argumentTypes = type.argumentTypes;
		Class<? extends ExecutableMessage> clientExecutableMessageClass = type.clientExecutableMessageClass;
		Class<? extends ExecutableMessage> serverExecutableMessageClass = type.serverExecutableMessageClass;
		MessageType newMessageType;
		if (isServer)
		{
			newMessageType = new MessageType(id, messageName, argumentTypes, clientExecutableMessageClass, null);
		} else
		{
			newMessageType = new MessageType(id, messageName, argumentTypes, null, serverExecutableMessageClass);
		}
		messageTypesByName.put(messageName, newMessageType);
		messageTypesByID.put(id, newMessageType);

	}

	/** Register all messages found in registry files.  Also checks the JAR file. */
	private void registerXmlMessages() {
		LinkedList<InputStream> streamsToRead = new LinkedList<>();
		addJarMessageEntries(streamsToRead);
		addMessageFiles(streamsToRead);
		readXmlStreams(streamsToRead);
		messagesRegistered = true;
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
				addAllMessages(reader.readMessageTypes(isServer));
				disabled.addAll(reader.readDisabledMessages());
			} catch (Exception ex)
			{
				LOGGER.log(Level.INFO, "Unable to parse XML file", ex);
			}
		}
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
			if (messageType != null && !messageTypesByID.containsKey(messageType.id) && !messageTypesByName.containsKey(messageType.name))
			{
				// Add the message type to the two trees.
				messageTypesByID.put(messageType.id, messageType);
				messageTypesByName.put(messageType.name, messageType);
			}
		}
	}

	public Message getMessage(String name) {
		// TODO Fix this nonsense.
		return new Message(name, this);
	}
}
