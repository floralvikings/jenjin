package com.jenjinstudios.core.io;

/**
 * @author Caleb Brinkman
 */
public class MessageInfo
{
	private final short id;
	private final String name;
	private final ArgumentType[] argumentTypes;

	public MessageInfo(short id, String name, ArgumentType[] argumentTypes) {
		this.id = id;
		this.name = name;
		this.argumentTypes = argumentTypes;
	}

	public short getId() { return id; }

	public String getName() { return name; }

	public ArgumentType[] getArgumentTypes() { return argumentTypes; }
}
