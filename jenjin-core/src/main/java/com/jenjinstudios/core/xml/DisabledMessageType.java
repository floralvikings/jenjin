package com.jenjinstudios.core.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "disabled_message", namespace = "https://www.jenjinstudios.com")
public class DisabledMessageType
{

	@XmlAttribute(name = "name")
	private String name;

	public String getName() {
		return name;
	}

}
