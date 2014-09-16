package com.jenjinstudios.core.io;

import com.jenjinstudios.core.util.TypeMapper;
import com.jenjinstudios.core.xml.ArgumentType;
import com.jenjinstudios.core.xml.MessageType;

import java.util.Map;
import java.util.TreeMap;

/**
 * The Message class is used for fast-and-loose serialization for efficient transport of data over socket.
 * @author Caleb Brinkman
 */
public class Message
{
	/** The name of this message. */
	public final String name;
	/** The message type of this message. */
	private final MessageType messageType;
	/** The arguments for this message by name. */
	private final Map<String, Object> argumentsByName;
	/** The ID of this message. */
	private final short id;

	/**
	 * Construct a new Message with the given ID and arguments. This is intended <b>only</b> for use in
	 * MessageInputStream. You should <b>not</b> use this constructor in your code.  If, for some reason, you decide to
	 * (and you really, really shouldn't) the arguments you pass <b>must</b> fill every available argument and be
	 * passed
	 * in the order in which they appear in the XML file.
	 * @param messageRegistry The Connection creating this message.
	 * @param id The ID of the message type for this message.
	 * @param args The arguments of this message.  This <b>must</b> fill every available argument for the message.
	 */
	Message(MessageRegistry messageRegistry, short id, Object... args) {
		this.id = id;
		messageType = messageRegistry.getMessageType(id);
		name = messageType.getName();
		argumentsByName = new TreeMap<>();
		for (int i = 0; i < messageType.getArguments().size(); i++)
		{
			try
			{
				setArgument(messageType.getArguments().get(i).getName(), args[i]);
			} catch (ArrayIndexOutOfBoundsException ex)
			{
				throw new IllegalStateException(
					  "Not enough arguments provided for Message", ex);
			}

		}
	}

	/**
	 * Construct a new Message using the MessageType specified by the given name; every argument in this message
	 * must be
	 * set using the {@code setArgument} method before it can be sent properly over socket.
	 */
	Message(MessageType messageType) {
		this.messageType = messageType;
		this.name = messageType.getName();
		id = messageType.getId();
		argumentsByName = new TreeMap<>();
	}

	/**
	 * Set the argument of the given name to the given value.
	 * @param argumentName The name of the argument.  If this is not a valid name, an {@code IllegalArgumentException}
	 * will be thrown.
	 * @param argument The value to set to the argument.  If the type of this object is incorrect, an {@code
	 * IllegalArgumentException} will be thrown.
	 */
	public void setArgument(String argumentName, Object argument) {
		ArgumentType argType = null;
		for (ArgumentType a : messageType.getArguments())
		{
			if (argumentName.equals(a.getName()))
			{
				argType = a;
			}
		}
		if (argType == null)
			throw new IllegalArgumentException("Invalid argument name for Message: " + argumentName +
				  " (Message type: " + messageType.getName() + ")");
		Class c = TypeMapper.getTypeForName(argType.getType());
		if (!c.isInstance(argument))
			throw new IllegalArgumentException("Invalid argument type for Message: " + argument +
				  " (Expected " + argType.getType() + ", got " + argument.getClass() + ")");
		argumentsByName.put(argumentName, argument);
	}

	/**
	 * Get the argument with the given name.
	 * @param argumentName The name of the argument.
	 * @return The argument with the specified name.
	 */
	public Object getArgument(String argumentName) {
		return argumentsByName.get(argumentName);
	}

	/**
	 * Get the ID of the MessageType created by this message.
	 * @return The ID of the MessageType of this message.
	 */
	public short getID() {
		return id;
	}

	/**
	 * Get the arguments of this message, in the order in which they should be sent over socket, assuming this message
	 * has been validly filled.
	 * @return An array of objects specifying the arguments of the message, in the order in which they should be sent
	 * over socket.
	 */
	public final Object[] getArgs() {
		if (isInvalid())
			throw new IllegalStateException("Attempting to retrieve arguments while message is invalid. (Not all " +
				  "arguments have been set.)");
		Object[] argsArray = new Object[messageType.getArguments().size()];
		for (int i = 0; i < messageType.getArguments().size(); i++)
		{
			argsArray[i] = argumentsByName.get(messageType.getArguments().get(i).getName());
		}
		return argsArray;
	}

	@Override
	public String toString() { return "Message " + id + " " + name; }

	/**
	 * Determine whether this message is valid, which is to say that all arguments have been correctly set.
	 * @return true if all arguments have been set, and correctly.
	 */
	boolean isInvalid() {
		return argumentsByName.size() != messageType.getArguments().size();
	}
}
