package com.jenjinstudios.world;

import java.util.ArrayList;

/**
 * The shallow, client-side copy of the game world.
 *
 * @author Caleb Brinkman
 */
public class ShallowWorld
{
	/** The zones in the world. */
	private final ZoneInfo[][] zones;
	/** The objects in the world. */
	private final ArrayList<GameObjectInfo> gameObjects;

	/**
	 * Construct a new ShallowWorld with the given size.
	 *
	 * @param size The size of the world, in zones.
	 */
	public ShallowWorld(int size)
	{
		/* The size of the world, in zones. */
		zones = new ZoneInfo[size][size];
		gameObjects = new ArrayList<>();
		for (int x = 0; x < size; x++)
			for (int z = 0; z < size; z++)
				zones[x][z] = new ZoneInfo(x, z);
	}

	/**
	 * Adds an object to the game world, as assigns it an ID.
	 *
	 * @param object The object to add.
	 */
	public void addObject(GameObjectInfo object)
	{
		object.setWorld(this);
		object.setId(gameObjects.size());
		gameObjects.add(object);
		double xCoord = object.getVector2D().getXCoordinate();
		double zCoord = object.getVector2D().getZCoordinate();
		getZoneForCoordinates(xCoord, zCoord).addObject(object);
	}

	/**
	 * Get the zone in which the specified coordinates land.
	 *
	 * @param x The x coordinate to test.
	 * @param z The z coordinate to test.
	 * @return The zone at the specified coordinates.
	 */
	public ZoneInfo getZoneForCoordinates(double x, double z)
	{
		return zones[(int) x / ZoneInfo.SIZE][(int) z / ZoneInfo.SIZE];
	}

	/**
	 * Get the location in which the specified coodinates land.
	 *
	 * @param x The x coordinate to test.
	 * @param z The z coordinate to test.
	 * @return The location in which the specified coordinates land.
	 */
	public LocationInfo getLocationForCoordinates(float x, float z)
	{
		return getZoneForCoordinates(x, z).getLocation((int) x % ZoneInfo.SIZE, (int) z % ZoneInfo.SIZE);
	}

	/** Update all the objects in the game world. */
	public void update()
	{
		for (GameObjectInfo o : gameObjects)
			o.update();
	}

	/** Reset any values that have to be reset after the update. */
	public void reset()
	{

	}
}
