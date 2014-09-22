package com.jenjinstudios.core.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for messageTypeCollection complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="messageTypeCollection">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="messages" type="{}messageType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="disabled_messages" type="{}disabled_messageType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
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
	 * Gets the value of the messages property.
	 * <p>
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you
	 * make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the messages property.
	 * <p>
	 * <p>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getMessages().add(newItem);
	 * </pre>
	 * <p>
	 * <p>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link MessageType }
	 */
	public List<MessageType> getMessages() {
		if (messages == null)
		{
			messages = new ArrayList<>();
		}
		return this.messages;
	}

	/**
	 * Gets the value of the disabledMessages property.
	 * <p>
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you
	 * make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the disabledMessages property.
	 * <p>
	 * <p>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getDisabledMessages().add(newItem);
	 * </pre>
	 * <p>
	 * <p>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link DisabledMessageType }
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
