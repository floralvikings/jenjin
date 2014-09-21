package com.jenjinstudios.core.io;

import com.jenjinstudios.core.xml.ArgumentType;
import com.jenjinstudios.core.xml.DisabledMessageType;
import com.jenjinstudios.core.xml.MessageType;
import com.jenjinstudios.core.xml.Messages;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Handles the registration of message classes and the information on how to reconstruct them from raw data.
 * @author Caleb Brinkman
 */
public class MessageRegistry
{
	/** The logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(MessageRegistry.class.getName());
	private static MessageRegistry messageRegistry;
	/** A map that stores messages types sorted by ID. */
	private final Map<Short, MessageType> messageTypesByID = new TreeMap<>();
	/** A map that stores message types sorted by name. */
	private final Map<String, MessageType> messageTypesByName = new TreeMap<>();

	/**
	 * Construct a new MessageRegistry.
	 */
	private MessageRegistry() {
		registerXmlMessages();
	}

	public static MessageRegistry getInstance() {
		if (messageRegistry == null)
		{
			messageRegistry = new MessageRegistry();
		}
		return messageRegistry;
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

	public Message createMessage(String name) {
		Message message = null;
		MessageType messageType = getMessageType(name);
		if (messageType == null)
		{
			LOGGER.log(Level.INFO, "Requested non-existant message {0}, refreshing XML registry", name);
			// Try again after re-registering XML files
			registerXmlMessages();
			messageType = getMessageType(name);
		}
		if (messageType != null)
		{
			message = new Message(messageType);
		} else
		{
			LOGGER.log(Level.WARNING, "Couldn't find {0} even after refreshing XML registry.", name);
		}
		return message;
	}

	/** Register all messages found in registry files.  Also checks the JAR file. */
	private void registerXmlMessages() {
		LinkedList<InputStream> streamsToRead = new LinkedList<>();
		streamsToRead.addAll(MessageFileFinder.findMessageJarStreams());
		streamsToRead.addAll(MessageFileFinder.findMessageFileStreams());
		readXmlStreams(streamsToRead);
	}

	/**
	 * Parse the XML streams and register the discovered MessageTypes.
	 * @param streamsToRead The streams containing the XML data to be parsed.
	 */
	private void readXmlStreams(Iterable<InputStream> streamsToRead) {
		Messages messages = new Messages();
		for (InputStream inputStream : streamsToRead)
		{
			try
			{
				JAXBContext jaxbContext = JAXBContext.newInstance(Messages.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				Messages collection = (Messages) jaxbUnmarshaller.unmarshal(inputStream);
				messages.addAll(collection);
				addAllMessages(collection.getMessages());
			} catch (Exception ex)
			{
				LOGGER.log(Level.INFO, "Unable to parse XML file", ex);
			}
		}
		messages.getDisabledMessages().forEach(this::disableExecutableMessage);
	}

	/**
	 * Register all message types within the given list.
	 * @param messageTypes The list of message types to add.
	 */
	private void addAllMessages(List<MessageType> messageTypes) {
		Stream<MessageType> stream = messageTypes.stream();
		Stream<MessageType> filter = stream.filter(messageType -> messageType != null &&
			  !messageTypesByID.containsKey(messageType.getId()) && !messageTypesByName.containsKey(messageType
			  .getName()));
		filter.forEach(messageType -> {
			messageTypesByID.put(messageType.getId(), messageType);
			messageTypesByName.put(messageType.getName(), messageType);
		});
	}

	/**
	 * Disable the ExecutableMessage invoked by the message with the given name.
	 */
	void disableExecutableMessage(DisabledMessageType disabledMessageType) {
		String messageName = disabledMessageType.getName();
		LOGGER.log(Level.INFO, "Disabling message: {0}", messageName);
		MessageType type;
		synchronized (messageTypesByName)
		{
			type = messageTypesByName.get(messageName);
		}
		short id = type.getId();
		List<ArgumentType> argumentTypes = type.getArguments();
		MessageType newMessageType = new MessageType();
		newMessageType.setId(id);
		newMessageType.setName(messageName);
		newMessageType.getArguments().addAll(argumentTypes);
		messageTypesByName.put(messageName, newMessageType);
		messageTypesByID.put(id, newMessageType);

	}
}
