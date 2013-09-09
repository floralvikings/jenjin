package com.jenjinstudios.world;

import java.util.TreeMap;

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
	private final TreeMap<Integer, WorldObject> objects;
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
		objects = new TreeMap<>();
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

	/**
	 * Get the objects residing in this location, as an array.
	 *
	 * @return An array containing all objects residing in this location.
	 */
	public WorldObject[] getObjects()
	{
		WorldObject[] r = new WorldObject[objects.size()];
		objects.values().toArray(r);
		return r;
	}

	/**
	 * Add the object to this location's object map.
	 *
	 * @param object The object to add.
	 */
	public void addObject(WorldObject object)
	{
		objects.put(object.getId(), object);
	}

	/**
	 * Remove an object from this location's object map.
	 *
	 * @param object The object to remove.
	 */
	public void removeObject(WorldObject object)
	{
		objects.remove(object.getId());
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
