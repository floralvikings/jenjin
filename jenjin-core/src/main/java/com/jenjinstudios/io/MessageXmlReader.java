package com.jenjinstudios.io;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Used to parse Messages.xml files.
 * @author Caleb Brinkman
 */
public class MessageXmlReader
{
	/** The XML document storing the NPC data. */
	private final Document messageDoc;

	/**
	 * Construct a new MessageXmlReader.
	 * @param inputStream The stream containing the xml.
	 * @throws java.io.IOException If there is an error reading the InputStream.
	 * @throws javax.xml.parsers.ParserConfigurationException If there is an error configuring the XML parser.
	 * @throws org.xml.sax.SAXException If there is an error parsing the XML.
	 */
	public MessageXmlReader(InputStream	inputStream) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		messageDoc = builder.parse(inputStream);
		messageDoc.getDocumentElement().normalize();
	}

	/**
	 * Read the generated XML document and return a list of message types.
	 * @param isServer Whether the parser should look for server-side executable messages.
	 * @return The list of parsed message types.
	 */
	public List<MessageType> readMessageTypes(boolean isServer)
	{
		LinkedList<MessageType> messageTypes = new LinkedList<>();
		NodeList messageElements = messageDoc.getElementsByTagName("message");

		for (int i = 0; i < messageElements.getLength(); i++)
		{
			Element currentMessageElement = (Element) messageElements.item(i);
			MessageType messageType = MessageTypeParser.parseMessageElement(currentMessageElement, isServer);
			messageTypes.add(messageType);
		}

		return messageTypes;
	}

	/**
	 * Read all the message types that should be disabled.
	 * @return The disabled message types.
	 */
	public Collection<? extends String> readDisabledMessages() {
		LinkedList<String> messageNames = new LinkedList<>();
		NodeList disabledElements = messageDoc.getElementsByTagName("disabled_message");

		for (int i = 0; i < disabledElements.getLength(); i++)
		{
			Element currentMessageElement = (Element) disabledElements.item(i);
			messageNames.add(currentMessageElement.getAttribute("name"));
		}

		return messageNames;
	}
}
