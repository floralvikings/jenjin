package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Vector2D;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The {@code Zone} class represents a grid of {@code Location} objects within the {@code World}.  Zones cannot be
 * accessed from other Zones.  Suuport for this feature is planned in a future release.
 * @author Caleb Brinkman
 */
public class Zone
{
	/** The number assigned to this Zone by the world on initialization. */
	public final int id;
	/** The number of {@code Location} objects in the x-axis. */
	private final int xSize;
	/** The number of {@code Location} objects in the y-axis. */
	private final int ySize;
	/** The grid of {@code Location} objects. */
	private final Location[][] locationGrid;

	/**
	 * Construct a new zone with the given ID and size.
	 * @param id The id number of the zone.
	 * @param xSize The x length of the zone.
	 * @param ySize The y length of zone.
	 */
	public Zone(int id, int xSize, int ySize) {
		this(id, xSize, ySize, null);
	}

	/**
	 * Construct a new zone with the given ID and size.
	 * @param id The id number of the zone.
	 * @param xSize The x length of the zone.
	 * @param ySize The y length of zone.
	 * @param specialLocations Any special locations that should be set on zone creation.
	 */
	public Zone(int id, int xSize, int ySize, Location[] specialLocations) {
		this.id = id;
		this.xSize = xSize;
		this.ySize = ySize;

		locationGrid = new Location[xSize][ySize];
		initLocations();

		if (specialLocations != null)
		{
			for (Location l : specialLocations)
			{
				locationGrid[l.X_COORDINATE][l.Y_COORDINATE] = l;
			}
		}
	}

	/**
	 * Determine if the coordinates of the vector are within this Zones boundries.
	 * @param vector2D The coordinates to check.
	 * @return Whether the coordinates of the vector are within this Zones boundries.
	 */
	public boolean isValidLocation(Vector2D vector2D) {
		double x = vector2D.getXCoordinate();
		double y = vector2D.getYCoordinate();
		return (x < 0 || y < 0 || x / Location.SIZE >= xSize || y / Location.SIZE >= ySize);
	}

	/**
	 * Get an area of location objects.
	 * @param centerCoords The center of the area to return.
	 * @param radius The radius of the area.
	 * @return An ArrayList containing all valid locations in the specified area.
	 */
	public ArrayList<Location> getLocationArea(Vector2D centerCoords, int radius) {
		ArrayList<Location> areaGrid = new ArrayList<>();
		Location center = getLocation(centerCoords);
		int xStart = Math.max(center.X_COORDINATE - (radius - 1), 0);
		int yStart = Math.max(center.Y_COORDINATE - (radius - 1), 0);
		int xEnd = Math.min(center.X_COORDINATE + (radius - 1), locationGrid.length - 1);
		int yEnd = Math.min(center.Y_COORDINATE + (radius - 1), locationGrid.length - 1);

		for (int x = xStart; x <= xEnd; x++)
		{
			areaGrid.addAll(Arrays.asList(locationGrid[x]).subList(yStart, yEnd + 1));
		}

		return areaGrid;
	}

	/**
	 * Get the location at the specified coordinates.
	 * @param centerCoords The coodinates.
	 * @return The location at the specified coordinates.
	 */
	public Location getLocation(Vector2D centerCoords) {
		return getLocation(centerCoords.getXCoordinate(), centerCoords.getYCoordinate());
	}

	/**
	 * Get the location at the specified coordinates.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @return The location at the specified coordinates.
	 */
	public Location getLocation(double x, double y) {
		return locationGrid[(int) x / Location.SIZE][(int) y / Location.SIZE];
	}

	/** Initialize the locations in the zone. */
	private void initLocations() {
		for (int x = 0; x < xSize; x++)
			for (int y = 0; y < ySize; y++)
				locationGrid[x][y] = new Location(x, y);
	}
}
