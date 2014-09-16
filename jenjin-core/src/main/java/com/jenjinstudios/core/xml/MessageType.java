package com.jenjinstudios.core.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for messageType complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="messageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="arguments" type="{}argumentType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="executable" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}byte" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "messageType", propOrder = {
	  "arguments",
	  "executable"
})
public class MessageType
{

	protected List<ArgumentType> arguments;
	protected String executable;
	@XmlAttribute(name = "name", required = true)
	protected String name;
	@XmlAttribute(name = "id", required = true)
	protected short id;

	/**
	 * Gets the value of the arguments property.
	 * <p>
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you
	 * make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the arguments property.
	 * <p>
	 * <p>
	 * For example, to add a new item, do as follows:
	 * <pre>
	 *    getArguments().add(newItem);
	 * </pre>
	 * <p>
	 * <p>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link ArgumentType }
	 */
	public List<ArgumentType> getArguments() {
		if (arguments == null)
		{
			arguments = new ArrayList<ArgumentType>();
		}
		return this.arguments;
	}

	/**
	 * Gets the value of the executable property.
	 * @return possible object is {@link String }
	 */
	public String getExecutable() { return executable; }

	/**
	 * Sets the value of the executable property.
	 * @param value allowed object is {@link String }
	 */
	public void setExecutable(String value) { this.executable = value; }

	/**
	 * Gets the value of the name property.
	 * @return possible object is {@link String }
	 */
	public String getName() { return name; }

	/**
	 * Sets the value of the name property.
	 * @param value allowed object is {@link String }
	 */
	public void setName(String value) { this.name = value; }

	/**
	 * Gets the value of the id property.
	 */
	public short getId() { return id; }

	/**
	 * Sets the value of the id property.
	 */
	public void setId(short value) { this.id = value; }

}
