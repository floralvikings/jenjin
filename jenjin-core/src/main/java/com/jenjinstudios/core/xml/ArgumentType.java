package com.jenjinstudios.core.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * The {@code ArgumentType} class is used to represent a type of argument in a {@code MessageType}.  It contains
 * properties used to indicate the {@code Class} of the argument (represened as a {@code String}), the name of the
 * argument, and whether or not the value of the argument should be encrypted.
 *
 * @author Caleb Brinkman
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "argument", namespace = "https://www.jenjinstudios.com")
public class ArgumentType
{
	@XmlAttribute(name = "type", required = true)
	private String type;
	@XmlAttribute(name = "name", required = true)
	private String name;
	@XmlAttribute(name = "encrypt")
	private boolean encrypt = false;

	/**
	 * Get the type of the argument.
	 *
	 * @return A String representation of the type of this object.  This can be converted to a Class object by the
	 * {@code TypeMapper} class.
	 */
	public String getType() { return type; }

	/**
	 * Get the name of the argument.
	 *
	 * @return The name of the argument.
	 */
	public String getName() { return name; }

	public Boolean isEncrypt() { return encrypt; }

	@Override
	public String toString() { return name + ", " + type + ", encrypt: " + encrypt; }
}
