package com.jenjinstudios.io;

import java.util.*;

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
	/** The ExecutableMessage classes used by this MessageType. */
	private final List<Class<? extends ExecutableMessage>> executableMessageClasses;
	/** The argument types for this message, sorted by name. */
	private final Map<String, ArgumentType> argumentTypeTreeMap;

	/**
	 * Construct a new MessageType with the given information.
	 * @param id The ID of the message type.
	 * @param name The name of the message type.
	 * @param argumentTypes The argumentTypes of the message type.
	 * @param classes The ExecutableMessage classes that can be invoked by this message type.
	 */
	public MessageType(short id, String name, ArgumentType[] argumentTypes, List<Class<? extends ExecutableMessage>> classes) {
		executableMessageClasses = new LinkedList<>(classes);
		this.id = id;
		this.name = name;
		this.argumentTypes = argumentTypes;
		argumentTypeTreeMap = new TreeMap<>();

		for (ArgumentType argumentType : argumentTypes) { argumentTypeTreeMap.put(argumentType.name, argumentType); }
	}

	public MessageType(short id, String messageName, ArgumentType[] argumentTypes) {
		this(id, messageName, argumentTypes, new LinkedList<Class<? extends ExecutableMessage>>());
	}

	/**
	 * Get the argument type with the given name.
	 * @param name The name of the argument.
	 * @return The ArgumentType with the given name.
	 */
	public ArgumentType getArgumentType(String name) { return argumentTypeTreeMap.get(name); }

	/**
	 * Get the executable messages associated with this message type.
	 * @return The executable messages associated with this message type.
	 */
	public List<Class<? extends ExecutableMessage>> getExecutableMessageClasses() { return executableMessageClasses; }
}
