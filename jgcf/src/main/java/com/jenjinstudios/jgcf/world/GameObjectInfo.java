package com.jenjinstudios.jgcf.world;

/**
 * The {@code GameObjectInfo} class is used to manage information about game object received from the server.  This
 * information is all that is necessary to replicate the server-side state of the object with the same ID.  Changes
 * made to this object will never affect the server version.
 *
 * @author Caleb Brinkman
 */
public class GameObjectInfo
{
	/** The ID of the GameObject represented by this object. */
	private int id;
	/** The representation of the zone in which this object exists. */
	private ZoneInfo zone;
	/** The representation of the world in which this object exists. */
	private ShallowWorld world;
	/** The x coordinate of this object. */
	private float xCoordinate;
	/** The z coordinate of this object. */
	private float zCoordinate;
	/** The direction in which this object is facing. */
	private float direction;

	/**
	 * Get the direction in which this object is facing.
	 *
	 * @return The direction, in radians, in which this object is facing.
	 */
	public float getDirection()
	{
		return direction;
	}

	/**
	 * Set the direction for this object to face.
	 *
	 * @param direction The new direction of this object, in radians.
	 */
	public void setDirection(float direction)
	{
		this.direction = direction;
	}

	/**
	 * Get the z coordinate of this object.
	 *
	 * @return The z coordinate of this object.
	 */
	public float getzCoordinate()
	{
		return zCoordinate;
	}

	/**
	 * Set the z coordinate of this object.
	 *
	 * @param zCoordinate The new z coordinate.
	 */
	public void setzCoordinate(float zCoordinate)
	{
		this.zCoordinate = zCoordinate;
	}

	/**
	 * Get the x coordinate of this object.
	 *
	 * @return The x coordinate of this object.
	 */
	public float getxCoordinate()
	{
		return xCoordinate;
	}

	/**
	 * Set the x coordinate of this object.
	 *
	 * @param xCoordinate The new x coordinate.
	 */
	public void setxCoordinate(float xCoordinate)
	{
		this.xCoordinate = xCoordinate;
	}

	/**
	 * Get the zone in which this object exists.
	 *
	 * @return The zone in which this object exists.
	 */
	public ZoneInfo getZone()
	{
		return zone;
	}

	/**
	 * Set the zone for this object.
	 *
	 * @param zone The new zone.
	 */
	public void setZone(ZoneInfo zone)
	{
		this.zone = zone;
	}

	/**
	 * Get the world in which this object exists.
	 *
	 * @return The world in which this object exists.
	 */
	public ShallowWorld getWorld()
	{
		return world;
	}

	/**
	 * Set the world for this objeect.
	 *
	 * @param world The new world for this object.
	 */
	public void setWorld(ShallowWorld world)
	{
		this.world = world;
	}

	/**
	 * Get the ID for this object.
	 *
	 * @return The ID of the object.
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * Set the ID for this object.
	 *
	 * @param id The ID for this object.
	 */
	public void setId(int id)
	{
		this.id = id;
	}
}
