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
	private final LocationProperties locationProperties;
	/** Flags whether the adjacent locations are set. */
	private boolean hasLocationsSet;
	/** The location adjacent to the North. */
	private Location adjNorth;
	/** The location adjacent to the South. */
	private Location adjSouth;
	/** The location adjacent to the East. */
	private Location adjEast;
	/** The location adjacent to the West. */
	private Location adjWest;
	/** The location adjacent to the NorthEast. */
	private Location adjNorthEast;
	/** The location adjacent to the NorthWest. */
	private Location adjNorthWest;
	/** The location adjacent to the SouthEast. */
	private Location adjSouthEast;
	/** The location adjacent to the SouthWest. */
	private Location adjSouthWest;
	/** The locations adjacent to this one. */
	private final LinkedList<Location> adjacentLocations;
	/** The locations adjacent to this one through which a path may be plotted. */
	private final LinkedList<Location> adjacentWalkableLocations;
	/** The locations adjacent diagonally. */
	private final List<Location> diagonals;
	/** The center of this Location. */
	private final Vector2D center;

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
	public Location(int x, int y, LocationProperties locationProperties1) {
		diagonals = new LinkedList<>();
		adjacentLocations = new LinkedList<>();
		adjacentWalkableLocations = new LinkedList<>();
		X_COORDINATE = x;
		Y_COORDINATE = y;
		center = new Vector2D(X_COORDINATE * SIZE + SIZE / 2, Y_COORDINATE * SIZE + SIZE / 2);
		this.locationProperties = locationProperties1;
		objects = new HashSet<>();
	}

	/**
	 * Get the locationProperties of this location.
	 * @return The locationProperties of this location.
	 */
	public LocationProperties getLocationProperties() { return locationProperties; }

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
	 * The location adjacent to the North.
	 * @return The location adjacent to the north.
	 */
	public Location getAdjNorth() { return adjNorth; }

	/**
	 * The location adjacent to the South.
	 * @return The location adjacent to the South.
	 */
	public Location getAdjSouth() { return adjSouth; }

	/**
	 * The location adjacent to the East.
	 * @return The location adjacent to the East.
	 */
	public Location getAdjEast() { return adjEast; }

	/**
	 * The location adjacent to the West.
	 * @return The data adjacent to the west.
	 */
	public Location getAdjWest() { return adjWest; }

	/**
	 * Get a list of all adjacent locations.
	 * @return The list of adjacent locations.
	 */
	public List<Location> getAdjacentLocations() { return new LinkedList<>(adjacentLocations); }

	/**
	 * Set the locations adjacent to this one.
	 * @param zone The zone in which this location (or rather, the "adjacent" locations) lie.
	 */
	protected void setAdjacentLocations(Zone zone) {
		if (hasLocationsSet)
		{
			throw new IllegalStateException("Cannot set adjacent locations after they have already been set!");
		}
		hasLocationsSet = true;

		adjNorth = zone.getLocationOnGrid(X_COORDINATE, Y_COORDINATE + 1);
		adjSouth = zone.getLocationOnGrid(X_COORDINATE, Y_COORDINATE - 1);
		adjEast = zone.getLocationOnGrid(X_COORDINATE + 1, Y_COORDINATE);
		adjWest = zone.getLocationOnGrid(X_COORDINATE - 1, Y_COORDINATE);
		adjNorthEast = zone.getLocationOnGrid(X_COORDINATE + 1, Y_COORDINATE + 1);
		adjNorthWest = zone.getLocationOnGrid(X_COORDINATE - 1, Y_COORDINATE + 1);
		adjSouthEast = zone.getLocationOnGrid(X_COORDINATE + 1, Y_COORDINATE - 1);
		adjSouthWest = zone.getLocationOnGrid(X_COORDINATE - 1, Y_COORDINATE - 1);

		if (adjNorth != null)
			adjacentLocations.add(adjNorth);
		if (adjSouth != null)
			adjacentLocations.add(adjSouth);
		if (adjEast != null)
			adjacentLocations.add(adjEast);
		if (adjWest != null)
			adjacentLocations.add(adjWest);
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

	/**
	 * The location adjacent to the NorthEast.
	 * @return The location adjacent to the NorthEast.
	 */
	public Location getAdjNorthEast() { return adjNorthEast; }

	/**
	 * The location adjacent to the NorthWest.
	 * @return The location adjacent to the NorthWest.
	 */
	public Location getAdjNorthWest() { return adjNorthWest; }

	/**
	 * The location adjacent to the SouthEast.
	 * @return The location adjacent to the SouthEast.
	 */
	public Location getAdjSouthEast() { return adjSouthEast; }

	/**
	 * The location adjacent to the SouthWest.
	 * @return The location adjacent to the SouthWest.
	 */
	public Location getAdjSouthWest() { return adjSouthWest; }

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
			if ("false".equals(walkable.getLocationProperties().getProperty("walkable")))
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
