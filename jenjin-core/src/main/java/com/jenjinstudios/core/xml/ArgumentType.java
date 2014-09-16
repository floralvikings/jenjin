package com.jenjinstudios.core.xml;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for argumentType complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="argumentType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="byte[]"/>
 *             &lt;enumeration value="String[]"/>
 *             &lt;enumeration value="boolean"/>
 *             &lt;enumeration value="byte"/>
 *             &lt;enumeration value="short"/>
 *             &lt;enumeration value="int"/>
 *             &lt;enumeration value="long"/>
 *             &lt;enumeration value="float"/>
 *             &lt;enumeration value="double"/>
 *             &lt;enumeration value="String"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="encrypt" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "argumentType", propOrder = {
	  "value"
})
public class ArgumentType
{

	@XmlValue
	protected String value;
	@XmlAttribute(name = "type", required = true)
	protected String type;
	@XmlAttribute(name = "name", required = true)
	protected String name;
	@XmlAttribute(name = "encrypt")
	protected Boolean encrypt;

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
	 * Gets the value of the type property.
	 * @return possible object is {@link String }
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the value of the type property.
	 * @param value allowed object is {@link String }
	 */
	public void setType(String value) {
		this.type = value;
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

	/**
	 * Gets the value of the encrypt property.
	 * @return possible object is {@link Boolean }
	 */
	public Boolean isEncrypt() {
		return encrypt;
	}

	/**
	 * Sets the value of the encrypt property.
	 * @param value allowed object is {@link Boolean }
	 */
	public void setEncrypt(Boolean value) {
		this.encrypt = value;
	}

	@Override
	public String toString() { return name + ", " + type + ", encrypt: " + encrypt; }
}
