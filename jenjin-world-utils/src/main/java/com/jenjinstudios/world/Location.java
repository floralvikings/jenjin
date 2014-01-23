package com.jenjinstudios.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Represents a location in the world's location grid.
 * @author Caleb Brinkman
 */
public class Location
{
	/** The size, int units, of each location. */
	public static final int SIZE = 10;
	/** The x coordinate of the location in it's zone's grid. */
	public final int X_COORDINATE;
	/** The y coordinate of the location in it's zone's grid. */
	public final int Y_COORDINATE;
	/** The objects residing in this location. */
	private final HashSet<WorldObject> objects;
	/** The locationProperties of this location. */
	private final LocationProperties locationProperties;

	/**
	 * Construct a new location at the given position in a zone grid.
	 * @param x The x coordinate of the zone grid.
	 * @param y The y coordinate of the zone grid.
	 */
	public Location(int x, int y) {
		this(x, y, new LocationProperties());
	}

	/**
	 * Construct a location with the given position and locationProperties.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param locationProperties1 The locationProperties.
	 */
	public Location(int x, int y, LocationProperties locationProperties1)
	{
		X_COORDINATE = x;
		Y_COORDINATE = y;
		this.locationProperties = locationProperties1;
		objects = new HashSet<>();
	}

	/**
	 * Get the locationProperties of this location.
	 * @return The locationProperties of this location.
	 */
	public LocationProperties getLocationProperties() {
		return locationProperties;
	}

	/**
	 * Get the objects residing in this location, as an array.
	 * @return An array containing all objects residing in this location.
	 */
	public Collection<WorldObject> getObjects() {
		return Collections.unmodifiableCollection(new ArrayList<>(objects));
	}

	/**
	 * Add the object to this location's object map.
	 * @param object The object to add.
	 */
	public void addObject(WorldObject object) {
		objects.add(object);
	}

	/**
	 * Remove an object from this location's object map.
	 * @param object The object to remove.
	 */
	public void removeObject(WorldObject object) {
		objects.remove(object);
	}

	@Override
	public String toString() {
		return "(" + X_COORDINATE + ", " + Y_COORDINATE + ")";
	}

}
