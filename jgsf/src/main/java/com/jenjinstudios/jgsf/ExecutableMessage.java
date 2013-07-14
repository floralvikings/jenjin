package com.jenjinstudios.jgsf;

import com.jenjinstudios.clientutil.file.FileUtil;
import com.jenjinstudios.message.BaseMessage;
import org.reflections.Reflections;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code ExecutableMessage} class should be extended to create self-handling messages on the server.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("unused")
public abstract class ExecutableMessage implements Cloneable
{
	/** The Logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(ExecutableMessage.class.getName());
	/** The reflections object used for finding classes. */
	private static final Reflections reflections = new Reflections();
	/** The collection of ExecutableMessage classes. */
	private static final HashMap<Short, Class<? extends ExecutableMessage>> executableMessageClasses = new HashMap<>();
	/** Keeps track of whether XML messages have been registered. */
	private static boolean messagesRegistered = false;
	/** The file name for ExecutableMessage registry files. */
	private static final String execMessageFileName = "ExecutableMessages.xml";

	/**
	 * Construct a new ExecutableMessage.  Must be implemented by subclasses.
	 *
	 * @param handler The handler using this ExecutableMessage.
	 * @param message The message.
	 */
	protected ExecutableMessage(ClientHandler handler, BaseMessage message)
	{
		if(message.getID() != getBaseMessageID())
		{
			throw new IllegalArgumentException("BaseMessage supplied to " + getClass().getName() + " constructor has invalid ID.");
		}
	}

	/** Run the synced portion of this message. */
	public abstract void runSynced();

	/** Run asynchronous portion of this message. */
	public abstract void runASync();

	/**
	 * Get the ID number of the BaseMessage type associated with this ExecutableMessage.
	 * @return The ID of the message type process by this ExecutableMessage.
	 */
	public abstract short getBaseMessageID();

	/**
	 * Get the class of the ExecutableMessage that handles the given BaseMessage.
	 *
	 * @param handler The client handler to use the ExecutableMessage.
	 * @param message The message.
	 * @return The class of the ExecutableMessage that handles the given BaseMessage.
	 */
	public static ExecutableMessage getExecutableMessageFor(ClientHandler handler, BaseMessage message)
	{
		ExecutableMessage r = null;
		if(!messagesRegistered) registerMessages();

		Class<? extends ExecutableMessage> execClass = executableMessageClasses.get(message.getID());

		try
		{
			Constructor<? extends ExecutableMessage> execConstructor;
			//noinspection unchecked
			execConstructor = (Constructor<? extends ExecutableMessage>) execClass.getConstructors()[0];
			r = execConstructor.newInstance(handler, message);
		} catch ( InvocationTargetException | InstantiationException | IllegalAccessException e)
		{
			LOGGER.log(Level.SEVERE, "Constructor not correct for: " + execClass.getName(), e);
		}

		return r;
	}

	/**
	 * Register all ExecutableMessages found in registry files.
	 */
	private static void registerMessages()
	{
		ArrayList<File> execMessageFiles = findExecMessageFiles();
		for(File xmlFile : execMessageFiles) parseXmlFile(xmlFile);
		messagesRegistered = true;
	}

	/**
	 * Parse the given XML file for registry information.
	 * @param xmlFile The XML file to be parsed.
	 */
	private static void parseXmlFile(File xmlFile)
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(xmlFile);

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("executable_message");

			for(int i=0; i<nList.getLength(); i++)
			{
				Element element;
				if(nList.item(i) instanceof  Element)
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

		} catch (ParserConfigurationException | SAXException | IOException | ClassNotFoundException e)
		{
			LOGGER.log(Level.SEVERE, "Unable to read Messages.xml", e);
		}
	}

	/**
	 * Find files that match the format for ExecutableMessage registry.
	 * @return An ArrayList of XML files.
	 */
	public static ArrayList<File> findExecMessageFiles()
	{
		String rootDir = Paths.get("").toAbsolutePath().toString() + File.separator;
		File rootFile = new File(rootDir);
		return FileUtil.findFilesWithName(rootFile, execMessageFileName);
	}
}
