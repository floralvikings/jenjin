package com.jenjinstudios.jgsf.world;

import java.util.TreeMap;

/**
 * Contains an Array of locations.  The Zone class will be used to apply properties to entire swathes of the world,
 * as well as for chat purposes.
 *
 * @author Caleb Brinkman
 */
public class Zone
{
	/** The size of the Zone's Location Grid. */
	public static final int SIZE = 5;
	/** The GameObjects contained in this Zone. */
	private final TreeMap<Integer, GameObject> gameObjects;
	/** The x coordinate of this Zone in the World zone grid. */
	private final int xCoordinate;
	/** The z coordinate of this Zone in the World zone grid. */
	private final int zCoordinate;
	/** The grid of locations. */
	private final Location[][] locations;

	/**
	 * Create a Zone at the given location in the world's zone-grid.
	 *
	 * @param x The x coordinate.
	 * @param z The z coordinate.
	 */
	public Zone(int x, int z)
	{
		xCoordinate = x;
		zCoordinate = z;
		gameObjects = new TreeMap<>();
		locations = new Location[SIZE][SIZE];
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++)
				locations[i][j] = new Location(x + (xCoordinate * SIZE), z + (zCoordinate * SIZE));
	}

	/**
	 * Add an object to this zone.
	 *
	 * @param o The object to add.
	 */
	public void addObject(GameObject o)
	{
		if (o.getZone() != null) o.getZone().removeObject(o.getId());
		gameObjects.put(o.getId(), o);
		o.setZone(this);
	}

	/**
	 * Get a location from this zone's grid.
	 *
	 * @param x The location's x position on the grid.
	 * @param z The location's z position on the grid.
	 * @return The location at the given position.
	 */
	public Location getLocation(int x, int z)
	{
		return locations[x][z];
	}

	/**
	 * Remove an object from this zone.
	 *
	 * @param ID The ID of the object to be removed.
	 */
	public void removeObject(int ID)
	{
		gameObjects.remove(ID);
	}

	/**
	 * Determine whether an object is in this zone.
	 *
	 * @param ID The ID of the object to look for.
	 * @return true if the object is here, false otherwise.
	 */
	public boolean containsObject(int ID)
	{
		return gameObjects.containsKey(ID);
	}

	/**
	 * Get the x coordinate of this zone on the world grid.
	 *
	 * @return The x coordinate of this zone on the world grid.
	 */
	public int getXCoordinate()
	{
		return xCoordinate;
	}

	/**
	 * Get the z coordinate of this zone on the world grid.
	 *
	 * @return The z coordinate of this zone on the world grid.
	 */
	public int getZCoordinate()
	{
		return zCoordinate;
	}
}
