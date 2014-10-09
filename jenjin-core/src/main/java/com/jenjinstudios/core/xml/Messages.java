package com.jenjinstudios.core.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a collection of {@code MessageType} and {@code DisabledMessageType} objects.
 *
 * @author Caleb Brinkman
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "messages", namespace = "https://www.jenjinstudios.com")
public class Messages
{
	@XmlElement(name = "message", namespace = "https://www.jenjinstudios.com")
	private List<MessageType> messages;
	@XmlElement(name = "disabled_message", namespace = "https://www.jenjinstudios.com")
	private List<DisabledMessageType> disabledMessages;

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

	/**
	 * Get the {@code DisabledMessageType} objects contained in this collection.
	 *
	 * @return The {@code DisabledMessageType} objects contained in this collection.
	 */
	public List<DisabledMessageType> getDisabledMessages() {
		if (disabledMessages == null)
		{
			disabledMessages = new ArrayList<>();
		}
		return this.disabledMessages;
	}

	public void addAll(Messages messages) {
		if (messages != null)
		{
			getMessages().addAll(messages.getMessages());
			getDisabledMessages().addAll(messages.getDisabledMessages());
		}
	}

}
