package com.jenjinstudios.world;

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
	/** The locations adjacent to this one. */
	private final LinkedList<Location> adjacentLocations;
	/** The locations adjacent to this one through which a path may be plotted. */
	private final LinkedList<Location> adjacentWalkableLocations;
	/** The locations adjacent diagonally. */
	private final List<Location> diagonals;
	/** Flags whether the adjacent locations are set. */
	private boolean adjacentsSet;

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
	public Collection<WorldObject> getObjects() {
		return Collections.unmodifiableCollection(new ArrayList<>(objects));
	}

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Location)) return false;

		Location location = (Location) o;

		return X_COORDINATE == location.X_COORDINATE && Y_COORDINATE == location.Y_COORDINATE &&
			locationProperties.equals(location.locationProperties);
	}

	@Override
	public int hashCode() {
		int result = X_COORDINATE;
		result = 31 * result + Y_COORDINATE;
		result = 31 * result + locationProperties.hashCode();
		return result;
	}

	/**
	 * Get a list of locations adjacent to this one, all of which can be walked to.
	 * @return A list of adjacent, walkable locations.
	 */
	public List<Location> getAdjacentWalkableLocations() {
		return new LinkedList<>(adjacentWalkableLocations);
	}

	/**
	 * Get a list of all adjacent locations.
	 * @return The list of adjacent locations.
	 */
	protected List<Location> getAdjacentLocations() { return new LinkedList<>(adjacentLocations); }

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

	/** Set the locations adjacent to this one which can be moved to while finding a path. */
	protected void setAdjacentWalkableLocations() {
		adjacentWalkableLocations.addAll(adjacentLocations);
		for (Location walkable : adjacentLocations)
		{
			if ("false".equals(walkable.getProperties().getProperty("walkable")))
			{
				adjacentWalkableLocations.remove(walkable);
				removeDiagonalsWithAdjacentUnwalkables(walkable);
			}
		}
	}

	private void setOrdinals(Zone zone) {
		Location adjNorthEast = zone.getLocationOnGrid(X_COORDINATE + 1, Y_COORDINATE + 1);
		Location adjNorthWest = zone.getLocationOnGrid(X_COORDINATE - 1, Y_COORDINATE + 1);
		Location adjSouthEast = zone.getLocationOnGrid(X_COORDINATE + 1, Y_COORDINATE - 1);
		Location adjSouthWest = zone.getLocationOnGrid(X_COORDINATE - 1, Y_COORDINATE - 1);
		addAdjacentOrdinalLocation(adjNorthEast);
		addAdjacentOrdinalLocation(adjNorthWest);
		addAdjacentOrdinalLocation(adjSouthEast);
		addAdjacentOrdinalLocation(adjSouthWest);
	}

	private void addAdjacentOrdinalLocation(Location adjacnt) {
		if (adjacnt != null)
		{
			adjacentLocations.add(adjacnt);
			diagonals.add(adjacnt);
		}
	}

	private void setCardinals(Zone zone) {
		Location adjNorth = zone.getLocationOnGrid(X_COORDINATE, Y_COORDINATE + 1);
		Location adjSouth = zone.getLocationOnGrid(X_COORDINATE, Y_COORDINATE - 1);
		Location adjEast = zone.getLocationOnGrid(X_COORDINATE + 1, Y_COORDINATE);
		Location adjWest = zone.getLocationOnGrid(X_COORDINATE - 1, Y_COORDINATE);
		addCardinalAdjacentLocation(adjNorth);
		addCardinalAdjacentLocation(adjSouth);
		addCardinalAdjacentLocation(adjEast);
		addCardinalAdjacentLocation(adjWest);
	}

	private void addCardinalAdjacentLocation(Location adjacent) {
		if (adjacent != null)
			adjacentLocations.add(adjacent);
	}

	private void removeDiagonalsWithAdjacentUnwalkables(Location walkable) {
		for (Location blocked : walkable.getAdjacentLocations())
		{
			if (diagonals.contains(blocked))
			{
				adjacentWalkableLocations.remove(blocked);
			}
		}
	}
}
