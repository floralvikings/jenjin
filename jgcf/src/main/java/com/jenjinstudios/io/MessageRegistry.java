package com.jenjinstudios.io;

import com.jenjinstudios.clientutil.file.FileUtil;
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
import java.util.*;
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
	/** The collection of message argument class names. */
	private static final TreeMap<Short, Class[]> argumentRegistry = new TreeMap<>();
	/** Flags whether messages have been registered. */
	private static boolean messagesRegistered;
	/** The file name of message registry classed. */
	private static final String messageFileName = "Messages.xml";

	/**
	 * Register all messages found in registry files.  Also checks the JAR file.
	 */
	public static void registerXmlMessages()
	{
		try
		{

			// Search the JAR
			CodeSource src = MessageRegistry.class.getProtectionDomain().getCodeSource();

			if( src != null )
			{
				URL jar = src.getLocation();
				ZipInputStream zip = new ZipInputStream( jar.openStream());
				ZipEntry ze;
				while( ( ze = zip.getNextEntry() ) != null )
				{
					String entryName = ze.getName();
					if( entryName.equals("Messages.xml") )
						parseXmlStream(MessageRegistry.class.getClassLoader().getResourceAsStream(entryName));
				}
			}

			// Search the directories
			ArrayList<File> messageFiles = findMessageFiles();
			for(File f : messageFiles) parseXmlFile(f);
			messagesRegistered = true;

		}
		catch (IOException | SAXException | ParserConfigurationException e)
		{
			LOGGER.log(Level.INFO, "Unable to parse JAR file.", e);
		}
	}

	/**
	 * Parse the given XML file and register message therein.
	 * @param xmlFile The XML file to be parsed.
	 * @throws java.io.IOException If this exception occurs.
	 * @throws javax.xml.parsers.ParserConfigurationException If this exception occurs.
	 * @throws org.xml.sax.SAXException If this exception occurs.
	 */
	private static void parseXmlFile(File xmlFile) throws IOException, SAXException, ParserConfigurationException
	{
		InputStream xmlStream = new FileInputStream(xmlFile);
		parseXmlStream(xmlStream);
	}

	/**
	 * Parse a stream for XML messages.
	 * @param stream The stream to parse.
	 * @throws java.io.IOException If this exception occurs.
	 * @throws javax.xml.parsers.ParserConfigurationException If this exception occurs.
	 * @throws org.xml.sax.SAXException If this exception occurs.
	 */
	private static void parseXmlStream(InputStream stream) throws IOException, SAXException, ParserConfigurationException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(stream);

		doc.getDocumentElement().normalize();

		NodeList nList = doc.getElementsByTagName("message");

		for (int i = 0; i < nList.getLength(); i++)
		{
			Node node = nList.item(i);

			short id;
			Class[] argClasses;
			Element element = (Element) node;

			id = Short.parseShort(element.getAttribute("id"));

			NodeList argElements = element.getElementsByTagName("argument");
			argClasses = parseArgumentNodes(argElements);

			registerMessage(id, argClasses);
		}
	}

	/**
	 * Add the given message ID and argument classes to the registry.
	 * @param id The ID of the message type.
	 * @param argClasses The classes of the arguments to be used by the message.
	 */
	private static void registerMessage(short id, Class[] argClasses)
	{
		argumentRegistry.put(id, argClasses);
	}

	/**
	 * Parse argument XML nodes in the XML registry file and return the classes contained therein.  This class should
	 * only be called using XML elements called "argument."
	 * @param argElements The NodeList containing the argument elements.
	 * @return An Array of Class objects representing the types of arguments found.
	 */
	private static Class[] parseArgumentNodes(NodeList argElements)
	{
		Class[] args = new Class[argElements.getLength()];
		for (int i = 0; i < argElements.getLength(); i++)
		{
			String className = argElements.item(i).getTextContent();

			switch (className)
			{
				case "boolean":
					args[i] = boolean.class;
					break;
				case "byte":
					args[i] = byte.class;
					break;
				case "short":
					args[i] = short.class;
					break;
				case "char":
					args[i] = char.class;
					break;
				case "int":
					args[i] = int.class;
					break;
				case "long":
					args[i] = long.class;
					break;
				case "float":
					args[i] = float.class;
					break;
				case "double":
					args[i] = double.class;
					break;
				case "String":
					args[i] = String.class;
					break;
				case "byte[]":
					args[i] = byte[].class;
					break;
				case "String[]":
					args[i] = String[].class;
					break;
			}
		}
		return args;
	}

	/**
	 * Get the class names of arguments for the class with the given registration ID.
	 *
	 * @param id The ID to lookup.
	 * @return A LinkedList of class names.
	 */
	public static LinkedList<Class> getArgumentClasses(short id)
	{
		if(!hasMessagesRegistered())
			registerXmlMessages();
		LinkedList<Class> temp = new LinkedList<>();
		synchronized (argumentRegistry)
		{
			if(!messagesRegistered)
				registerXmlMessages();
			Class[] args = argumentRegistry.get(id);
			if(args != null)
			{
				List<Class> argsList = Arrays.asList(args);
				temp.addAll(argsList);
			}else
			{
				LOGGER.log(Level.SEVERE, "Message ID not found in registry: " + id);
			}
		}
		return temp;
	}

	/**
	 * Get whether messages have been registered.
	 *
	 * @return Whether messages have been registered.
	 */
	public static boolean hasMessagesRegistered()
	{
		return messagesRegistered;
	}

	/**
	 * Look for files that match the message registry format.
	 * @return An ArrayList of message registry files.
	 */
	private static ArrayList<File> findMessageFiles()
	{
		String rootDir = Paths.get("").toAbsolutePath().toString() + File.separator;
		File rootFile = new File(rootDir);
		return FileUtil.findFilesWithName(rootFile, messageFileName);
	}
}
