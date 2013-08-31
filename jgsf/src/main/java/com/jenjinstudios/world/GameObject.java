package com.jenjinstudios.world;

import com.jenjinstudios.math.Vector2D;

/**
 * Represents an object that exists in the game world.
 *
 * @author Caleb Brinkman
 */
public class GameObject
{
	/** The vector2D in the world at which the object is located. */
	private Vector2D vector2D;
	/** The zone in which this object is located. */
	private Zone zone;
	/** The direction in which this object is facing. */
	private float direction;
	/** The ID number of this object. */
	private int id = Integer.MIN_VALUE;
	/** The world in which this object exists. */
	private World world;

	/** Construct a new GameObject. */
	public GameObject()
	{
		vector2D = new Vector2D(0, 0);
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
	}

	/**
	 * Set this object' current position.
	 *
	 * @param x The new x coordinate.
	 * @param z The new z coordinate.
	 */
	public void setVector2D(double x, double z)
	{
		vector2D.setXCoordinate(x);
		vector2D.setZCoordinate(z);
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
	 * Get this object's zone.
	 *
	 * @return this object's zone.
	 */
	public Zone getZone()
	{
		return zone;
	}

	/**
	 * Set this object's zone.
	 *
	 * @param zone The new zone.
	 */
	public void setZone(Zone zone)
	{
		this.zone = zone;
	}

	/**
	 * Get this object's location.
	 *
	 * @return This object's location.
	 */
	public Location getLocation()
	{
		return world.getLocationForCoordinates(vector2D);
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
	}

	/** Update this GameObject. */
	public void update()
	{

	}
}
