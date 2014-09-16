package com.jenjinstudios.core.io;

import com.jenjinstudios.core.xml.ArgumentType;
import com.jenjinstudios.core.xml.MessageType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Arrays;
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
	private final Element messageElement;

	public MessageTypeParser(Element messageElement) {

		this.messageElement = messageElement;
	}

	/**
	 * Get a message type by parsing the XML element specified.  Returns null if the element could not be properly
	 * parsed.
	 * @return A MessageType retrieved from the XML element.
	 */
	public MessageType parseMessage() {
		short id;
		String name;
		ArgumentType[] argumentTypes;
		id = Short.parseShort(messageElement.getAttribute("id"));
		name = messageElement.getAttribute("name");
		argumentTypes = parseArgumentNodes();

		MessageType messageType = null;

		if (argumentTypes != null)
		{
			messageType = new MessageType();
			messageType.setId(id);
			messageType.setName(name);
			messageType.getArguments().addAll(Arrays.asList(argumentTypes));
			messageType.setExecutable(getExecutableMessageClassName());
		}

		return messageType;
	}

	private String getExecutableMessageClassName() {
		String r = null;
		NodeList executableNodes = messageElement.getElementsByTagName("executable");
		if (executableNodes.getLength() > 0)
		{
			r = executableNodes.item(0).getTextContent();
		}
		return r;
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
			String type = currentArgElement.getAttribute("type");
			boolean encrypt = Boolean.parseBoolean(currentArgElement.getAttribute("encrypt"));

			if (name == null || type == null)
			{
				argumentTypes = null;
				break;
			}

			argumentTypes[i] = new ArgumentType();
			argumentTypes[i].setName(name);
			argumentTypes[i].setType(type);
			argumentTypes[i].setEncrypt(encrypt);
		}
		return argumentTypes;
	}

	/**
	 * Derive a class from an argument element type attribute.
	 * @param className The name of the class as read from the XML file.
	 * @return The class type if a correct string is parsed.  Null otherwise.
	 */
	@SuppressWarnings({"OverlyLongMethod", "OverlyComplexMethod"})
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