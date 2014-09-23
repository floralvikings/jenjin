package com.jenjinstudios.world.util;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.math.Vector2D;

/**
 * @author Caleb Brinkman
 */
public class LocationUtils
{
	public static Location getObjectLocation(WorldObject worldObject) {
		Location loc = null;
		World world = worldObject.getWorld();
		if (world != null)
		{
			Zone zone = world.getZones().get(worldObject.getZoneID());
			if (zone != null)
			{
				loc = ZoneUtils.getLocationForCoordinates(zone, worldObject.getVector2D());
			}
		}
		return loc;
	}

	public static boolean isWalkable(Location location) {
		return !"false".equals(location.getProperties().get("walkable"));
	}

	public static Vector2D getNorthEastCorner(Location location) {
		return new Vector2D((location.getXCoordinate() + 1) * Location.SIZE - 1,
			  (location.getYCoordinate() + 1) * Location.SIZE - 1);
	}

	public static Vector2D getNorthWestCorner(Location location) {
		return new Vector2D(location.getXCoordinate() * Location.SIZE, (location.getYCoordinate() + 1) * Location.SIZE
			  - 1);
	}

	public static Vector2D getSouthEastCorner(Location location) {
		return new Vector2D((location.getXCoordinate() + 1) * Location.SIZE - 1,
			  location.getYCoordinate() * Location.SIZE);
	}

	public static Vector2D getSouthWestCorner(Location location) {
		return new Vector2D(location.getXCoordinate() *
			  Location.SIZE, location.getYCoordinate() * Location.SIZE);
	}

	/**
	 * Get the Vector2D at the center of this location.
	 * @return The Vector2D at the center of this location.
	 */
	public static Vector2D getCenter(Location location) {
		return new Vector2D(location.getXCoordinate() * Location.SIZE + Location.SIZE / 2,
			  location.getYCoordinate() * Location.SIZE + Location.SIZE / 2);
	}

	public static boolean coordinatesEqual(Location location, Location otherLocation) {
		return otherLocation != null && location != null
			  && coordinatesEqual(location, otherLocation.getXCoordinate(), otherLocation.getYCoordinate());
	}

	public static boolean coordinatesEqual(Location location, int xCoordinate, int yCoordinate) {
		return location.getXCoordinate() == xCoordinate && location.getYCoordinate() == yCoordinate;
	}
}
