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
	public final int id;
	public final int xSize;
	public final int ySize;
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

	public Location getLocationForCoordinates(Vector2D coordinates) {
		double gridX = coordinates.getXCoordinate() / Location.SIZE;
		double gridY = coordinates.getYCoordinate() / Location.SIZE;
		gridX = gridX < 0 ? -1 : gridX;
		gridY = gridY < 0 ? -1 : gridY;
		return getLocationOnGrid((int) gridX, (int) gridY);
	}

	public Location getLocationOnGrid(int x, int y) {
		Location loc;
		if (x < 0 || x >= xSize || y < 0 || y >= ySize)
			loc = null;
		else
			loc = locationGrid[x][y];
		return loc;
	}

	private void addSpecialLocations(Location[] specialLocations) {
		for (Location l : specialLocations) { locationGrid[l.X_COORDINATE][l.Y_COORDINATE] = l; }
	}

	private void constructLocations() {
		for (int x = 0; x < xSize; x++) { constructColumn(x); }
	}

	private void constructColumn(int x) {
		for (int y = 0; y < ySize; y++) { locationGrid[x][y] = new Location(x, y); }
	}

	private void initializeLocationAdjacency() {
		setAdjacentLocations();
		setAdjacentWalkableLocations();
	}

	private void setAdjacentWalkableLocations() {
		for (int x = 0; x < xSize; x++) { setAdjacentWalkableColumn(x); }
	}

	private void setAdjacentWalkableColumn(int x) {
		for (int y = 0; y < ySize; y++) { locationGrid[x][y].setAdjacentWalkableLocations(); }
	}

	private void setAdjacentLocations() {
		for (int x = 0; x < xSize; x++) { setAdjacentColumn(x); }
	}

	private void setAdjacentColumn(int x) {
		for (int y = 0; y < ySize; y++) { locationGrid[x][y].setAdjacentLocations(this);}
	}
}
