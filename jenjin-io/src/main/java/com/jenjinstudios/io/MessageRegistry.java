package com.jenjinstudios.io;

import com.jenjinstudios.util.FileUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Handles the registration of message classes and the information on how to reconstruct them from raw data.
 *
 * @author Caleb Brinkman
 */
public class MessageRegistry
{
	/** The logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(MessageRegistry.class.getName());
	/** The file name of message registry classed. */
	private static final String messageFileName = "Messages.xml";
	/** A map that stores messages types sorted by ID. */
	private static final TreeMap<Short, MessageType> messageTypesByID = new TreeMap<>();
	/** A map that stores message types sorted by name. */
	private static final TreeMap<String, MessageType> messageTypesByName = new TreeMap<>();
	/** Flags whether messages have been registered. */
	private static boolean messagesRegistered;

	/**
	 * Register all messages found in registry files.  Also checks the JAR file.
	 *
	 * @param isServer Whether the program registering messages is a server or client-side program.
	 * @throws java.io.IOException      If there is an IO exception when reading XML files.
	 * @throws javax.xml.parsers.ParserConfigurationException
	 *                                  If there is an error parsing XML files.
	 * @throws org.xml.sax.SAXException If there is an error parsing XML files.
	 */
	public static void registerXmlMessages(boolean isServer) throws ParserConfigurationException, SAXException, IOException
	{
		try
		{
			String classPath = System.getProperty("java.class.path");
			String[] pathElements = classPath.split(System.getProperty("path.separator"));

			for (String fileName : pathElements)
			{
				File file = new File(fileName);
				if (file.isDirectory() || !file.exists())
				{
					continue;
				}
				FileInputStream inputStream = new FileInputStream(file);
				ZipInputStream zip = new ZipInputStream(inputStream);
				ZipEntry ze;
				while ((ze = zip.getNextEntry()) != null)
				{
					String entryName = ze.getName();
					if (entryName.endsWith("Messages.xml"))
					{
						System.out.println("Registering messages in: " + entryName);
						parseXmlStream(MessageRegistry.class.getClassLoader().getResourceAsStream(entryName), isServer);
					}
				}
			}

			// Search the directories
			ArrayList<File> messageFiles = findMessageFiles();
			for (File f : messageFiles) parseXmlFile(f, isServer);
			messagesRegistered = true;

		} catch (IOException | SAXException | ParserConfigurationException e)
		{
			LOGGER.log(Level.INFO, "Unable to parse XML files.", e);
			throw e;
		}
	}

	/**
	 * Look for files that match the message registry format.
	 *
	 * @return An ArrayList of message registry files.
	 */
	private static ArrayList<File> findMessageFiles()
	{
		String rootDir = Paths.get("").toAbsolutePath().toString() + File.separator;
		File rootFile = new File(rootDir);
		return FileUtil.findFilesWithName(rootFile, messageFileName);
	}

	/**
	 * Parse a stream for XML messages.
	 *
	 * @param stream   The stream to parse.
	 * @param isServer Whether the program registering message is server or client side.
	 * @throws java.io.IOException      If this exception occurs.
	 * @throws javax.xml.parsers.ParserConfigurationException
	 *                                  If this exception occurs.
	 * @throws org.xml.sax.SAXException If this exception occurs.
	 */
	private static void parseXmlStream(InputStream stream, boolean isServer) throws IOException, SAXException, ParserConfigurationException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(stream);

		doc.getDocumentElement().normalize();

		NodeList messageElements = doc.getElementsByTagName("message");

		for (int i = 0; i < messageElements.getLength(); i++)
		{
			Element currentMessageElement = (Element) messageElements.item(i);
			MessageType messageType = MessageTypeFactory.parseMessageElement(currentMessageElement, isServer);
			if (messageType != null)
			{
				// Add the message type to the two trees.
				messageTypesByID.put(messageType.id, messageType);
				messageTypesByName.put(messageType.name, messageType);
			}
		}
	}

	/**
	 * Parse the given XML file and register message therein.
	 *
	 * @param xmlFile  The XML file to be parsed.
	 * @param isServer Whether the program registering message is server or client side.
	 * @throws java.io.IOException      If this exception occurs.
	 * @throws javax.xml.parsers.ParserConfigurationException
	 *                                  If this exception occurs.
	 * @throws org.xml.sax.SAXException If this exception occurs.
	 */
	private static void parseXmlFile(File xmlFile, boolean isServer) throws IOException, SAXException, ParserConfigurationException
	{
		InputStream xmlStream = new FileInputStream(xmlFile);
		parseXmlStream(xmlStream, isServer);
	}

	/**
	 * Get the message type with the given name.
	 *
	 * @param name The name of the message type.
	 * @return The MessageType with the given name.
	 */
	public static MessageType getMessageType(String name)
	{
		if (!messagesRegistered)
		{
			LOGGER.log(Level.SEVERE, "Messages not registered!  Please remeber to call MessageRegistry.registerXmlMessages()");
		}

		return messageTypesByName.get(name);
	}

	/**
	 * Get the MessageType with the given ID.
	 *
	 * @param id The id.
	 * @return The MessageType with the given ID.
	 */
	public static MessageType getMessageType(short id)
	{
		if (!messagesRegistered)
		{
			LOGGER.log(Level.SEVERE, "Messages not registered!  Please remeber to call MessageRegistry.registerXmlMessages()");
		}

		return messageTypesByID.get(id);
	}

	/**
	 * Get the class names of argumentTypes for the class with the given registration ID.
	 *
	 * @param id The ID to lookup.
	 * @return A LinkedList of class names.
	 */
	public static LinkedList<Class> getArgumentClasses(short id)
	{
		if (!messagesRegistered)
		{
			LOGGER.log(Level.SEVERE, "Messages not registered!  Please remeber to call MessageRegistry.registerXmlMessages()");
		}

		LinkedList<Class> temp = new LinkedList<>();

		synchronized (messageTypesByID)
		{
			MessageType type = messageTypesByID.get(id);
			for (int i = 0; i < type.argumentTypes.length; i++)
				temp.add(type.argumentTypes[i].type);
		}
		return temp;
	}

}
