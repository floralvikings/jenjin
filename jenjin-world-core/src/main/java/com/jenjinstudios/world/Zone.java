package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Vector2D;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code Zone} class represents a grid of {@code Location} objects within the {@code World}.  Zones cannot be
 * accessed from other Zones.  Support for this feature is planned in a future release.
 * @author Caleb Brinkman
 */
public class Zone
{
	/** The Logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(Zone.class.getName());
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
	 * @param specialLocations Any special locations that should be set on zone creation.
	 */
	public Zone(int id, int xSize, int ySize, Location[] specialLocations) {
		this.id = id;
		this.xSize = xSize;
		this.ySize = ySize;

		locationGrid = new Location[xSize][ySize];
		LOGGER.log(Level.FINEST, "Constructing Locations.");
		constructLocations();
		LOGGER.log(Level.FINEST, "Adding Special Locations.");
		addSpecialLocations(specialLocations);
		LOGGER.log(Level.FINEST, "Calculating Location Adjacency.");
		setAdjacentLocations();
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
	 * Determine if the coordinates of the vector are within this Zones boundaries.
	 * @param vector2D The coordinates to check.
	 * @return Whether the coordinates of the vector are within this Zones boundaries.
	 */
	public boolean isInvalidLocation(Vector2D vector2D) {
		double x = vector2D.getXCoordinate();
		double y = vector2D.getYCoordinate();
		return !(x < 0 || y < 0 || x / Location.SIZE >= xSize || y / Location.SIZE >= ySize);
	}

	/**
	 * Get an area of location objects.
	 * @param centerCoordinates The center of the area to return.
	 * @param radius The radius of the area.
	 * @return An ArrayList containing all valid locations in the specified area.
	 */
	public ArrayList<Location> getLocationArea(Vector2D centerCoordinates, int radius) {
		ArrayList<Location> areaGrid = new ArrayList<>();
		Location center = getLocationForCoordinates(centerCoordinates);
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
	 * @param centerCoordinates The coordinates.
	 * @return The location at the specified coordinates.
	 */
	public Location getLocationForCoordinates(Vector2D centerCoordinates) {
		return getLocationForCoordinates(centerCoordinates.getXCoordinate(), centerCoordinates.getYCoordinate());
	}

	/**
	 * Get the location at the specified coordinates.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @return The location at the specified coordinates.
	 */
	protected Location getLocationForCoordinates(double x, double y) {
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
	public Collection<Location> castVisibilityCircle(Location center, int radius) {
		LinkedList<Location> locations = new LinkedList<>();
		int centerY = center.X_COORDINATE;
		int centerX = center.Y_COORDINATE;
		HashSet<Location> visibleLocations = new HashSet<>();

		LinkedList<Vector2D> circle = castCircle(centerX, centerY, radius);

		for(Vector2D vector2D : circle) {
			int x = (int) vector2D.getXCoordinate();
			int y = (int) vector2D.getYCoordinate();
			visibleLocations.addAll(castVisibilityRay(centerX, centerY, x, y));
		}

		locations.addAll(visibleLocations);
		return locations;
	}

	/**
	 * Use Bresenham's circle algorithm to find a circle of locations with the given center coordinates and radius.
	 * @param x0 The center x coordinate.
	 * @param y0 The center y coordinate.
	 * @param radius The radius of the circle.
	 * @return A list of locations containing the perimeter of the circle.
	 */
	protected LinkedList<Vector2D> castCircle(int x0, int y0, int radius) {
		int x = radius, y = 0;
		int radiusError = 1-x;
		LinkedList<Vector2D> circle = new LinkedList<>();
		while(x >= y)
		{
			circle.add(new Vector2D(x + x0, y + y0));
			circle.add(new Vector2D(y + x0, x + y0));
			circle.add(new Vector2D(-x + x0, y + y0));
			circle.add(new Vector2D(-y + x0, x + y0));
			circle.add(new Vector2D(-x + x0, -y + y0));
			circle.add(new Vector2D(-y + x0, -x + y0));
			circle.add(new Vector2D(x + x0, -y + y0));
			circle.add(new Vector2D(y + x0, -x + y0));
			y++;
			if (radiusError<0)
			{
				radiusError += 2 * y + 1;
			} else {
				x--;
				radiusError+= 2 * (y - x + 1);
			}
		}
		return circle;
	}

	/**
	 * This uses a modified version of Bresenham's Line Algorithm, available in its original form <a
	 * href=http://lifc.univ-fcomte.fr/~dedu/projects/bresenham/index.html>here.</a>  This algorithm works by casting a
	 * ray until a Location with a LocationProperty containing the property "blocksVision" set to "true".
	 * @param x1 The starting x location.
	 * @param y1 The starting y location.
	 * @param x2 The ending x location.
	 * @param y2 The ending y location.
	 * @return The ray cast from the given starting points to the given end points.
	 */
	@SuppressWarnings("SuspiciousNameCombination")
	protected Collection<Location> castVisibilityRay(int x1, int y1, int x2, int y2) {
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
						if (!addLocationToVisibilityRay(y, x - xStep, visibleRay)) break;
					} else
					{
						if (error + previousError > ddy)
						{
							if (!addLocationToVisibilityRay(y - yStep, x, visibleRay)) break;
						} else
						{
							if (!addLocationToVisibilityRay(y, x - xStep, visibleRay)) break;
							if (!addLocationToVisibilityRay(y - yStep, x, visibleRay)) break;
						}
					}
				}
				if (!addLocationToVisibilityRay(y, x, visibleRay)) break;
				previousError = error;
			}
		}
		return visibleRay;
	}

	/**
	 * Add the location at the given coordinates to the specified ray, returning true if the location was added, false
	 * if not.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param ray The ray.
	 * @return true if the location was added.
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean addLocationToVisibilityRay(int x, int y, List<Location> ray) {
		Location location = getLocationOnGrid(x, y);
		if (location == null || "true".equals(location.getLocationProperties().getProperty("blocksVision")))
		{
			return false;
		}
		ray.add(location);
		return true;
	}

	/** Initialize the locations in the zone. */
	private void constructLocations() {
		for (int x = 0; x < xSize; x++)
			for (int y = 0; y < ySize; y++)
				locationGrid[x][y] = new Location(x, y);
	}

	/**
	 * Establish the locations adjacent to one another.
	 */
	private void setAdjacentLocations() {
		for (int x = 0; x < xSize; x++)
			for (int y = 0; y < ySize; y++)
				locationGrid[x][y].setAdjacentLocations(this);
		for (int x = 0; x < xSize; x++)
			for (int y = 0; y < ySize; y++)
				locationGrid[x][y].setAdjacentWalkableLocations();
	}
}
