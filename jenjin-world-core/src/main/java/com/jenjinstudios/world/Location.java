package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Vector2D;

import java.util.*;

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
	private final Properties locationProperties;
	/** Flags whether the adjacent locations are set. */
	private boolean adjacentsSet;
	/** The locations adjacent to this one. */
	private final LinkedList<Location> adjacentLocations;
	/** The locations adjacent to this one through which a path may be plotted. */
	private final LinkedList<Location> adjacentWalkableLocations;
	/** The locations adjacent diagonally. */
	private final List<Location> diagonals;
	/** The center of this Location. */
	private final Vector2D center;
	private final Vector2D northEastCorner;
	private final Vector2D northWestCorner;
	private final Vector2D southEastCorner;
	private final Vector2D southWestCorner;

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
		diagonals = new LinkedList<>();
		adjacentLocations = new LinkedList<>();
		adjacentWalkableLocations = new LinkedList<>();
		X_COORDINATE = x;
		Y_COORDINATE = y;
		center = new Vector2D(X_COORDINATE * SIZE + SIZE / 2, Y_COORDINATE * SIZE + SIZE / 2);
		northEastCorner = new Vector2D((X_COORDINATE + 1) * SIZE - 1, (Y_COORDINATE + 1) * SIZE - 1);
		northWestCorner = new Vector2D(X_COORDINATE * SIZE, (Y_COORDINATE + 1) * SIZE - 1);
		southEastCorner = new Vector2D((X_COORDINATE + 1) * SIZE - 1, Y_COORDINATE * SIZE);
		southWestCorner = new Vector2D(X_COORDINATE * SIZE, Y_COORDINATE * SIZE);
		this.locationProperties = properties;
		objects = new HashSet<>();

	}

	/**
	 * Get the locationProperties of this location.
	 * @return The locationProperties of this location.
	 */
	public Properties getProperties() { return locationProperties; }

	/**
	 * Get the objects residing in this location, as an array.
	 * @return An array containing all objects residing in this location.
	 */
	public Collection<WorldObject> getObjects() { return Collections.unmodifiableCollection(new ArrayList<>(objects)); }

	/**
	 * Add the object to this location's object map.
	 * @param object The object to add.
	 */
	public void addObject(WorldObject object) { objects.add(object); }

	/**
	 * Remove an object from this location's object map.
	 * @param object The object to remove.
	 */
	public void removeObject(WorldObject object) { objects.remove(object); }

	@Override
	public String toString() { return "(" + X_COORDINATE + ", " + Y_COORDINATE + ")"; }

	/**
	 * Get a list of all adjacent locations.
	 * @return The list of adjacent locations.
	 */
	public List<Location> getAdjacentLocations() { return new LinkedList<>(adjacentLocations); }

	public Vector2D getNorthEastCorner() {
		return northEastCorner;
	}

	public Vector2D getNorthWestCorner() {
		return northWestCorner;
	}

	public Vector2D getSouthEastCorner() {
		return southEastCorner;
	}

	public Vector2D getSouthWestCorner() {
		return southWestCorner;
	}

	/**
	 * Set the locations adjacent to this one.
	 * @param zone The zone in which this location (or rather, the "adjacent" locations) lie.
	 */
	protected void setAdjacentLocations(Zone zone) {
		if (adjacentsSet)
		{
			throw new IllegalStateException("Cannot set adjacent locations after they have already been set!");
		}
		adjacentsSet = true;
		setCardinals(zone);
		setOrdinals(zone);
	}

	private void setOrdinals(Zone zone) {
		Location adjNorthEast = zone.getLocationOnGrid(X_COORDINATE + 1, Y_COORDINATE + 1);
		Location adjNorthWest = zone.getLocationOnGrid(X_COORDINATE - 1, Y_COORDINATE + 1);
		Location adjSouthEast = zone.getLocationOnGrid(X_COORDINATE + 1, Y_COORDINATE - 1);
		Location adjSouthWest = zone.getLocationOnGrid(X_COORDINATE - 1, Y_COORDINATE - 1);
		if (adjNorthEast != null)
		{
			adjacentLocations.add(adjNorthEast);
			diagonals.add(adjNorthEast);
		}
		if (adjNorthWest != null)
		{
			adjacentLocations.add(adjNorthWest);
			diagonals.add(adjNorthWest);
		}
		if (adjSouthEast != null)
		{
			adjacentLocations.add(adjSouthEast);
			diagonals.add(adjSouthEast);
		}
		if (adjSouthWest != null)
		{
			adjacentLocations.add(adjSouthWest);
			diagonals.add(adjSouthWest);
		}
	}

	private void setCardinals(Zone zone) {
		Location adjNorth = zone.getLocationOnGrid(X_COORDINATE, Y_COORDINATE + 1);
		Location adjSouth = zone.getLocationOnGrid(X_COORDINATE, Y_COORDINATE - 1);
		Location adjEast = zone.getLocationOnGrid(X_COORDINATE + 1, Y_COORDINATE);
		Location adjWest = zone.getLocationOnGrid(X_COORDINATE - 1, Y_COORDINATE);
		if (adjNorth != null)
			adjacentLocations.add(adjNorth);
		if (adjSouth != null)
			adjacentLocations.add(adjSouth);
		if (adjEast != null)
			adjacentLocations.add(adjEast);
		if (adjWest != null)
			adjacentLocations.add(adjWest);
	}

	/**
	 * Get a list of locations adjacent to this one, all of which can be walked to.
	 * @return A list of adjacent, walkable locations.
	 */
	public List<Location> getAdjacentWalkableLocations() {
		return new LinkedList<>(adjacentWalkableLocations);
	}

	/**
	 * Get the Vector2D at the center of this location.
	 * @return The Vector2D at the center of this location.
	 */
	public Vector2D getCenter() {
		return center;
	}

	/** Set the locations adjacent to this one which can be moved to while finding a path. */
	protected void setAdjacentWalkableLocations() {
		adjacentWalkableLocations.addAll(adjacentLocations);
		for (Location walkable : adjacentLocations)
		{
			if ("false".equals(walkable.getProperties().getProperty("walkable")))
			{
				adjacentWalkableLocations.remove(walkable);
				for (Location blocked : walkable.getAdjacentLocations())
				{
					if (diagonals.contains(blocked))
					{
						adjacentWalkableLocations.remove(blocked);
					}
				}
			}
		}
	}
}
