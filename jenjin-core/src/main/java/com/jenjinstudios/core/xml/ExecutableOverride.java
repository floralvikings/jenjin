package com.jenjinstudios.core.xml;

import javax.xml.bind.annotation.*;

/**
 * Stores the mode and name of an executable message to be overridden.
 *
 * @author Caleb Brinkman
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "executable_override", namespace = "https://www.jenjinstudios.com")
public class ExecutableOverride
{
	@XmlValue
	private String executable;
	@XmlAttribute(name = "id", required = true)
	private short id;
	@XmlAttribute(name = "mode", required = true)
	private String mode;

	/**
	 * Get the name of the ExecutableMessage to be used.
	 *
	 * @return The name of the class of the ExecutableMessage to be used.
	 */
	public String getExecutable() { return executable; }

	/**
	 * Set the executable message to override the existing one.
	 *
	 * @param executable The name of the class of the new executable message.
	 */
	public void setExecutable(String executable) { this.executable = executable; }

	/**
	 * Get the override mode.
	 *
	 * @return The override mode.
	 */
	public String getMode() { return mode; }

	/**
	 * Set the override mode.
	 *
	 * @param mode The new override mode.
	 */
	public void setMode(String mode) { this.mode = mode; }

	/**
	 * Get the ID of the message being overridden.
	 *
	 * @return The ID of the message being overridden.
	 */
	public short getId() { return id; }

	/**
	 * Set the ID of the message being overridden.
	 *
	 * @param id The ID of the message being overridden.
	 */
	public void setId(short id) { this.id = id; }
}
