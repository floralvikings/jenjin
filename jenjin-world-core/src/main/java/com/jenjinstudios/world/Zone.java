package com.jenjinstudios.world;

import com.jenjinstudios.world.collections.LocationArrayList;
import com.jenjinstudios.world.math.Dimension2D;

import javax.xml.bind.annotation.*;
import java.util.Collections;

/**
 * The {@code Zone} class represents a grid of {@code Location} objects within the {@code World}.  Zones cannot be
 * accessed from other Zones.  Support for this feature is planned in a future release.
 * @author Caleb Brinkman
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "zone", namespace = "https://www.jenjinstudios.com")
public class Zone
{
	@XmlAttribute(name = "id", namespace = "https://www.jenjinstudios.com")
	private int id;
	@XmlAttribute(name = "xSize", namespace = "https://www.jenjinstudios.com")
	private int xSize;
	@XmlAttribute(name = "ySize", namespace = "https://www.jenjinstudios.com")
	private int ySize;
	@XmlElement(name = "location", namespace = "https://www.jenjinstudios.com")
	private LocationArrayList locationGrid;

	/**
	 * Construct a new zone with the given ID and size.
	 * @param id The id number of the zone.
	 * @param specialLocations Any special locations that should be set on zone creation.
	 */
	public Zone(int id, Dimension2D size, Location... specialLocations) {
		this.id = id;
		this.xSize = size.getXSize();
		this.ySize = size.getYSize();

		locationGrid = new LocationArrayList();
		populateLocations();
		Collections.addAll(locationGrid, specialLocations);
	}

	public LocationArrayList getLocationGrid() {
		if (locationGrid == null)
		{
			locationGrid = new LocationArrayList();
			populateLocations();
		}
		return locationGrid;
	}

	public int getId() { return id; }

	public int getXSize() { return xSize; }

	public int getYSize() { return ySize; }

	private void populateLocations() { for (int x = 0; x < xSize; x++) { populateColumn(x); } }

	private void populateColumn(int x) { for (int y = 0; y < ySize; y++) { locationGrid.add(new Location(x, y)); } }

}
