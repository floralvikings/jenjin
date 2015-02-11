package com.jenjinstudios.core.xml;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Contains properties representing the metadata of a {@code Message} object, used to constuct a {@code Message} from a
 * stream.
 *
 * @author Caleb Brinkman
 */
@SuppressWarnings("ALL")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "message", namespace = "https://www.jenjinstudios.com", propOrder = {
	  "arguments",
	  "executables"
})
public class MessageType
{
	@XmlElement(name = "argument", namespace = "https://www.jenjinstudios.com")
	private List<ArgumentType> arguments;
	@XmlElement(name = "executable", namespace = "https://www.jenjinstudios.com")
	private List<String> executables;
	@XmlAttribute(name = "name", required = true)
	private String name;
	@XmlAttribute(name = "id", required = true)
	private short id;

	/**
	 * Get the {@code ArgumentType} objects containing the metadata of the arguments that the {@code Message} should
	 * contain, in the order in which they should be read and written to a stream.
	 *
	 * @return The {@code ArgumentType} objects containing the metadata of the arguments that the {@code Message}
	 * should
	 * contain, in the order in which they should be read and written to a stream.
	 */
	public List<ArgumentType> getArguments() {
		if (arguments == null)
		{
			arguments = new ArrayList<>();
		}
		return this.arguments;
	}

	/**
	 * Get the name of the class of the {@code ExecutableMessage} to be invoked when this message is received.
	 *
	 * @return The name of the class of the {@code ExecutableMessage} to be invoked when this message is received.
	 */
	public List<String> getExecutables() {
		if (executables == null)
		{
			executables = new LinkedList<>();
		}
		return executables;
	}

	/**
	 * Get the unique name of the {@code Message}.
	 *
	 * @return The unique name of the {@code Message}.
	 */
	public String getName() { return name; }

	/**
	 * Set the name of the {@code Message}.
	 *
	 * @param value The new name.
	 */
	public void setName(String value) { this.name = value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageType)) return false;

        MessageType that = (MessageType) o;

        if (id != that.id) return false;
        if (arguments != null ? !arguments.equals(that.arguments) : that.arguments != null) return false;
        if (executables != null ? !executables.equals(that.executables) : that.executables != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = arguments != null ? arguments.hashCode() : 0;
        result = 31 * result + (executables != null ? executables.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (int) id;
        return result;
    }

    /**
     * Get the unique ID of the {@code Message}.

     *
	 * @return The unique ID of the {@code Message}.
	 */
	public short getId() { return id; }

	/**
	 * Set the ID of the {@code Message}.
	 *
	 * @param value The ID of the {@code Message}.
	 */
	public void setId(short value) { this.id = value; }

}
