package com.jenjinstudios.core.xml;

import javax.xml.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Stores the mode and name of an executable message to be overridden.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("ALL")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "executable_override", namespace = "https://www.jenjinstudios.com")
public class ExecutableOverride
{
	@XmlElement(name = "executable", namespace = "https://www.jenjinstudios.com")
	private List<String> executables;
	@XmlAttribute(name = "id", required = true)
	private short id;
	@XmlAttribute(name = "mode", required = true)
	private String mode;

	/**
	 * Get the names of the ExecutableMessages to be used.
	 *
	 * @return The names of the class of the ExecutableMessages to be used.
	 */
	public List<String> getExecutables() {
		if (executables == null)
		{
			executables = new LinkedList<>();
		}
		return executables;
	}

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExecutableOverride)) return false;

        ExecutableOverride override = (ExecutableOverride) o;

        if (id != override.id) return false;
        if ((executables != null) ? !executables.equals(override.executables) : (override.executables != null))
            return false;
        if ((mode != null) ? !mode.equals(override.mode) : (override.mode != null)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (executables != null) ? executables.hashCode() : 0;
        result = 31 * result + id;
        result = 31 * result + ((mode != null) ? mode.hashCode() : 0);
        return result;
    }
}
