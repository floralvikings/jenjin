package com.jenjinstudios.core.io;

/**
 * Enumerates the available override modes.
 *
 * @author Caleb Brinkman
 */
public enum OverrideMode
{
	/** The mode used to override existing executable messages. */
	OVERRIDE("Override"),
	/** The mode used to disable existing executable messages. */
	DISABLE("Disable"),
	/** The mode used to finalize an executable message. */
	FINAL("Final");
	private final String mode;

	OverrideMode(String override) { mode = override; }

	/**
	 * Get the value of this mode.
	 *
	 * @return The value of this mode.
	 */
	public String getValue() { return mode; }
}
