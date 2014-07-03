package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Dimension2D;
import com.jenjinstudios.world.math.Vector2D;

/**
 * The {@code Zone} class represents a grid of {@code Location} objects within the {@code World}.  Zones cannot be
 * accessed from other Zones.  Support for this feature is planned in a future release.
 * @author Caleb Brinkman
 */
public class Zone
{
	/** The number assigned to this Zone by the world on initialization. */
	public final int id;
	/** The number of {@code Location} objects in the x-axis. */
	public final int xSize;
	/** The number of {@code Location} objects in the y-axis. */
	public final int ySize;
	/** The grid of {@code Location} objects. */
	private final Location[][] locationGrid;

	/**
	 * Construct a new zone with the given ID and size.
	 * @param id The id number of the zone.
	 * @param specialLocations Any special locations that should be set on zone creation.
	 */
	public Zone(int id, Dimension2D size, Location... specialLocations) {
		this.id = id;
		this.xSize = size.getXSize();
		this.ySize = size.getYSize();

		locationGrid = new Location[size.getXSize()][size.getYSize()];
		constructLocations();
		addSpecialLocations(specialLocations);
		initializeLocationAdjacency();
	}

	public int getXSize() { return xSize; }

	public int getYSize() { return ySize; }

	private void addSpecialLocations(Location[] specialLocations) {
		for (Location l : specialLocations)
		{
			locationGrid[l.X_COORDINATE][l.Y_COORDINATE] = l;
		}
	}

	/**
	 * Determine if the coordinates of the vector are within this Zones boundaries.
	 * @param vector2D The coordinates to check.
	 * @return Whether the coordinates of the vector are within this Zones boundaries.
	 */
	public boolean isInvalidLocation(Vector2D vector2D) {
		double x = vector2D.getXCoordinate();
		double y = vector2D.getYCoordinate();
		return !(x < 0 || y < 0 || x / Location.SIZE >= xSize || y / Location.SIZE >= ySize);
	}

	public Location getLocationForCoordinates(Vector2D centerCoordinates) {
		return getLocationForCoordinates(centerCoordinates.getXCoordinate(), centerCoordinates.getYCoordinate());
	}

	protected Location getLocationForCoordinates(double x, double y) {
		int gridX = (int) x / Location.SIZE;
		int gridY = (int) y / Location.SIZE;
		return getLocationOnGrid(gridX, gridY);
	}

	public Location getLocationOnGrid(int x, int y) {
		Location loc;
		if (x < 0 || x >= xSize || y < 0 || y >= ySize)
			loc = null;
		else
			loc = locationGrid[x][y];
		return loc;
	}

	private void constructLocations() {
		for (int x = 0; x < xSize; x++)
			constructColumn(x);
	}

	private void constructColumn(int x) {
		for (int y = 0; y < ySize; y++)
			locationGrid[x][y] = new Location(x, y);
	}

	private void initializeLocationAdjacency() {
		setAdjacentLocations();
		setAdjacentWalkableLocations();
	}

	private void setAdjacentWalkableLocations() {
		for (int x = 0; x < xSize; x++)
			setAdjacentWalkableColumn(x);
	}

	private void setAdjacentWalkableColumn(int x) {
		for (int y = 0; y < ySize; y++)
			locationGrid[x][y].setAdjacentWalkableLocations();
	}

	private void setAdjacentLocations() {
		for (int x = 0; x < xSize; x++)
			setAdjacentColumn(x);
	}

	private void setAdjacentColumn(int x) {
		for (int y = 0; y < ySize; y++)
			locationGrid[x][y].setAdjacentLocations(this);
	}
}
