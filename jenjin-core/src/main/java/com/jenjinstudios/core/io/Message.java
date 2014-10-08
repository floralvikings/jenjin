package com.jenjinstudios.core.io;

import com.jenjinstudios.core.util.TypeMapper;
import com.jenjinstudios.core.xml.ArgumentType;
import com.jenjinstudios.core.xml.MessageType;

import java.util.Map;
import java.util.TreeMap;

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

	@Override
	public String toString() { return "Message " + id + " " + name; }

	boolean isInvalid() {
		return argumentsByName.size() != messageType.getArguments().size();
	}
}
