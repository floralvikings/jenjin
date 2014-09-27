package com.jenjinstudios.world;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a location in the world's location grid.
 * @author Caleb Brinkman
 */
public class Location
{
	/** The size, int units, of each location. */
	public static final int SIZE = 10;
	/** The x coordinate of the location in it's zone's grid. */
	private int x;
	/** The y coordinate of the location in it's zone's grid. */
	private int y;
	/** The properties of this location. */
	private Map<String, String> properties;

	public Location() { }

	/**
	 * Construct a new location at the given position in a zone grid.
	 * @param x The x coordinate of the zone grid.
	 * @param y The y coordinate of the zone grid.
	 */
	public Location(int x, int y) { this(x, y, new HashMap<>()); }

	/**
	 * Construct a location with the given position and properties.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 */
	public Location(int x, int y, Map<String, String> properties) {
		this.x = x;
		this.y = y;
		this.properties = new HashMap<>(properties);
	}

	/**
	 * Get the properties of this location.
	 * @return The properties of this location.
	 */
	public Map<String, String> getProperties() {
		if (properties == null)
		{
			properties = new HashMap<>();
		}
		return properties;
	}

	/** The x coordinate of the location in it's zone's grid. */
	public int getX() { return x; }

	/** The y coordinate of the location in it's zone's grid. */
	public int getY() { return y; }

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		result = 31 * result + properties.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Location)) return false;

		Location location = (Location) o;

		return x == location.x && y == location.y &&
			  properties.equals(location.properties);
	}

	@Override
	public String toString() { return "(" + x + ", " + y + ")"; }
}
