package com.jenjinstudios.core.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents a type of {@code Message} that should be disabled, and not invoked when received.
 *
 * @author Caleb Brinkman
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "disabled_message", namespace = "https://www.jenjinstudios.com")
public class DisabledMessageType
{

	@XmlAttribute(name = "name")
	private String name;

	/**
	 * Get the name of the {@code Message} to be disabled.
	 *
	 * @return The name of the {@code Message} to be disabled.
	 */
	public String getName() {
		return name;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DisabledMessageType)) return false;

        DisabledMessageType that = (DisabledMessageType) o;

        if ((name != null) ? !name.equals(that.name) : (that.name != null)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (name != null) ? name.hashCode() : 0;
    }
}
