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
		initLocations();

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
	 * @param centerX The center location x.
	 * @param centerY The center location y.
	 * @param radius The radius of the circle.
	 * @return All locations lying within the specified circle.
	 */
	public HashSet<Location> castCircle(int centerX, int centerY, int radius) {
		HashSet<Location> locations = new HashSet<>();
		int d = 3 - (2 * radius);
		int x = 0;
		int y = radius;

		do
		{
			locations.addAll(castRay(centerX, centerY, centerX + x, centerY + y));
			locations.addAll(castRay(centerX, centerY, centerX + x, centerY - y));
			locations.addAll(castRay(centerX, centerY, centerX - x, centerY + y));
			locations.addAll(castRay(centerX, centerY, centerX - x, centerY - y));
			locations.addAll(castRay(centerX, centerY, centerX + y, centerY + x));
			locations.addAll(castRay(centerX, centerY, centerX + y, centerY - x));
			locations.addAll(castRay(centerX, centerY, centerX - y, centerY + x));
			locations.addAll(castRay(centerX, centerY, centerX - y, centerY - x));
			if (d < 0)
			{
				d = d + (4 * x) + 6;
			} else
			{
				d = d + 4 * (x - y) + 10;
				y--;
			}
			x++;
		} while (x <= y);
		return locations;
	}

	/**
	 * This uses a modified version of Bresenhem's Line Algorithm, available in its original form <a
	 * href=http://lifc.univ-fcomte.fr/~dedu/projects/bresenham/index.html>here.</a>
	 * @param x1 The starting x location.
	 * @param y1 The starting y location.
	 * @param x2 The ending x location.
	 * @param y2 The ending y location.
	 * @return A list of locations that lie along the ray.
	 */
	@SuppressWarnings("SuspiciousNameCombination")
	public LinkedList<Location> castRay(int x1, int y1, int x2, int y2) {
		LinkedList<Location> ray = new LinkedList<>();
		// If slope is outside the range [-1,1], swap x and y
		int i;               // loop counter
		int ystep, xstep;    // the step on y and x axis
		int error;           // the error accumulated during the increment
		int errorprev;       // *vision the previous value of the error variable
		int y = y1, x = x1;  // the line points
		int ddy, ddx;        // compulsory variables: the double values of dy and dx
		int dx = x2 - x1;
		int dy = y2 - y1;
		ray.add(getLocationOnGrid(y1, x1));  // first point
		// NB the last point can't be here, because of its previous point (which has to be verified)
		if (dy < 0)
		{
			ystep = -1;
			dy = -dy;
		} else
			ystep = 1;
		if (dx < 0)
		{
			xstep = -1;
			dx = -dx;
		} else
			xstep = 1;
		ddy = 2 * dy;  // work with double values for full precision
		ddx = 2 * dx;
		if (ddx >= ddy)
		{  // first octant (0 <= slope <= 1)
			// compulsory initialization (even for errorprev, needed when dx==dy)
			errorprev = error = dx;  // start in the middle of the square
			for (i = 0; i < dx; i++)
			{  // do not use the first point (already done)
				x += xstep;
				error += ddy;
				if (error > ddx)
				{  // increment y if AFTER the middle ( > )
					y += ystep;
					error -= ddx;
					// three cases (octant == right->right-top for directions below):
					if (error + errorprev < ddx)  // bottom square also
						ray.add(getLocationOnGrid(y - ystep, x));
					else if (error + errorprev > ddx)  // left square also
						ray.add(getLocationOnGrid(y, x - xstep));
					else
					{  // corner: bottom and left squares also
						ray.add(getLocationOnGrid(y - ystep, x));
						ray.add(getLocationOnGrid(y, x - xstep));
					}
				}
				ray.add(getLocationOnGrid(y, x));
				errorprev = error;
			}
		} else
		{  // the same as above
			errorprev = error = dy;
			for (i = 0; i < dy; i++)
			{
				y += ystep;
				error += ddx;
				if (error > ddy)
				{
					x += xstep;
					error -= ddy;
					if (error + errorprev < ddy)
						ray.add(getLocationOnGrid(y, x - xstep));
					else if (error + errorprev > ddy)
						ray.add(getLocationOnGrid(y - ystep, x));
					else
					{
						ray.add(getLocationOnGrid(y, x - xstep));
						ray.add(getLocationOnGrid(y - ystep, x));
					}
				}
				ray.add(getLocationOnGrid(y, x));
				errorprev = error;
			}
		}

		return ray;
	}

	/** Initialize the locations in the zone. */
	private void initLocations() {
		for (int x = 0; x < xSize; x++)
			for (int y = 0; y < ySize; y++)
				locationGrid[x][y] = new Location(x, y);
	}
}
