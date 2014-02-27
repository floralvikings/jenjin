package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Vector2D;

/**
 * Represents an object that exists in the game world.
 * @author Caleb Brinkman
 */
public class WorldObject
{
	/** The name of this actor. */
	private final String name;
	/** The zoneID in which this actor is located. */
	private int zoneID;
	/** The vector2D in the world at which the object is located. */
	private Vector2D vector2D;
	/** The relativeAngle in which this object is facing. */
	private double direction;
	/** The ID number of this object. */
	private int id = Integer.MIN_VALUE;
	/** The world in which this object exists. */
	private World world;
	/** The location in which this object is residing. */
	private Location location;
	/** The resource ID number for this object. */
	private int resourceID;


	/**
	 * Construct a new WorldObject.
	 * @param name The name of this object.
	 */
	public WorldObject(String name) {
		vector2D = new Vector2D(0, 0);
		this.name = name;
	}

	/**
	 * Get the relativeAngle in which this object is facing, in radians.
	 * @return The relativeAngle in which this object is facing.
	 */
	public double getDirection() { return direction; }

	/**
	 * Set the relativeAngle in which this object is facing.
	 * @param direction The new relativeAngle for this object to face.
	 */
	public void setDirection(double direction) { this.direction = direction; }

	/**
	 * Get this object's current position.
	 * @return This object's current position.
	 */
	public Vector2D getVector2D() { return new Vector2D(vector2D); }

	/**
	 * Set this object's current position.
	 * @param vector2D The new position.
	 */
	public void setVector2D(Vector2D vector2D) {
		this.vector2D = new Vector2D(vector2D);

		if (world != null)
		{
			Location newLocation = world.getLocationForCoordinates(this.zoneID, this.vector2D);
			setLocation(newLocation);
		}
	}

	/**
	 * Get the resourceID for this object.
	 * @return The resourceID for this object.
	 */
	public int getResourceID() { return resourceID; }

	/**
	 * Set the resourceID for this object.
	 * @param resourceID The resourceID for this object.
	 */
	public void setResourceID(int resourceID) { this.resourceID = resourceID; }

	/**
	 * Get this object's ID number.
	 * @return This object's ID number.
	 */
	public int getId() { return id; }

	/**
	 * Set this object's ID number if it has not already been set.
	 * @param id The new ID number.
	 */
	public void setId(int id) { this.id = id; }

	/**
	 * Get this object's location.
	 * @return This object's location.
	 */
	public Location getLocation() { return location; }

	/**
	 * Set this objects new location.
	 * @param newLocation The new location.
	 */
	protected void setLocation(Location newLocation) {
		Location oldLocation = location;
		location = newLocation;
		if (oldLocation != location && oldLocation != null)
		{
			oldLocation.removeObject(this);
		}
		if (location != null)
		{
			location.addObject(this);
		}
	}

	/**
	 * Get the world in which this object is located.
	 * @return the world in which this object is located.
	 */
	public World getWorld() { return world; }

	/**
	 * Set the world in which this object is located, if it hasn't already been set.
	 * @param world The new world.
	 */
	public void setWorld(World world) {
		if (this.world != null)
			throw new IllegalArgumentException("The world has already been set for this object.");
		this.world = world;
		setLocation(world.getLocationForCoordinates(this.zoneID, this.vector2D));
	}

	/**
	 * Get the name of this actor.
	 * @return The name of this actor.
	 */
	public String getName() { return name; }

	/** Set up this WorldObject before updating. */
	public void setUp() { }

	/** Update this WorldObject. */
	public void update() { }

	/** Reset this WorldObject after updating. */
	public void reset() { }

	public String toString() { return name + ": " + id; }

	/**
	 * Get the id number of the zone in which this player is located.
	 * @return The id number of the zone in which this player is located.
	 */
	public int getZoneID() { return zoneID; }

	/**
	 * Set the zone id in which this player is located.
	 * @param zoneID The id of the new zone.
	 */
	public void setZoneID(int zoneID) { this.zoneID = zoneID; }

	/**
	 * Set the object's vector based on coordinates.
	 * @param xCoordinate The x coordinate.
	 * @param yCoordinate The y coordinate.
	 */
	public void setVector2D(double xCoordinate, double yCoordinate) {
		this.setVector2D(new Vector2D(xCoordinate, yCoordinate));
	}
}
