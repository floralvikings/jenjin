package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Vector2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

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
	public final int xSize;
	/** The number of {@code Location} objects in the y-axis. */
	public final int ySize;
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
		constructLocations();

		addSpecialLocations(specialLocations);

		setLocationVisibility();
	}

	/**
	 * Replace empty locations with the specified locations.
	 * @param specialLocations The locations to be placed in the grid.
	 */
	private void addSpecialLocations(Location[] specialLocations) {
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

	/**
	 * Get the location at the specified location in the array.
	 * @param x The x value of the location.
	 * @param y The y value of the location.
	 * @return The location at the specified spot in the array.
	 */
	public Location getLocationOnGrid(int x, int y) {
		if (x < 0 || x >= xSize || y < 0 || y >= ySize)
			return null;
		return locationGrid[x][y];
	}

	/**
	 * Cast a circle using the given center and radius, returning all locations within the circle.  This method is very
	 * slow and memory-intensive, so it should not be used during world updates.  Instead, call this method during
	 * initialization.
	 * @param center The center location.
	 * @param radius The radius of the circle.
	 * @return All locations lying within the specified circle.
	 */
	@SuppressWarnings("SuspiciousNameCombination")
	public LinkedList<Location> castVisibilityCircle(Location center, int radius) {
		LinkedList<Location> locations = new LinkedList<>();
		int centerY = center.X_COORDINATE;
		int centerX = center.Y_COORDINATE;
		HashSet<Location> visibleLocations = new HashSet<>();
		LinkedList<Location> locationsInSquare = new LinkedList<>();

		int xStart = centerX - radius;
		int yStart = centerY - radius;
		int xEnd = centerX + radius;
		int yEnd = centerY + radius;

		for (int x = xStart; x <= xEnd; x++)
		{
			for (int y = yStart; y <= yEnd; y++)
			{
				Location location = getLocationOnGrid(x, y);
				if (location != null && !"false".equals(location.getLocationProperties().getProperty("walkable")))
				{
					locationsInSquare.add(location);
				}
			}
		}

		for (Location location : locationsInSquare)
		{
			double distance = new Vector2D(centerX, centerY).getDistanceToVector(new Vector2D(location.X_COORDINATE, location.Y_COORDINATE));
			if (distance <= radius)
			{
				LinkedList<Location> visibleRay = castVisibilityRay(centerX, centerY, location.X_COORDINATE, location.Y_COORDINATE);
				visibleLocations.addAll(visibleRay);
			}
		}

		locations.addAll(visibleLocations);
		return locations;
	}

	/**
	 * This uses a modified version of Bresenhem's Line Algorithm, available in its original form <a
	 * href=http://lifc.univ-fcomte.fr/~dedu/projects/bresenham/index.html>here.</a>  This algorithm works by casting a ray
	 * until a Location with a LocationProperty containing the property "blocksVision" set to "true".
	 * @param x1 The starting x location.
	 * @param y1 The starting y location.
	 * @param x2 The ending x location.
	 * @param y2 The ending y location.
	 * @return The ray cast from the given starting points to the given end points.
	 */
	@SuppressWarnings("SuspiciousNameCombination")
	public LinkedList<Location> castVisibilityRay(int x1, int y1, int x2, int y2) {
		LinkedList<Location> visibleRay = new LinkedList<>();
		int i;               // loop counter
		int yStep, xStep;    // the step on y and x axis
		int error;           // the error accumulated during the increment
		int previousError;       // *vision the previous value of the error variable
		int y = y1, x = x1;  // the line points
		double ddy, ddx;        // compulsory variables: the double values of dy and dx
		int dx = x2 - x1;
		int dy = y2 - y1;
		visibleRay.add(getLocationOnGrid(y1, x1));  // first point
		// NB the last point can't be here, because of its previous point (which has to be verified)
		if (dy < 0)
		{
			yStep = -1;
			dy = -dy;
		} else
		{
			yStep = 1;
		}
		if (dx < 0)
		{
			xStep = -1;
			dx = -dx;
		} else
		{
			xStep = 1;
		}
		ddy = 2 * dy;  // work with double values for full precision
		ddx = 2 * dx;
		if (ddx >= ddy)
		{  // first octant (0 <= slope <= 1)
			// compulsory initialization (even for previousError, needed when dx==dy)
			previousError = error = dx;  // start in the middle of the square
			for (i = 0; i < dx; i++)
			{  // do not use the first point (already done)
				x += xStep;
				error += ddy;
				if (error > ddx)
				{  // increment y if AFTER the middle ( > )
					y += yStep;
					error -= ddx;
					// three cases (octant == right->right-top for directions below):
					if (error + previousError < ddx)  // bottom square also
					{
						if (!addLocationToVisibilityRay(y - yStep, x, visibleRay)) break;
					} else if (error + previousError > ddx)  // left square also
					{
						if (!addLocationToVisibilityRay(y, x - xStep, visibleRay)) break;
					} else
					{  // corner: bottom and left squares also
						if (!addLocationToVisibilityRay(y - yStep, x, visibleRay)) break;
						if (!addLocationToVisibilityRay(y, x - xStep, visibleRay)) break;
					}
				}
				if (!addLocationToVisibilityRay(y, x, visibleRay)) break;
				previousError = error;
			}
		} else
		{  // the same as above
			previousError = error = dy;
			for (i = 0; i < dy; i++)
			{
				y += yStep;
				error += ddx;
				if (error > ddy)
				{
					x += xStep;
					error -= ddy;
					if (error + previousError < ddy)
					{
						if(!addLocationToVisibilityRay(y, x - xStep, visibleRay)) break;
					} else
					{
						if (error + previousError > ddy)
						{
							if(!addLocationToVisibilityRay(y - yStep, x, visibleRay)) break;
						} else
						{
							if(!addLocationToVisibilityRay(y, x - xStep, visibleRay)) break;
							if(!addLocationToVisibilityRay(y - yStep, x, visibleRay)) break;
						}
					}
				}
				if(!addLocationToVisibilityRay(y, x, visibleRay)) break;
				previousError = error;
			}
		}
		return visibleRay;
	}

	/**
	 * Add the location at the given coordinates to the specified ray, returning true if the location was added, false if
	 * not.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param ray The ray.
	 * @return true if the location was added.
	 */
	private boolean addLocationToVisibilityRay(int x, int y, LinkedList<Location> ray) {
		Location location = getLocation(x, y);
		if (location == null || "true".equals(location.getLocationProperties().getProperty("blocksVision")))
		{
			return false;
		}
		ray.add(location);
		return true;
	}

	/** Add visible locations to initiated locations. */
	private void setLocationVisibility() {
		for (int x = 0; x < xSize; x++)
		{
			for (int y = 0; y < ySize; y++)
			{
				Location loc = getLocationOnGrid(x, y);
				loc.setLocationsVisibleFrom(castVisibilityCircle(loc, SightedObject.VIEW_RADIUS));
			}
		}
	}

	/** Initialize the locations in the zone. */
	private void constructLocations() {
		for (int x = 0; x < xSize; x++)
			for (int y = 0; y < ySize; y++)
				locationGrid[x][y] = new Location(x, y);
	}
}
