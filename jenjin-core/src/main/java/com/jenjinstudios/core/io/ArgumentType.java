package com.jenjinstudios.core.io;

/**
 * This class is used to define arguments for MessageTypes.
 * @author Caleb Brinkman
 */
public class ArgumentType
{
	/** The name of the argument. */
	public final String name;
	/** The type of the argument. */
	public final Class type;
	/** Whether to encrypt the argument. */
	public final boolean encrypt;

	/**
	 * Create a new {@code ArgumentType} with the given name and type, and whether the argument should be encrypted
	 * before being sent over socket.  Only Strings and String arrays can be encrypted.
	 * @param name The name of the argument.
	 * @param type The type of the argument.
	 * @param encrypt If type is String or String[], whether the argument should be encrypted.
	 */
	public ArgumentType(String name, Class type, boolean encrypt) {
		this.encrypt = encrypt;
		this.name = name;
		this.type = type;
	}

	@Override
	public String toString() { return name + ", " + type + ", encrypt: " + encrypt; }
}
