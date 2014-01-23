package com.jenjinstudios.world;

/**
 * LocationProperties assigned to a location.
 * @author Caleb Brinkman.
 */
public class LocationProperties
{
	/** The default property for "walkable". */
	public static final boolean DEFAULT_WALKABLE = true;
	/** Whether or not the location with this property is "walkable". */
	public final boolean isWalkable;

	/**
	 * Construct a new LocationProperties with default attributes.
	 */
	public LocationProperties()
	{
		this(DEFAULT_WALKABLE);
	}

	/**
	 * Construct a new Property.
	 * @param walkable Whether the property includes the "walkable" attribute.
	 */
	public LocationProperties(boolean walkable) {
		isWalkable = walkable;
	}

}