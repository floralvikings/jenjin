package com.jenjinstudios.world.util;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.math.Vector2D;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

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
			Zone zone = world.getZone(worldObject.getZoneID());
			if (zone != null)
			{
				loc = zone.getLocationForCoordinates(worldObject.getVector2D());
			}
		}
		return loc;
	}

	public static List<Location> getAdjacentLocations(Location loc, Zone zone) {
		List<Location> adjacents = new LinkedList<>();
		if (zone != null)
		{
			int x = loc.X_COORDINATE;
			int y = loc.Y_COORDINATE;

			Location adjNorthEast = zone.getLocationOnGrid(x + 1, y + 1);
			Location adjNorthWest = zone.getLocationOnGrid(x - 1, y + 1);
			Location adjSouthEast = zone.getLocationOnGrid(x + 1, y - 1);
			Location adjSouthWest = zone.getLocationOnGrid(x - 1, y - 1);
			Location adjNorth = zone.getLocationOnGrid(x, y + 1);
			Location adjSouth = zone.getLocationOnGrid(x, y - 1);
			Location adjEast = zone.getLocationOnGrid(x + 1, y);
			Location adjWest = zone.getLocationOnGrid(x - 1, y);

			adjacents.add(adjNorthEast);
			adjacents.add(adjNorthWest);
			adjacents.add(adjSouthEast);
			adjacents.add(adjSouthWest);
			adjacents.add(adjNorth);
			adjacents.add(adjSouth);
			adjacents.add(adjEast);
			adjacents.add(adjWest);

			adjacents.removeAll(Collections.singleton(null));
		}
		return adjacents;
	}

	public static List<Location> getAdjacentDiagonalLocations(Location loc, Zone zone) {
		List<Location> diagonals = new LinkedList<>();

		if (zone != null)
		{
			int x = loc.X_COORDINATE;
			int y = loc.Y_COORDINATE;

			Location adjNorthEast = zone.getLocationOnGrid(x + 1, y + 1);
			Location adjNorthWest = zone.getLocationOnGrid(x - 1, y + 1);
			Location adjSouthEast = zone.getLocationOnGrid(x + 1, y - 1);
			Location adjSouthWest = zone.getLocationOnGrid(x - 1, y - 1);

			diagonals.add(adjNorthEast);
			diagonals.add(adjNorthWest);
			diagonals.add(adjSouthEast);
			diagonals.add(adjSouthWest);

			diagonals.removeAll(Collections.singleton(null));
		}
		return diagonals;
	}

	public static List<Location> getAdjacentWalkableLocations(Location location, Zone zone) {
		List<Location> adjacentWalkable = new LinkedList<>();
		if (zone != null)
		{
			List<Location> adjacent = getAdjacentLocations(location, zone);
			List<Location> diagonals = getAdjacentDiagonalLocations(location, zone);
			adjacentWalkable.addAll(adjacent);
			Stream<Location> stream = adjacentWalkable.stream().filter(LocationUtils::isWalkable);
		}
		return adjacentWalkable;
	}

	public static boolean isWalkable(Location location) {
		return !"false".equals(location.getProperties().getProperty("walkable"));
	}

	public static Vector2D getNorthEastCorner(Location location) {
		return new Vector2D((location.X_COORDINATE + 1) * Location.SIZE - 1,
			  (location.Y_COORDINATE + 1) * Location.SIZE - 1);
	}

	public static Vector2D getNorthWestCorner(Location location) {
		return new Vector2D(location.X_COORDINATE * Location.SIZE, (location.Y_COORDINATE + 1) * Location.SIZE - 1);
	}

	public static Vector2D getSouthEastCorner(Location location) {
		return new Vector2D((location.X_COORDINATE + 1) * Location.SIZE - 1, location.Y_COORDINATE * Location.SIZE);
	}

	public static Vector2D getSouthWestCorner(Location location) {
		return new Vector2D(location.X_COORDINATE *
			  Location.SIZE, location.Y_COORDINATE * Location.SIZE);
	}

	/**
	 * Get the Vector2D at the center of this location.
	 * @return The Vector2D at the center of this location.
	 */
	public static Vector2D getCenter(Location location) {
		return new Vector2D(location.X_COORDINATE * Location.SIZE + Location.SIZE / 2,
			  location.Y_COORDINATE * Location.SIZE + Location.SIZE / 2);
	}
}
