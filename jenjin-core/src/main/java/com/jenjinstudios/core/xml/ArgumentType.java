package com.jenjinstudios.core.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


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
@XmlType(name = "argument", namespace = "https://www.jenjinstudios.com")
public class ArgumentType
{
	@XmlAttribute(name = "type", required = true)
	private String type;
	@XmlAttribute(name = "name", required = true)
	private String name;
	@XmlAttribute(name = "encrypt")
	private boolean encrypt = false;

	/**
	 * Gets the value of the type property.
	 * @return possible object is {@link String }
	 */
	public String getType() { return type; }

	/**
	 * Gets the value of the name property.
	 * @return possible object is {@link String }
	 */
	public String getName() { return name; }

	/**
	 * Gets the value of the encrypt property.
	 * @return possible object is {@link Boolean }
	 */
	public Boolean isEncrypt() { return encrypt; }

	@Override
	public String toString() { return name + ", " + type + ", encrypt: " + encrypt; }
}
