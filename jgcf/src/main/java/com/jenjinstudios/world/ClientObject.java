package com.jenjinstudios.world;

import com.jenjinstudios.math.Vector2D;

/**
 * The {@code ClientObject} class is used to represent a server-side {@code WorldObject} on the client.
 *
 * @author Caleb Brinkman
 */
public class ClientObject
{
	/** The vector2D in the world at which the object is located. */
	private Vector2D vector2D;
	/** The direction in which this object is facing. */
	private float direction;
	/** The ID number of this object. */
	private int id = Integer.MIN_VALUE;

	/**
	 * Construct a new WorldObject.
	 *
	 * @param id The ID of the object.
	 */
	public ClientObject(int id)
	{
		vector2D = new Vector2D(0, 0);
		setId(id);
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

	/** Update this ClientObject. */
	public void update()
	{

	}
}
