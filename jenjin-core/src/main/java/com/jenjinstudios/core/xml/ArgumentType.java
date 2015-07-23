package com.jenjinstudios.core.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * The {@code ArgumentType} class is used to represent a type of argument in a {@code MessageType}.  It contains
 * properties used to indicate the {@code Class} of the argument (represened as a {@code String}) and the name of the
 * argument.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("ALL")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "argument", namespace = "https://www.jenjinstudios.com")
public class ArgumentType
{
	@XmlAttribute(name = "type", required = true)
	private String type;
	@XmlAttribute(name = "name", required = true)
	private String name;

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

    @Override
    public String toString() { return name + ", " + type; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArgumentType)) return false;

        ArgumentType that = (ArgumentType) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
