package com.jenjinstudios.world;

import com.jenjinstudios.math.Vector2D;

/**
 * Represents an object that exists in the game world.
 *
 * @author Caleb Brinkman
 */
public class WorldObject
{
	/** The vector2D in the world at which the object is located. */
	private Vector2D vector2D;
	/** The direction in which this object is facing. */
	private float direction;
	/** The ID number of this object. */
	private int id = Integer.MIN_VALUE;
	/** The world in which this object exists. */
	private World world;
	/** The location in which this object is residing. */
	private Location location;
	/** The name of this actor. */
	private final String name;
	/** The default name of this actor. */
	public static final String DEFAULT_NAME = "Object";

	/** Construct a new WorldObject. */
	public WorldObject()
	{
		this(DEFAULT_NAME);
	}

	/**
	 * Construct a new WorldObject.
	 *
	 * @param name The name of this object.
	 */
	public WorldObject(String name)
	{
		vector2D = new Vector2D(0, 0);
		this.name = name;
	}

	/**
	 * Get the direction in which this object is facing, in radians.
	 *
	 * @return The direction in which this object is facing.
	 */
	public float getDirection()
	{
		return direction;
	}

	/**
	 * Set the direction in which this object is facing.
	 *
	 * @param direction The new direction for this object to face.
	 */
	public void setDirection(float direction)
	{
		this.direction = direction;
	}

	/**
	 * Get this object's current position.
	 *
	 * @return This object's current position.
	 */
	public Vector2D getVector2D()
	{
		return new Vector2D(vector2D);
	}

	/**
	 * Set this object's current position.
	 *
	 * @param vector2D The new position.
	 */
	public void setVector2D(Vector2D vector2D)
	{
		this.vector2D = new Vector2D(vector2D);
		Location oldLocation = location;
		// TODO This call is somewhat intensive, and will probably need some optimization.
		// We can probably just store the "edge" of the current location, and check against that.  It depends on
		// how intensive the calculation of the location within the world turns out to be.
		if (world != null)
		{
			location = world.getLocationForCoordinates(this.vector2D);
			if (oldLocation != location)
			{
				oldLocation.removeObject(this);
				location.addObject(this);
			}
		}
	}

	/**
	 * Set this object' current position.
	 *
	 * @param x The new x coordinate.
	 * @param z The new z coordinate.
	 */
	public void setVector2D(double x, double z)
	{
		setVector2D(new Vector2D(x, z));
	}

	/**
	 * Get this object's ID number.
	 *
	 * @return This object's ID number.
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * Set this object's ID number if it has not already been set.
	 *
	 * @param id The new ID number.
	 */
	public void setId(int id)
	{
		if (this.id != Integer.MIN_VALUE)
			throw new IllegalArgumentException("This object's ID has already been set.");
		this.id = id;
	}

	/**
	 * Get this object's location.
	 *
	 * @return This object's location.
	 */
	public Location getLocation()
	{
		return location;
	}

	/**
	 * Get the world in which this object is located.
	 *
	 * @return the world in which this object is located.
	 */
	public World getWorld()
	{
		return world;
	}

	/**
	 * Set the world in which this object is located, if it hasn't already been set.
	 *
	 * @param world The new world.
	 */
	public void setWorld(World world)
	{
		if (this.world != null)
			throw new IllegalArgumentException("The world has already been set for this object.");
		this.world = world;
		location = world.getLocationForCoordinates(this.vector2D);
		location.addObject(this);
	}

	/**
	 * Get the name of this actor.
	 *
	 * @return The name of this actor.
	 */
	public String getName()
	{
		return name;
	}

	/** Update this WorldObject. */
	public void update()
	{

	}
}
