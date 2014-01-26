package com.jenjinstudios.world;

import java.util.TreeMap;

/**
 * LocationProperties assigned to a location.
 * @author Caleb Brinkman.
 */
public class LocationProperties
{
	/** The properties stored by this object. */
	public final TreeMap<String, String> properties;

	/**
	 * Construct a new LocationProperties with default attributes.
	 */
	public LocationProperties()
	{
		this(new TreeMap<String, String>());
	}

	/**
	 * Construct a new Property.
	 * @param properties The properties to be stored by this object.
	 */
	public LocationProperties(TreeMap<String, String> properties) {
		this.properties = new TreeMap<>(properties);
	}

	/**
	 * Get the property with the given name.
	 * @param name The name of the property.
	 * @return The value of the property.
	 */
	public String getProperty(String name) {
		return properties.get(name);
	}
}