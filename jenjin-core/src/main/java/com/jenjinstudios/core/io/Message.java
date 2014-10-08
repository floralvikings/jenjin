package com.jenjinstudios.core.io;

import com.jenjinstudios.core.util.TypeMapper;
import com.jenjinstudios.core.xml.ArgumentType;
import com.jenjinstudios.core.xml.MessageType;

import java.util.Map;
import java.util.TreeMap;

/**
 * The {@code Message} class is used in sending data to and receiving data from {@code Connection} objects.  Each
 * Message has a unique {@code name}, a unique {@code id}, and a {@code Map} of arguments which are accessed with the
 * {@code getArgument} and {@code setObject} methods. </p> Message arguments may consist of any primitive type, as well
 * as {@code String} objects, and {@code String} and {@code byte} arrays.
 * @author Caleb Brinkman
 */
public class Message
{
	public final String name;
	private final MessageType messageType;
	private final Map<String, Object> argumentsByName;
	private final short id;

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

	Message(MessageType messageType) {
		this.messageType = messageType;
		this.name = messageType.getName();
		id = messageType.getId();
		argumentsByName = new TreeMap<>();
	}

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

	public Object getArgument(String argumentName) {
		return argumentsByName.get(argumentName);
	}

	public short getID() {
		return id;
	}

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

	boolean isInvalid() {
		return argumentsByName.size() != messageType.getArguments().size();
	}

	@Override
	public String toString() { return "Message " + id + " " + name; }
}
