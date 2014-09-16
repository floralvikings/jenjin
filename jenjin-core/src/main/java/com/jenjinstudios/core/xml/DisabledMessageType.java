package com.jenjinstudios.core.xml;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for disabled_messageType complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="disabled_messageType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "disabled_messageType", propOrder = {
	  "value"
})
public class DisabledMessageType
{

	@XmlValue
	protected String value;
	@XmlAttribute(name = "name")
	protected String name;

	/**
	 * Gets the value of the value property.
	 * @return possible object is {@link String }
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value of the value property.
	 * @param value allowed object is {@link String }
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets the value of the name property.
	 * @return possible object is {@link String }
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of the name property.
	 * @param value allowed object is {@link String }
	 */
	public void setName(String value) {
		this.name = value;
	}

}
