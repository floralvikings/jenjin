package com.jenjinstudios.world;

import java.util.TreeMap;

/**
 * LocationProperties assigned to a location.
 * @author Caleb Brinkman.
 */
public class LocationProperties
{
	/** The properties stored by this object. */
	private final TreeMap<String, String> properties;

	/** Construct a new LocationProperties with default attributes. */
	public LocationProperties() { this(new TreeMap<String, String>()); }

	/**
	 * Construct a new Property.
	 * @param properties The properties to be stored by this object.
	 */
	public LocationProperties(TreeMap<String, String> properties) { this.properties = new TreeMap<>(properties); }

	/**
	 * Get the property with the given name.
	 * @param name The name of the property.
	 * @return The value of the property.
	 */
	public String getProperty(String name) {
		synchronized (properties)
		{
			return properties.get(name);
		}
	}

	/**
	 * Get the map used to store properties; usage of this method is <b>not</b> recommended for thread-safety purposes.
	 * @return A copy of the map of properties.
	 */
	public TreeMap<String, String> getProperties() { return properties; }
}