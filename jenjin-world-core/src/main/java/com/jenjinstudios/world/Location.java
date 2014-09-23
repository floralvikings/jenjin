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
	private final int xCoordinate;
	/** The y coordinate of the location in it's zone's grid. */
	private final int yCoordinate;
	/** The locationProperties of this location. */
	private final Map<String, Object> locationProperties;

	/**
	 * Construct a new location at the given position in a zone grid.
	 * @param x The x coordinate of the zone grid.
	 * @param y The y coordinate of the zone grid.
	 */
	public Location(int x, int y) { this(x, y, new HashMap<>()); }

	/**
	 * Construct a location with the given position and locationProperties.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 */
	public Location(int x, int y, Map<String, Object> properties) {
		xCoordinate = x;
		yCoordinate = y;
		this.locationProperties = new HashMap<>(properties);
	}

	/**
	 * Get the locationProperties of this location.
	 * @return The locationProperties of this location.
	 */
	public Map<String, Object> getProperties() { return locationProperties; }

	/** The x coordinate of the location in it's zone's grid. */
	public int getXCoordinate() { return xCoordinate; }

	/** The y coordinate of the location in it's zone's grid. */
	public int getYCoordinate() { return yCoordinate; }

	@Override
	public int hashCode() {
		int result = xCoordinate;
		result = 31 * result + yCoordinate;
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Location)) return false;

		Location location = (Location) o;

		return xCoordinate == location.xCoordinate && yCoordinate == location.yCoordinate;
	}

	@Override
	public String toString() { return "(" + xCoordinate + ", " + yCoordinate + ")"; }
}
