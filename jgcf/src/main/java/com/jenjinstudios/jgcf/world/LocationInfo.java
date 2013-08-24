package com.jenjinstudios.jgcf.world;

/**
 * The {@code LocationInfo} class is used to construct location properties as received from the server.
 *
 * @author Caleb Brinkman
 */
public class LocationInfo
{
	/** The property for this location. */
	private Property locationProperty;
	/** The x coordinate for this location . */
	private final int xCoordinate;
	/** The z coordinate for this location. */
	private final int zCoordinate;

	/**
	 * Construct a new LocationInfo object.
	 *
	 * @param x The x coordinate of the new location.
	 * @param z The z coordinate of the new location.
	 */
	public LocationInfo(int x, int z)
	{
		xCoordinate = x;
		zCoordinate = z;
		locationProperty = Property.OPEN;
	}

	/**
	 * Get the property of this location.
	 *
	 * @return The property of this location.
	 */
	public Property getLocationProperty()
	{
		return locationProperty;
	}

	/**
	 * Set the property of this location.
	 *
	 * @param locationProperty The new location property.
	 */
	public void setLocationProperty(Property locationProperty)
	{
		this.locationProperty = locationProperty;
	}

	/**
	 * Get the x coordinate of this location.
	 *
	 * @return The x coordinate of this location.
	 */
	public int getxCoordinate()
	{
		return xCoordinate;
	}

	/**
	 * Get the z coordinate of this location.
	 *
	 * @return The z coordinate of this location.
	 */
	public int getzCoordinate()
	{
		return zCoordinate;
	}

	/** Enumerate location properties. */
	public enum Property
	{
		/** Specifies that the location is open. */
		OPEN,
		/** Specifies that the location is closed. */
		CLOSED
	}

}
