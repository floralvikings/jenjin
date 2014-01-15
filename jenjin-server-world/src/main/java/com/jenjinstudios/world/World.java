package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Vector2D;

import java.util.ArrayList;

/**
 * Contains all the Zones, Locations and GameObjects.
 * @author Caleb Brinkman
 */
public class World
{
	/** The size of the world's location grid. */
	public final int DEFAULT_SIZE = 50;
	/** The list of in-world Zones. */
	private final ArrayList<Zone> zones;
	/** The GameObjects contained in the world. */
	private final ArrayList<WorldObject> worldObjects;
	/** The number of objects currently in the world. */
	private int objectCount;

	/** Construct a new World. */
	public World() {
		zones = new ArrayList<>();
		zones.add(new Zone(zones.size(), DEFAULT_SIZE, DEFAULT_SIZE));
		worldObjects = new ArrayList<>();
	}

	/**
	 * Add an object to the world.
	 * @param object The object to add.
	 * @throws InvalidLocationException If an object is attempted to be added with an invalid location.
	 */
	public void addObject(WorldObject object) throws InvalidLocationException {
		if (object == null)
			throw new IllegalArgumentException("addObject(WorldObject obj) argument 0 not allowed to be null!");
		object.setWorld(this);
		object.setVector2D(object.getVector2D());
		synchronized (worldObjects)
		{
			object.setId(worldObjects.size());
			worldObjects.add(object);
		}
		objectCount++;
	}

	/**
	 * Remove an object from the world.  Specifically, sets the index of the given object in the world's array to null.
	 * @param object The object to remove.
	 */
	public void removeObject(WorldObject object) {
		synchronized (worldObjects)
		{
			worldObjects.set(object.getId(), null);
			object.getLocation().removeObject(object);
		}
		objectCount--;
	}

	/**
	 * Get the location from the zone grid that contains the specified vector2D.
	 * @param zoneID The ID of the zone in which to look for the location.
	 * @param vector2D The vector2D.
	 * @return The location that contains the specified vector2D.
	 */
	public Location getLocationForCoordinates(int zoneID, Vector2D vector2D) {
		if (!isValidLocation(zoneID, vector2D))
			return null;
		return zones.get(zoneID).getLocation(vector2D);
	}

	/**
	 * Determine whether the given vector lies within a valid location.
	 * @param zoneID The ID of the zone in which to look for the location.
	 * @param vector2D The vector.
	 * @return Whether the vector lies within a valid location.
	 */
	public boolean isValidLocation(int zoneID, Vector2D vector2D) {
		Zone zone = zones.get(zoneID);
		return !(zone != null && zone.isValidLocation(vector2D));
	}

	/** Update all objects in the world. */
	public void update() {
		synchronized (worldObjects)
		{
			for (WorldObject o : worldObjects)
				if (o != null)
					o.update();
		}
	}

	/**
	 * Get an area of location objects.
	 * @param zoneID The ID of the zone in which to get the location area.
	 * @param center The center of the area to return.
	 * @param radius The radius of the area.
	 * @return An ArrayList containing all valid locations in the specified area.
	 */
	public ArrayList<Location> getLocationArea(int zoneID, Vector2D center, int radius) {
		Zone z = zones.get(zoneID);
		if (z == null) { return null; }
		return z.getLocationArea(center, radius);
	}

	/**
	 * Get the number of objects currently in the world.
	 * @return The number of objects currently in the world.
	 */
	public int getObjectCount() {
		return objectCount;
	}
}
