package com.jenjinstudios.io;

import java.util.Arrays;
import java.util.TreeMap;

/**
 * This class is used to define the ID, name, and Argument Types for each type of message contained in the XML files.
 * @author Caleb Brinkman
 */
public class MessageType
{
	/** The ID of this message type. */
	public final short id;
	/** The name of this message type. */
	public final String name;
	/** The argumentTypes for this message type. */
	public final ArgumentType[] argumentTypes;
	/** The class of the client executable message associated with this type. */
	public final Class<? extends ExecutableMessage> clientExecutableMessageClass;
	/** The class of the server executable message associated with this type. */
	public final Class<? extends ExecutableMessage> serverExecutableMessageClass;
	/** The argument types for this message, sorted by name. */
	private final TreeMap<String, ArgumentType> argumentTypeTreeMap;

	/**
	 * Construct a new MessageType with the given information.
	 * @param id The ID of the message type.
	 * @param name The name of the message type.
	 * @param argumentTypes The argumentTypes of the message type.
	 * @param clientExec The class of the ExecutableMessage to be invoked by clients.
	 * @param serverExec The class of the ExecutableMessage to be invoked by servers.
	 */
	public MessageType(short id, String name, ArgumentType[] argumentTypes,
					   Class<? extends ExecutableMessage> clientExec, Class<? extends ExecutableMessage> serverExec)
	{
		this.id = id;
		this.name = name;
		this.argumentTypes = argumentTypes;
		this.clientExecutableMessageClass = clientExec;
		this.serverExecutableMessageClass = serverExec;
		argumentTypeTreeMap = new TreeMap<>();

		for (ArgumentType argumentType : argumentTypes) { argumentTypeTreeMap.put(argumentType.name, argumentType); }
	}

	/**
	 * Get the argument type with the given name.
	 * @param name The name of the argument.
	 * @return The ArgumentType with the given name.
	 */
	public ArgumentType getArgumentType(String name) { return argumentTypeTreeMap.get(name); }

	@Override
	public String toString() { return name + " " + id + ": " + Arrays.toString(argumentTypes) + " " + serverExecutableMessageClass; }
}
