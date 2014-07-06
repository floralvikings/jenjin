package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;

/**
 * Represents an object that exists in the game world.
 * @author Caleb Brinkman
 */
public class WorldObject
{
	private final String name;
	private int zoneID;
	private int resourceID;
	private int id = Integer.MIN_VALUE;
	private Angle angle;
	private Location location;
	private Vector2D vector2D;
	private World world;

	public WorldObject(String name) {
		vector2D = new Vector2D(0, 0);
		this.name = name;
		angle = new Angle();
	}

	public Angle getAngle() { return angle; }

	public void setAngle(Angle angle) { this.angle = angle; }

	public Vector2D getVector2D() { return new Vector2D(vector2D); }

	public void setVector2D(Vector2D vector2D) {
		this.vector2D = new Vector2D(vector2D);

		if (world != null)
		{
			Location newLocation = world.getLocationForCoordinates(this.zoneID, this.vector2D);
			setLocation(newLocation);
		}
	}

	public int getResourceID() { return resourceID; }

	public void setResourceID(int resourceID) { this.resourceID = resourceID; }

	public int getId() { return id; }

	public void setId(int id) { this.id = id; }

	public Location getLocation() { return location; }

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof WorldObject)) return false;

		WorldObject that = (WorldObject) o;

		return id == that.id && name.equals(that.name);

	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + id;
		return result;
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

}
