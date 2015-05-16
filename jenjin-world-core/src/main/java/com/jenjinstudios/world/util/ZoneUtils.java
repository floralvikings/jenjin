package com.jenjinstudios.world.util;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.collections.LocationCollection;
import com.jenjinstudios.world.math.Dimension2D;
import com.jenjinstudios.world.math.Vector2D;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Caleb Brinkman
 */
public class ZoneUtils
{
	private static final int DEFAULT_SIZE = 50;

	public static Location getLocationForCoordinates(Zone zone, Vector2D coordinates) {
		double gridX = coordinates.getXValue() / Location.SIZE;
		double gridY = coordinates.getYValue() / Location.SIZE;
		gridX = gridX < 0 ? -1 : gridX;
		gridY = gridY < 0 ? -1 : gridY;
		return getLocationOnGrid(zone, (int) gridX, (int) gridY);
	}

	public static Location getLocationOnGrid(Zone zone, int x, int y) {
		Location loc;
		if (x < 0 || x >= zone.getXSize() || y < 0 || y >= zone.getYSize())
			loc = null;
		else
		{
			LocationCollection locationGrid = zone.getLocationGrid();
			loc = locationGrid.getLocationWithXY(x, y);
		}
		return loc;
	}

	public static List<Location> getAdjacentLocations(Zone zone, Location loc) {
		List<Location> adjacents = new LinkedList<>();
		if (zone != null)
		{
			int x = loc.getX();
			int y = loc.getY();

			Location adjNorthEast = getLocationOnGrid(zone, x + 1, y + 1);
			Location adjNorthWest = getLocationOnGrid(zone, x - 1, y + 1);
			Location adjSouthEast = getLocationOnGrid(zone, x + 1, y - 1);
			Location adjSouthWest = getLocationOnGrid(zone, x - 1, y - 1);
			Location adjNorth = getLocationOnGrid(zone, x, y + 1);
			Location adjSouth = getLocationOnGrid(zone, x, y - 1);
			Location adjEast = getLocationOnGrid(zone, x + 1, y);
			Location adjWest = getLocationOnGrid(zone, x - 1, y);

			adjacents.add(adjNorth);
			adjacents.add(adjSouth);
			adjacents.add(adjEast);
			adjacents.add(adjWest);
			adjacents.add(adjNorthEast);
			adjacents.add(adjNorthWest);
			adjacents.add(adjSouthEast);
			adjacents.add(adjSouthWest);

			adjacents.removeAll(Collections.singleton(null));
		}
		return adjacents;
	}

	public static List<Location> getAdjacentDiagonalLocations(Zone zone, Location loc) {
		List<Location> diagonals = new LinkedList<>();

		if (zone != null)
		{
			int x = loc.getX();
			int y = loc.getY();

			Location adjNorthEast = getLocationOnGrid(zone, x + 1, y + 1);
			Location adjNorthWest = getLocationOnGrid(zone, x - 1, y + 1);
			Location adjSouthEast = getLocationOnGrid(zone, x + 1, y - 1);
			Location adjSouthWest = getLocationOnGrid(zone, x - 1, y - 1);

			diagonals.add(adjNorthEast);
			diagonals.add(adjNorthWest);
			diagonals.add(adjSouthEast);
			diagonals.add(adjSouthWest);

			diagonals.removeAll(Collections.singleton(null));
		}
		return diagonals;
	}

	public static List<Location> getAdjacentWalkableLocations(Zone zone, Location location) {
		List<Location> adjacentWalkable = new LinkedList<>();
		if (zone != null)
		{
			List<Location> adjacent = getAdjacentLocations(zone, location);
			List<Location> diagonals = getAdjacentDiagonalLocations(zone, location);
			List<Location> unwalkable = new LinkedList<>();
			adjacentWalkable.addAll(adjacent);
			adjacentWalkable.stream().filter(loc -> !LocationUtils.isWalkable(loc)).forEach(loc -> {
				unwalkable.add(loc);
				getAdjacentLocations(zone, loc).stream().filter(diagonals::contains).
					  forEach(unwalkable::add);
			});
			adjacentWalkable.removeAll(unwalkable);
		}
		return adjacentWalkable;
	}

	/**
	 * Get the location from the zone grid that contains the specified vector2D.
	 * @param world The world in which the zone is contained
	 * @param zoneID The ID of the zone in which to look for the location.
	 * @param vector2D The vector; only x and y values are used.
	 * @return The location that contains the specified vector2D.
	 */
	public static Location getLocationForCoordinates(World world, int zoneID, Vector2D vector2D) {
		return getLocationForCoordinates(world.getZones().get(zoneID), vector2D);
	}

	static Zone createDefautZone() { return new Zone(0, new Dimension2D(DEFAULT_SIZE, DEFAULT_SIZE)); }
}
