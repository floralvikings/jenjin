package com.jenjinstudios.core.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a collection of {@code MessageType} and {@code DisabledMessageType} objects.
 *
 * @author Caleb Brinkman
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "messages", namespace = "https://www.jenjinstudios.com")
public class MessageGroup
{
	@XmlElement(name = "message", namespace = "https://www.jenjinstudios.com")
	private List<MessageType> messages;
	@XmlElement(name = "executable_override", namespace = "https://www.jenjinstudios.com")
	private List<ExecutableOverride> overrides;

	/**
	 * Get the {@code MessageType} objects contained in this collection.
	 *
	 * @return The {@code MessageType} objects contained in this collection.
	 */
	public List<MessageType> getMessages() {
		if (messages == null)
		{
			messages = new ArrayList<>();
		}
		return this.messages;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageGroup)) return false;

        MessageGroup messageGroup = (MessageGroup) o;

        if (!messages.equals(messageGroup.messages)) return false;
        return overrides.equals(messageGroup.overrides);

    }

    @Override
    public int hashCode() {
        int result = messages.hashCode();
        result = 31 * result + overrides.hashCode();
        return result;
    }

    /**
     * Get the executable message override groups.
	 *
	 * @return The executable message override groups.
	 */
	public List<ExecutableOverride> getOverrides() {
		if (overrides == null)
		{
			overrides = new LinkedList<>();

        }
		return overrides;
	}

}
