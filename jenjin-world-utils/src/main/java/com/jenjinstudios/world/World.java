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
	private final Zone[] zones;
	/** The GameObjects contained in the world. */
	private final ArrayList<WorldObject> worldObjects;
	/** The number of objects currently in the world. */
	private int objectCount;

	/** Construct a new World. */
	public World() {
		zones = new Zone[1];
		zones[0] = new Zone(0, DEFAULT_SIZE, DEFAULT_SIZE, new Location[] { });
		worldObjects = new ArrayList<>();
	}

	/**
	 * Construct a new world with the specified Zone array.
	 * @param zones The zones used to create the world.
	 */
	public World(Zone[] zones) {
		this.zones = zones;
		worldObjects = new ArrayList<>();
	}

	/**
	 * Add an object to the world.
	 * @param object The object to add.
	 * @throws InvalidLocationException If an object is attempted to be added with an invalid location.
	 */
	public void addObject(WorldObject object) throws InvalidLocationException {
		this.addObject(object, worldObjects.size());
	}

	/**
	 * Add an object with the specified ID.
	 * @param object The object to add.
	 * @param id The id.
	 * @throws InvalidLocationException If the object is to be added in an invalid location.
	 */
	public void addObject(WorldObject object, int id) throws InvalidLocationException {
		if (object == null)
			throw new IllegalArgumentException("addObject(WorldObject obj) argument 0 not allowed to be null!");

		worldObjects.ensureCapacity(id + 1);

		while(worldObjects.size() <= id)
		{
			worldObjects.add(null);
		}

		if (worldObjects.get(id) != null)
			throw new IllegalArgumentException("addObject(WorldObject obj) argument 1 not allowed to be an occupied id: " + id);

		object.setWorld(this);
		object.setVector2D(object.getVector2D());
		synchronized (worldObjects)
		{
			object.setId(id);
			worldObjects.add(id, object);
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
	 * Remove the object with the specified id.
	 * @param id The id.
	 */
	public void removeObject(int id) { this.removeObject(worldObjects.get(id)); }

	/**
	 * Get the location from the zone grid that contains the specified vector2D.
	 * @param zoneID The ID of the zone in which to look for the location.
	 * @param vector2D The vector2D.
	 * @return The location that contains the specified vector2D.
	 */
	public Location getLocationForCoordinates(int zoneID, Vector2D vector2D) {
		if (!isValidLocation(zoneID, vector2D))
			return null;
		return zones[zoneID].getLocationForCoordinates(vector2D);
	}

	/**
	 * Determine whether the given vector lies within a valid location.
	 * @param zoneID The ID of the zone in which to look for the location.
	 * @param vector2D The vector.
	 * @return Whether the vector lies within a valid location.
	 */
	public boolean isValidLocation(int zoneID, Vector2D vector2D) {
		Zone zone = zones[zoneID];
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
		Zone z = zones[zoneID];
		if (z == null) { return null; }
		return z.getLocationArea(center, radius);
	}

	/**
	 * Get the number of objects currently in the world.
	 * @return The number of objects currently in the world.
	 */
	public int getObjectCount() { return objectCount; }

	/**
	 * Get an object by its id.
	 * @param id The id.
	 * @return The object with the specified id.
	 */
	public WorldObject getObject(int id) { return worldObjects.get(id); }

	/**
	 * Get the zone array.
	 * @return The array of zone objects contained in this world.
	 */
	public Zone[] getZones() {
		return zones;
	}

	/**
	 * Reset the world to it's original state.
	 */
	public void purgeObjects() {
		synchronized (worldObjects)
		{
			ArrayList<WorldObject> copy = new ArrayList<>(worldObjects);
			for(WorldObject obj : copy)
			{
				removeObject(obj);
			}
		}
	}
}
