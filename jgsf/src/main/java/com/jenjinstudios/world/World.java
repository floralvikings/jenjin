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
	public final int SIZE = 3;
	/** The grid of locations in the game world. */
	private final Location[][] locationGrid;
	/** The GameObjects contained in the world. */
	private final ArrayList<WorldObject> worldObjects;

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
	 */
	public void addObject(WorldObject object)
	{
		if (object == null)
			throw new IllegalArgumentException("addObject(WorldObject obj) argument 0 not allowed to be null!");
		object.setWorld(this);
		object.setId(worldObjects.size());
		worldObjects.add(object);
	}

	/**
	 * Get the location that contains the specified coordinates.
	 *
	 * @param x The x coordinate.
	 * @param z The z coordinate
	 * @return The location that contains the specified coordinates.
	 */
	public Location getLocationForCoordinates(double x, double z)
	{
		return locationGrid[(int) x / Location.SIZE][(int) z / Location.SIZE];
	}

	/**
	 * Get the location from the zone grid that contains the specified vector2D.
	 *
	 * @param vector2D The vector2D
	 * @return The location that contains the specified vector2D.
	 */
	public Location getLocationForCoordinates(Vector2D vector2D)
	{
		return getLocationForCoordinates(vector2D.getXCoordinate(), vector2D.getZCoordinate());
	}

	/** Update all objects in the world. */
	public void update()
	{
		for (WorldObject o : worldObjects)
			o.update();
	}
}
