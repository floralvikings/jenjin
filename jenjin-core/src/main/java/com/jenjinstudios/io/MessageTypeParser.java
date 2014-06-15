package com.jenjinstudios.io;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generates MessageTypes based on XML elements.
 * @author Caleb Brinkman
 */
class MessageTypeParser
{
	/** The Logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(MessageTypeParser.class.getName());
	/** The XML element of the message being parsed. */
	private Element messageElement;

	public MessageTypeParser(Element messageElement) {

		this.messageElement = messageElement;
	}

	/**
	 * Get a message type by parsing the XML element specified.  Returns null if the element could not be properly parsed.
	 * @return A MessageType retrieved from the XML element.
	 */
	public MessageType parseMessage() {
		short id;
		String name;
		ArgumentType[] argumentTypes;
		List<Class<? extends ExecutableMessage>> classes = new LinkedList<>();
		id = Short.parseShort(messageElement.getAttribute("id"));
		name = messageElement.getAttribute("name");
		argumentTypes = parseArgumentNodes();
		classes.addAll(getExecutableMessageClasses());

		MessageType messageType = null;

		if (argumentTypes != null)
			messageType = new MessageType(id, name, argumentTypes, classes);

		return messageType;
	}

	/**
	 * Parse the supplied XML element looking for an executable tag with the attribute language="java".  If multiple
	 * executable tags with the language="java" attribute exist, the last one found is used.
	 * @return The class derived from the XML element.
	 */
	@SuppressWarnings("unchecked")
	private List<Class<? extends ExecutableMessage>> getExecutableMessageClasses() {
		NodeList executableNodes = messageElement.getElementsByTagName("executable");
		String executableMessageClassName;
		List<Class<? extends ExecutableMessage>> executableMessageClasses = new LinkedList<>();
		// Parse executable tags for those containing language="java"
		for (int i = 0; i < executableNodes.getLength(); i++)
		{
			Node currentExecutableNode = executableNodes.item(i);
			Element currentExecutableElement = (Element) currentExecutableNode;
			String languageAttribute = currentExecutableElement.getAttribute("language");
			// If it's in java, set the executable message class name.
			if ("java".equalsIgnoreCase(languageAttribute))
			{
				executableMessageClassName = currentExecutableElement.getTextContent();
				try
				{
					executableMessageClasses.add((Class<? extends ExecutableMessage>)
							Class.forName(executableMessageClassName));
				} catch (ClassNotFoundException e)
				{
					LOGGER.log(Level.INFO,
							"Unable to locate Executable Message specified: " +
									executableMessageClassName, e);
				}
			}
		}
		return executableMessageClasses;
	}

	/**
	 * Parse the given XML element for argument elements.
	 * @return An array of discovered ArgumentTypes.
	 */
	private ArgumentType[] parseArgumentNodes() {
		ArgumentType[] argumentTypes;
		NodeList argumentNodes = messageElement.getElementsByTagName("argument");
		argumentTypes = new ArgumentType[argumentNodes.getLength()];
		for (int i = 0; i < argumentNodes.getLength(); i++)
		{
			Element currentArgElement = (Element) argumentNodes.item(i);
			String name = currentArgElement.getAttribute("name");
			Class type = parseClassName(currentArgElement.getAttribute("type"));
			boolean encrypt = Boolean.parseBoolean(currentArgElement.getAttribute("encrypt"));

			if (name == null || type == null)
			{
				argumentTypes = null;
				break;
			}

			argumentTypes[i] = new ArgumentType(name, type, encrypt);
		}
		return argumentTypes;
	}

	/**
	 * Derive a class from an argument element type attribute.
	 * @param className The name of the class as read from the XML file.
	 * @return The class type if a correct string is parsed.  Null otherwise.
	 */
	private static Class parseClassName(String className) {
		Class type = null;
		switch (className)
		{
			case "boolean":
				type = Boolean.class;
				break;
			case "byte":
				type = Byte.class;
				break;
			case "short":
				type = Short.class;
				break;
			case "int":
				type = Integer.class;
				break;
			case "long":
				type = Long.class;
				break;
			case "float":
				type = Float.class;
				break;
			case "double":
				type = Double.class;
				break;
			case "String":
				type = String.class;
				break;
			case "byte[]":
				type = byte[].class;
				break;
			case "String[]":
				type = String[].class;
				break;
		}
		return type;
	}
}