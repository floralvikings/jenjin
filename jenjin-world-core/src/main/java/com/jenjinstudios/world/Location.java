package com.jenjinstudios.world;

import java.util.Properties;

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
	/** The locationProperties of this location. */
	private final Properties locationProperties;

	/**
	 * Construct a new location at the given position in a zone grid.
	 * @param x The x coordinate of the zone grid.
	 * @param y The y coordinate of the zone grid.
	 */
	public Location(int x, int y) {
		this(x, y, new Properties());
	}

	/**
	 * Construct a location with the given position and locationProperties.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 */
	public Location(int x, int y, Properties properties) {
		X_COORDINATE = x;
		Y_COORDINATE = y;
		this.locationProperties = properties;
	}

	/**
	 * Get the locationProperties of this location.
	 * @return The locationProperties of this location.
	 */
	public Properties getProperties() { return locationProperties; }

	@Override
	public int hashCode() {
		int result = X_COORDINATE;
		result = 31 * result + Y_COORDINATE;
		result = 31 * result + locationProperties.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Location)) return false;

		Location location = (Location) o;

		return X_COORDINATE == location.X_COORDINATE && Y_COORDINATE == location.Y_COORDINATE &&
			  locationProperties.equals(location.locationProperties);
	}

	@Override
	public String toString() { return "(" + X_COORDINATE + ", " + Y_COORDINATE + ")"; }

}
