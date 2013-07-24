package com.jenjinstudios.jgcf.message;

import com.jenjinstudios.util.FileUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * The {@code ExecutableMessage} class should be extended to create self-handling messages on the server.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public abstract class ExecutableMessage implements Runnable
{
	/** The collection of ExecutableMessage classes. */
	protected static final HashMap<Short, Class<? extends ExecutableMessage>> executableMessageClasses = new HashMap<>();
	/** The Logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(ExecutableMessage.class.getName());
	/** The file name for ExecutableMessage registry files. */
	private static final String execMessageFileName = "ExecutableMessages.xml";
	/** Keeps track of whether XML messages have been registered. */
	private static boolean messagesRegistered = false;
	/** The BaseMessage for this ExecutableMessage. */
	private final BaseMessage message;

	/**
	 * Construct an ExecutableMessage with the given BaseMessage.
	 *
	 * @param message The BaseMessage.
	 */
	protected ExecutableMessage(BaseMessage message)
	{
		if (message.getID() != getBaseMessageID())
		{
			throw new IllegalArgumentException("BaseMessage supplied to " + getClass().getName() + " constructor has invalid ID.");
		}
		this.message = message;
	}

	/**
	 * Keeps track of whether XML messages have been registered.
	 *
	 * @return Whether messages have been registered.
	 */
	public static boolean areMessagesRegistered()
	{
		return messagesRegistered;
	}

	/**
	 * Set whether messages are registered.
	 *
	 * @param messagesRegistered Whether messages are registered.
	 */
	private static void setMessagesRegistered(boolean messagesRegistered)
	{
		ExecutableMessage.messagesRegistered = messagesRegistered;
	}

	/** Register all ExecutableMessages found in registry files. */
	protected static void registerMessages()
	{
		try
		{
			CodeSource src = ExecutableMessage.class.getProtectionDomain().getCodeSource();

			if (src != null)
			{
				URL jar = src.getLocation();
				ZipInputStream zip = new ZipInputStream(jar.openStream());
				ZipEntry ze;
				while ((ze = zip.getNextEntry()) != null)
				{
					String entryName = ze.getName();
					if (entryName.contains("ExecutableMessages.xml"))
					{
						parseXmlStream(ExecutableMessage.class.getClassLoader().getResourceAsStream(entryName));
					}
				}
			}
			ArrayList<File> execMessageFiles = findExecMessageFiles();
			for (File xmlFile : execMessageFiles) parseXmlFile(xmlFile);
			setMessagesRegistered(true);
		} catch (IOException | ClassNotFoundException | ParserConfigurationException | SAXException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Parse the given XML file for registry information.
	 *
	 * @param xmlFile The XML file to be parsed.
	 * @throws java.io.IOException      If this exception occurs.
	 * @throws ClassNotFoundException   If this exception occurs.
	 * @throws javax.xml.parsers.ParserConfigurationException
	 *                                  If this exception occurs.
	 * @throws org.xml.sax.SAXException If this exception occurs.
	 */
	private static void parseXmlFile(File xmlFile) throws ParserConfigurationException, IOException, SAXException, ClassNotFoundException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		parseXmlStream(new FileInputStream(xmlFile));
	}

	/**
	 * Parse the XML stream.
	 *
	 * @param inputStream The stream to be parsed
	 * @throws ParserConfigurationException If this exception occurs.
	 * @throws IOException                  If this exception occurs.
	 * @throws SAXException                 If this exception occurs.
	 * @throws ClassNotFoundException       If this exception occurs.
	 */
	private static void parseXmlStream(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException, ClassNotFoundException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(inputStream);

		doc.getDocumentElement().normalize();

		NodeList nList = doc.getElementsByTagName("executable_message");

		for (int i = 0; i < nList.getLength(); i++)
		{
			Element element;
			if (nList.item(i) instanceof Element)
				element = (Element) nList.item(i);
			else
				continue;

			Node baseMessageIDNode = element.getElementsByTagName("base_message_id").item(0);
			String baseMessageIDText = baseMessageIDNode.getTextContent();
			short baseMessageID = Short.parseShort(baseMessageIDText);

			Node executableMessageClassNode = element.getElementsByTagName("class_name").item(0);
			String executableMessageClassName = executableMessageClassNode.getTextContent();
			Class<?> executableMessageClass = Class.forName(executableMessageClassName);

			//noinspection unchecked
			executableMessageClasses.put(baseMessageID, (Class<? extends ExecutableMessage>) executableMessageClass);
		}
	}

	/**
	 * Find files that match the format for ExecutableMessage registry.
	 *
	 * @return An ArrayList of XML files.
	 */
	public static ArrayList<File> findExecMessageFiles()
	{
		String rootDir = Paths.get("").toAbsolutePath().toString() + File.separator;
		File rootFile = new File(rootDir);
		return FileUtil.findFilesWithName(rootFile, execMessageFileName);
	}

	/** Run the synced portion of this message. */
	public abstract void runSynced();

	/** Run asynchronous portion of this message. */
	public abstract void runASync();

	/**
	 * Get the ID number of the BaseMessage type associated with this ExecutableMessage.
	 *
	 * @return The ID of the message type process by this ExecutableMessage.
	 */
	public abstract short getBaseMessageID();

	/** Calls the {@code runSynced} method. */
	public final void run()
	{
		runSynced();
	}

	/**
	 * The BaseMessage for this ExecutableMessage.
	 *
	 * @return The BaseMessage used by this ExecutableMessage
	 */
	public BaseMessage getMessage()
	{
		return message;
	}
}
