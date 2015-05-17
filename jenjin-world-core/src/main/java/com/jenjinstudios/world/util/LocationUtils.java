package com.jenjinstudios.world.util;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.object.WorldObject;

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
				loc = ZoneUtils.getLocationForCoordinates(zone, worldObject.getGeometry2D().getPosition());
			}
		}
		return loc;
	}

	public static boolean isWalkable(Location location) {
		return !"false".equals(location.getProperties().get("walkable"));
	}

	public static Vector2D getNorthEastCorner(Location location) {
		return new Vector2D((location.getX() + 1) * Location.SIZE - 1,
			  (location.getY() + 1) * Location.SIZE - 1);
	}

	public static Vector2D getNorthWestCorner(Location location) {
		return new Vector2D(location.getX() * Location.SIZE, (location.getY() + 1) * Location.SIZE
			  - 1);
	}

	public static Vector2D getSouthEastCorner(Location location) {
		return new Vector2D((location.getX() + 1) * Location.SIZE - 1,
			  location.getY() * Location.SIZE);
	}

	public static Vector2D getSouthWestCorner(Location location) {
		return new Vector2D(location.getX() *
			  Location.SIZE, location.getY() * Location.SIZE);
	}

	/**
	 * Get the Vector2D at the center of this location.
	 * @return The Vector2D at the center of this location.
	 */
	public static Vector2D getCenter(Location location) {
		return new Vector2D(location.getX() * Location.SIZE + Location.SIZE / 2,
			  location.getY() * Location.SIZE + Location.SIZE / 2);
	}

	public static boolean coordinatesEqual(Location location, Location otherLocation) {
		return otherLocation != null && location != null
			  && coordinatesEqual(location, otherLocation.getX(), otherLocation.getY());
	}

	public static boolean coordinatesEqual(Location location, int xCoordinate, int yCoordinate) {
		return location.getX() == xCoordinate && location.getY() == yCoordinate;
	}
}
