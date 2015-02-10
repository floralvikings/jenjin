package com.jenjinstudios.core.io;

import com.jenjinstudios.core.xml.ExecutableOverride;
import com.jenjinstudios.core.xml.MessageGroup;
import com.jenjinstudios.core.xml.MessageType;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private static final MessageRegistry messageRegistry = new MessageRegistry();
    private final Map<Short, MessageType> messageTypesByID = new TreeMap<>();
	private final Map<String, MessageType> messageTypesByName = new TreeMap<>();
    private final Collection<Short> finalOverrides = new LinkedList<>();

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
        return messageRegistry;
    }

	/**
	 * Get the {@code MessageType} with the given unique name.
	 *
	 * @param name The name of the {@code MessageType}.
	 *
	 * @return The {@code MessageType} with the given unique name.
	 */
	public MessageType getMessageType(String name) {
		synchronized (messageTypesByName)
		{
			return messageTypesByName.get(name);
		}
	}

	/**
	 * Get the {@code MessageType} with the given unique id.
	 *
	 * @param id The id of the {@code MessageType}.
	 *
	 * @return The {@code MessageType} with the given unique id.
	 */
	public MessageType getMessageType(short id) {
		synchronized (messageTypesByID)
		{
			return messageTypesByID.get(id);
		}
	}

	/**
	 * Create a new "empty" {@code Message} that corresponds to the MessageType with the given name.
	 *
	 * @param name The name of the {@code MessageType} of which the return {@code Message} will be.
	 *
	 * @return A new "empty" {@code Message} of the type specified by {@code name}.
	 */
	public Message createMessage(String name) {
        MessageType messageType = getMessageType(name);
        if (messageType == null)
		{
			LOGGER.log(Level.INFO, "Requested non-existant message {0}, refreshing XML registry", name);
			// Try again after re-registering XML files
			registerXmlMessages();
			messageType = getMessageType(name);
		}
        Message message = null;
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
		Collection<MessageGroup> foundMessages = MessageFileFinder.findXmlRegistries();

		for (MessageGroup currentMessageCollection : foundMessages)
		{
			currentMessageCollection.getMessages().forEach(this::registerMessageType);
			currentMessageCollection.getOverrides().forEach(this::registerOverride);
		}
	}

	private void registerOverride(ExecutableOverride override) {
        if (finalOverrides.contains(override.getId()))
        {
			throw new IllegalArgumentException("Cannot overwrite final message executable: " + override.getId());
		}
        List<String> executables;
        synchronized (messageTypesByID)
        {
            MessageType messageType = messageTypesByID.get(override.getId());
            executables = messageType.getExecutables();
        }
        switch (override.getMode())
        {
			case "Override":
                executables.clear();
                executables.addAll(override.getExecutables());
                break;
			case "Disable":
				executables.removeAll(override.getExecutables());
				break;
			case "Final":
				finalOverrides.add(override.getId());
                executables.clear();
                executables.addAll(override.getExecutables());
                break;
            default:
                throw new IllegalArgumentException("Invalid Override Mode: " + override.getMode());
        }
	}

	private void registerMessageType(MessageType messageType) {
		if (messageType == null)
		{
			LOGGER.log(Level.INFO, "Attempted to register null reference type.");
		} else if (messageTypesByID.containsKey(messageType.getId()))
		{
			LOGGER.log(Level.WARNING, "Unable to register message type: " + messageType.getName() + ". ID already " +
				  "registered.");
		} else if (messageTypesByName.containsKey(messageType.getName()))
		{
			LOGGER.log(Level.WARNING, "Unable to register message type: " + messageType.getId() + ". Name already " +
				  "registered.");
		} else
		{
			messageTypesByID.put(messageType.getId(), messageType);
			messageTypesByName.put(messageType.getName(), messageType);
		}
	}

}
