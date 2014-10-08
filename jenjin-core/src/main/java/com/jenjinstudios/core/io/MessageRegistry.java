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
 * The {@code MessageRegistry} class is central to the Jenjin's dynamic messaging system.  An instance of the registry
 * by calling {@code getInstance}; this method returns the same immutable instance each time it is called.  This
 * instance can be used to retrieve "empty" {@code Message} objects, which are prepared to accept arguments and be
 * written to a {@code MessageOutputStream}.
 * <p>
 * The first time the {@code getInstance} method is called, this class recursively searches for existing Messages.xml
 * files in the working directory and the classpath, registering any files found.
 * <p>
 * The addition of a duplicate file is non-deterministic; the behavior is undefined if two message types with the same
 * id are added to the registry.
 *
 * @author Caleb Brinkman
 */
public class MessageRegistry
{
	private static final Logger LOGGER = Logger.getLogger(MessageRegistry.class.getName());
	private static MessageRegistry messageRegistry;
	private final Map<Short, MessageType> messageTypesByID = new TreeMap<>();
	private final Map<String, MessageType> messageTypesByName = new TreeMap<>();

	private MessageRegistry() {
		registerXmlMessages();
	}

	/**
	 * Get an immutable instance of this class.  This method only creates a new instance once; each time it is called
	 * thereafter returns the same instance that has already been created.
	 *
	 * @return An immutable, static {@code MessageRegistry}.
	 */
	public static MessageRegistry getInstance() {
		if (messageRegistry == null)
		{
			messageRegistry = new MessageRegistry();
		}
		return messageRegistry;
	}

	public MessageType getMessageType(String name) {
		synchronized (messageTypesByName)
		{
			return messageTypesByName.get(name);
		}
	}

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

	private void registerXmlMessages() {
		LinkedList<InputStream> streamsToRead = new LinkedList<>();
		streamsToRead.addAll(MessageFileFinder.findMessageJarStreams());
		streamsToRead.addAll(MessageFileFinder.findMessageFileStreams());
		readXmlStreams(streamsToRead);
	}

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
