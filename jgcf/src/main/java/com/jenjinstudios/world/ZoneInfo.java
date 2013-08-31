package com.jenjinstudios.world;

import java.util.TreeMap;

/**
 * The {@code ZoneInfo} class is used to construc a copy of the server zone.  This is used for positioning and property
 * functions.
 *
 * @author Caleb Brinkman
 */
public class ZoneInfo
{
	//TODO Zone should get size info from server.
	/** The size of zones. */
	public static final int SIZE = 5;
	/** The collection of GameObjects in the zone. */
	private final TreeMap<Integer, GameObjectInfo> gameObjects;
	/** The x coordinate of this zone. */
	private final int xCoordinate;
	/** The z coordinate of this zone. */
	private final int zCoordinate;
	/** The grid of locations contained in this zone. */
	private final LocationInfo[][] locations;

	/**
	 * Construct a new ZoneInfo for the given coordinates.
	 *
	 * @param x The zone's x coordinate.
	 * @param z The zone's z coordinate
	 */
	public ZoneInfo(int x, int z)
	{
		xCoordinate = x;
		zCoordinate = z;
		locations = new LocationInfo[SIZE][SIZE];
		gameObjects = new TreeMap<>();
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++)
				locations[i][j] = new LocationInfo(x + (xCoordinate * SIZE), z + (zCoordinate * SIZE));
	}

	/**
	 * Get the x coordinate of this zone.
	 *
	 * @return The x coordinate of this zone.
	 */
	public int getxCoordinate()
	{
		return xCoordinate;
	}

	/**
	 * Get the z coordinate of this zone.
	 *
	 * @return The z coordinate of this zone.
	 */
	public int getzCoordinate()
	{
		return zCoordinate;
	}

	/**
	 * Get the location at the given position within this zone.
	 *
	 * @param x The x coordinate, relative to the zone, of the location.
	 * @param z The z coordinate, relative to the zone, of the location.
	 * @return The location at the specified coordinates.
	 */
	public LocationInfo getLocation(int x, int z)
	{
		return locations[x][z];
	}

	/**
	 * Remove the object with the specified ID.
	 *
	 * @param ID The ID of the object to be removed.
	 */
	public void removeObject(int ID)
	{
		gameObjects.remove(ID);
	}

	/**
	 * Add the specified object to the zone.
	 *
	 * @param o The object to be added.
	 */
	public void addObject(GameObjectInfo o)
	{
		if (o.getZone() != null) o.getZone().removeObject(o.getId());
		gameObjects.put(o.getId(), o);
		o.setZone(this);
	}

	/**
	 * Determine whether this zoneinfo contains the gameobject with the specified ID.
	 *
	 * @param id The id of the gameobject.
	 * @return true if the zone contains the object with the specified id.
	 */
	public boolean containsObject(int id)
	{
		return gameObjects.containsKey(id);
	}
}
