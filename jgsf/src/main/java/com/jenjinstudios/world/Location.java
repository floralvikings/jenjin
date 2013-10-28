package com.jenjinstudios.world;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Represents a location in the world's location grid.
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
	/** The objects residing in this location. */
	private final HashSet<WorldObject> objects;
	/** The property of this location. */
	private LocationProperty property;

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
		property = LocationProperty.OPEN;
		objects = new HashSet<>();
	}

	/**
	 * Get the property of this location.
	 *
	 * @return The property of this location.
	 */
	public LocationProperty getProperty()
	{
		return property;
	}

	/**
	 * Set the property of this location.
	 *
	 * @param property The new property.
	 */
	public void setProperty(LocationProperty property)
	{
		this.property = property;
	}

	/**
	 * Get the objects residing in this location, as an array.
	 *
	 * @return An array containing all objects residing in this location.
	 */
	public ArrayList<WorldObject> getObjects()
	{
		return new ArrayList<>(objects);
	}

	/**
	 * Add the object to this location's object map.
	 *
	 * @param object The object to add.
	 */
	public void addObject(WorldObject object)
	{
		objects.add(object);
	}

	/**
	 * Remove an object from this location's object map.
	 *
	 * @param object The object to remove.
	 */
	public void removeObject(WorldObject object)
	{
		objects.remove(object);
	}

	@Override
	public String toString()
	{
		return "(" + X_COORDINATE + ", " + Z_COORDINATE + ")";
	}

	/** Specifies a property of a location. */
	public enum LocationProperty
	{
		/** Specifies that the location is open, and can be entered by objects. */
		OPEN,
		/** Specifies that the location is closed, and cannot be entered by objects. */
		CLOSED
	}
}
