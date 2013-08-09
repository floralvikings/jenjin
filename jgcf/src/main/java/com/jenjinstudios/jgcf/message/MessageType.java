package com.jenjinstudios.jgcf.message;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to define the ID, name, and Argument Types for each type of message contained in the XML files.
 * @author Caleb Brinkman */
public class MessageType
{
	/** The Logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(MessageType.class.getName());
	/** The ID of this message type. */
	public final short id;
	/** The name of this message type. */
	public final String name;
	/** The argumentTypes for this message type. */
	public final ArgumentType[] argumentTypes;
	/** The class of the executable message associated with this type. */
	public final Class<? extends ExecutableMessage> executableMessageClass;

	/**
	 * Construct a new MessageType with the given information.
	 *
	 * @param id        The ID of the message type.
	 * @param name      The name of the message type.
	 * @param argumentTypes The argumentTypes of the message type.
	 * @param executableMessageClass The class of the ExecutableMessage
	 */
	public MessageType(short id, String name, ArgumentType[] argumentTypes, Class<? extends ExecutableMessage> executableMessageClass)
	{
		this.id = id;
		this.name = name;
		this.argumentTypes = argumentTypes;
		this.executableMessageClass = executableMessageClass;
	}

	/**
	 * Get a message type by parsing the XML element specified.  Returns null if the element could not be properly
	 * parsed.
	 * @param messageElement The XML Element.
	 * @return A MessageType retrieved from the XML element.
	 */
	public static MessageType parseMessageElement(Element messageElement)
	{
		short id;
		String name;
		ArgumentType[] argumentTypes;
		Class<? extends ExecutableMessage> executableMessageClass;

		/*
		Should look like this:
			<message id="0" name="name">
				<executable language="java" side="client">com.jenjinstudios.jgcf.message.ClassName</executable>
				<argument name="argumentName" type="String" encrypt="true" />
			</message>
		*/

		id = Short.parseShort(messageElement.getAttribute("id"));
		name = messageElement.getAttribute("name");
		argumentTypes = parseArgumentNodes(messageElement);
		executableMessageClass = getExecutableMessageClass(messageElement);

		MessageType messageType = null;

		if(argumentTypes != null)
			messageType = new MessageType(id, name, argumentTypes, executableMessageClass);

		return messageType;
	}

	/**
	 * Parse the supplied XML element looking for an executable tag with the attribute language="java".  If multiple
	 * executable tags with the language="java" attribute exist, the last one found is used.
	 * @param messageElement The message XML element.
	 * @return The class derived from the XML element.
	 */
	private static Class<? extends ExecutableMessage> getExecutableMessageClass(Element messageElement)
	{
		NodeList executableNodes = messageElement.getElementsByTagName("executable");
		String executableMessageClassName = null;
		Class<? extends ExecutableMessage> executableMessageClass = null;
		// Parse executable tags for those containing language="java"
		for(int i=0; i<executableNodes.getLength(); i++)
		{
			Node currentExecutableNode = executableNodes.item(i);
			Element currentExecutableElement = (Element)currentExecutableNode;
			String languageAttribute = currentExecutableElement.getAttribute("language");
			// If it's in java, set the executable message class name.
			if(languageAttribute.equalsIgnoreCase("java"))
				executableMessageClassName = currentExecutableElement.getTextContent();
		}
		if(executableMessageClassName != null)
		{
			try
			{
				executableMessageClass = (Class<? extends ExecutableMessage>) Class.forName(executableMessageClassName);
			} catch (ClassNotFoundException | ClassCastException e)
			{
				LOGGER.log(Level.WARNING, "Incorrect Executable Message specified: ", e);
			}
		}
		return executableMessageClass;
	}

	/**
	 * Parse the given XML element for argument elements.
	 * @param messageElement The XML element.
	 * @return An array of discovered ArgumentTypes.
	 */
	private static ArgumentType[] parseArgumentNodes(Element messageElement)
	{
		ArgumentType[] argumentTypes;
		NodeList argumentNodes = messageElement.getElementsByTagName("argument");
		argumentTypes = new ArgumentType[argumentNodes.getLength()];
		for(int i=0; i<argumentNodes.getLength(); i++)
		{
			Element currentArgElement = (Element) argumentNodes.item(i);
			String name = currentArgElement.getAttribute("name");
			Class type = parseClassName(currentArgElement.getAttribute("type"));
			boolean encrypt = Boolean.parseBoolean(currentArgElement.getAttribute("encrypt"));

			if(name == null || type == null)
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
	private static Class parseClassName(String className)
	{
		Class type = null;

		switch (className)
		{
			case "boolean":
				type = boolean.class;
				break;
			case "byte":
				type = byte.class;
				break;
			case "short":
				type = short.class;
				break;
			case "char":
				type = char.class;
				break;
			case "int":
				type = int.class;
				break;
			case "long":
				type = long.class;
				break;
			case "float":
				type = float.class;
				break;
			case "double":
				type = double.class;
				break;
			case "String":
				// TODO Handle encrypted messages
				type = String.class;
				break;
			case "byte[]":
				type = byte[].class;
				break;
			case "String[]":
				// TODO Handle encrypted messages
				type = String[].class;
				break;
		}

		return type;
	}
}
