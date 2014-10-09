package com.jenjinstudios.core.xml;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains properties representing the metadata of a {@code Message} object, used to constuct a {@code Message} from a
 * stream.
 *
 * @author Caleb Brinkman
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "message", namespace = "https://www.jenjinstudios.com", propOrder = {
	  "arguments",
	  "executable"
})
public class MessageType
{
	@XmlElement(name = "argument", namespace = "https://www.jenjinstudios.com")
	private List<ArgumentType> arguments;
	@XmlElement(name = "executable", namespace = "https://www.jenjinstudios.com")
	private String executable;
	@XmlAttribute(name = "name", required = true)
	private String name;
	@XmlAttribute(name = "id", required = true)
	private short id;

	public List<ArgumentType> getArguments() {
		if (arguments == null)
		{
			arguments = new ArrayList<>();
		}
		return this.arguments;
	}

	public String getExecutable() { return executable; }

	public String getName() { return name; }

	public void setName(String value) { this.name = value; }

	public short getId() { return id; }

	public void setId(short value) { this.id = value; }

}
