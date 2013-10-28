package com.jenjinstudios.world;

import com.jenjinstudios.math.Vector2D;

import java.util.ArrayList;

/**
 * Contains all the Zones, Locations and GameObjects.
 *
 * @author Caleb Brinkman
 */
public class World
{
	/** The size of the world's location grid. */
	public final int SIZE = 50;
	/** The grid of locations in the game world. */
	private final Location[][] locationGrid;
	/** The GameObjects contained in the world. */
	private final ArrayList<WorldObject> worldObjects;
	/** The number of objects currently in the world. */
	private int objectCount;

	/** Construct a new World. */
	public World()
	{
		locationGrid = new Location[SIZE][SIZE];
		worldObjects = new ArrayList<>();
		for (int x = 0; x < SIZE; x++)
			for (int z = 0; z < SIZE; z++)
				locationGrid[x][z] = new Location(x, z);
	}

	/**
	 * Add an object to the world.
	 *
	 * @param object The object to add.
	 *
	 * @throws InvalidLocationException If an object is attempted to be added with an invalid location.
	 */
	public void addObject(WorldObject object) throws InvalidLocationException
	{
		if (object == null)
			throw new IllegalArgumentException("addObject(WorldObject obj) argument 0 not allowed to be null!");
		object.setWorld(this);
		object.setVector2D(object.getVector2D());
		synchronized (worldObjects)
		{
			object.setId(worldObjects.size());
			worldObjects.add(object);
		}
		objectCount++;
	}

	/**
	 * Remove an object from the world.  Specifically, sets the index of the given object in the world's array to null.
	 *
	 * @param object The object to remove.
	 */
	public void removeObject(WorldObject object)
	{
		synchronized (worldObjects)
		{
			worldObjects.set(object.getId(), null);
			object.getLocation().removeObject(object);
		}
		objectCount--;
	}

	/** Update all objects in the world. */
	public void update()
	{
		synchronized (worldObjects)
		{
			for (WorldObject o : worldObjects)
				if (o != null)
					o.update();
		}
	}

	/**
	 * Get an area of location objects.
	 *
	 * @param center The center of the area to return.
	 * @param radius The radius of the area.
	 *
	 * @return An ArrayList containing all valid locations in the specified area.
	 */
	public ArrayList<Location> getLocationArea(Location center, int radius)
	{
		// Make the array list with an initial size of area
		ArrayList<Location> areaGrid = new ArrayList<>();
		// Get the top-right corner of the grid.
		int xStart = center.X_COORDINATE - radius;
		int zStart = center.Z_COORDINATE - radius;
		int xEnd = center.X_COORDINATE + radius;
		int zEnd = center.Z_COORDINATE + radius;

		for (int x = xStart; x < xEnd; x++)
			for (int z = zStart; z < zEnd; z++)
			{
				try
				{
					areaGrid.add(locationGrid[x][z]);
				} catch (ArrayIndexOutOfBoundsException ignored)
				{
					// Just means we're near the end of the world =)
				}
			}


		return areaGrid;
	}

	/**
	 * Get the number of objects currently in the world.
	 *
	 * @return The number of objects currently in the world.
	 */
	public int getObjectCount()
	{
		return objectCount;
	}

	/**
	 * Indicate whether the specified vector is in a valid location or not.
	 *
	 * @param vector2D The vector to test.
	 *
	 * @return Whether the vector lands in a vlid location.
	 */
	public boolean isValidLocation(Vector2D vector2D)
	{
		int maxCoord = SIZE * Location.SIZE;

		boolean tooSmall = (vector2D.getXCoordinate() < 0) || (vector2D.getZCoordinate() < 0);
		boolean tooBig = (vector2D.getXCoordinate() >= maxCoord) || (vector2D.getZCoordinate() >= maxCoord);

		return !(tooBig || tooSmall);
	}

	/**
	 * Determine whether the location at the specified vector is valid and open.
	 *
	 * @param vector2D The vector.
	 *
	 * @return Whether the location containing the vector exists and is open.
	 */
	public boolean isOpenLocation(Vector2D vector2D)
	{
		Location loc = getLocationForCoordinates(vector2D);
		return (loc != null) && (loc.getProperty() != Location.LocationProperty.CLOSED);

	}

	/**
	 * Get the location from the zone grid that contains the specified vector2D.
	 *
	 * @param vector2D The vector2D
	 *
	 * @return The location that contains the specified vector2D.
	 */
	public Location getLocationForCoordinates(Vector2D vector2D)
	{
		if (!isValidLocation(vector2D))
			return null;
		double x = vector2D.getXCoordinate();
		double z = vector2D.getZCoordinate();
		return locationGrid[(int) x / Location.SIZE][(int) z / Location.SIZE];
	}

}
