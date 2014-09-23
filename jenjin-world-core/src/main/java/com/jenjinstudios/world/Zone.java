package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Dimension2D;
import com.jenjinstudios.world.util.LocationUtils;

import java.util.ArrayList;
import java.util.List;

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
	private final List<Location> locationGrid;

	/**
	 * Construct a new zone with the given ID and size.
	 * @param id The id number of the zone.
	 * @param specialLocations Any special locations that should be set on zone creation.
	 */
	public Zone(int id, Dimension2D size, Location... specialLocations) {
		this.id = id;
		this.xSize = size.getXSize();
		this.ySize = size.getYSize();

		locationGrid = new ArrayList<>();
		constructLocations();
		for (Location l : specialLocations)
		{
			l.getProperties().forEach((k, v) -> locationGrid.stream().filter(loc ->
				  LocationUtils.coordinatesEqual(loc, l)).forEach(loc ->
				  loc.getProperties().put(k, v)));
		}
	}

	public List<Location> getLocationGrid() { return locationGrid; }

	private void constructLocations() { for (int x = 0; x < xSize; x++) { constructColumn(x); } }

	private void constructColumn(int x) { for (int y = 0; y < ySize; y++) { locationGrid.add(new Location(x, y)); } }

}
