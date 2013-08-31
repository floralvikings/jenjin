package com.jenjinstudios.world;

/**
 * Represents a location in the location grid of a Zone.
 *
 * @author Caleb Brinkman
 */
public class Location
{
	/** The size, int units, of each location. */
	public static final int SIZE = 10;
	/** The x coordinate of the location in it's zone's grid. */
	public final int X_COORDINATE;
	/** The z coordinate of the location in it's zone's grid. */
	public final int Z_COORDINATE;
	/** The property of this location. */
	private Property property;

	/**
	 * Construct a new location at the given position in a zone grid.
	 *
	 * @param x The x coordinate of the zone grid.
	 * @param z The z coordinate of the zone grid.
	 */
	public Location(int x, int z)
	{
		X_COORDINATE = x;
		Z_COORDINATE = z;
		property = Property.OPEN;
	}

	/**
	 * Get the property of this location.
	 *
	 * @return The property of this location.
	 */
	public Property getProperty()
	{
		return property;
	}

	/**
	 * Set the property of this location.
	 *
	 * @param property The new property.
	 */
	public void setProperty(Property property)
	{
		this.property = property;
	}

	/** Specifies a property of a location. */
	public enum Property
	{
		/** Specifies that the location is open, and can be entered by objects. */
		OPEN,
		/** Specifies that the location is closed, and cannot be entered by objects. */
		CLOSED
	}
}
