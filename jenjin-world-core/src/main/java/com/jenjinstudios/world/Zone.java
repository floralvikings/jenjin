package com.jenjinstudios.world;

import com.jenjinstudios.world.collections.LocationCollection;
import com.jenjinstudios.world.math.Dimension2D;

/**
 * The {@code Zone} class represents a grid of {@code Location} objects within the {@code World}.  Zones cannot be
 * accessed from other Zones.  Support for this feature is planned in a future release.
 * @author Caleb Brinkman
 */
public class Zone
{
	private int id;
	private int xSize;
	private int ySize;
	private LocationCollection locationGrid;

	/**
	 * Construct a new zone with the given ID and size.
	 * @param id The id number of the zone.
	 * @param specialLocations Any special locations that should be set on zone creation.
	 */
	public Zone(int id, Dimension2D size, Location... specialLocations) {
		this.id = id;
		this.xSize = size.getXSize();
		this.ySize = size.getYSize();

		locationGrid = new LocationCollection();
		populateLocations();
		locationGrid.addAll(specialLocations);
	}

	public Zone() { }

	public LocationCollection getLocationGrid() {
		if (locationGrid != null && locationGrid.size() < xSize * ySize)
		{
			populateLocations();
		} else if (locationGrid == null)
		{
			locationGrid = new LocationCollection();
		}
		return locationGrid;
	}

	public int getId() { return id; }

	public int getXSize() { return xSize; }

	public int getYSize() { return ySize; }

	private void populateLocations() { for (int x = 0; x < xSize; x++) { populateColumn(x); } }

	private void populateColumn(int x) {
		for (int y = 0; y < ySize; y++)
		{
			if (!locationGrid.containsLocationWithXY(x, y))
			{
				locationGrid.add(new Location(x, y));
			}
		}
	}

}
