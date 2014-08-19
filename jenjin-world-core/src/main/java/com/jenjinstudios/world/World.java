package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Dimension2D;
import com.jenjinstudios.world.math.Vector2D;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * Contains all the Zones, Locations and GameObjects.
 * @author Caleb Brinkman
 */
public class World
{
	private static final int DEFAULT_SIZE = 50;
	/** The list of in-world Zones. */
	private final TreeMap<Integer, Zone> zones;
	/** The GameObjects contained in the world. */
	private final WorldObjectMap worldObjects;
	private final LinkedList<WorldObject> scheduledForRemoval;
	private final LinkedList<WorldObject> scheduledForAddition;
	private final LinkedList<WorldObject> scheduledForOverwrite;
	/** The time at which the most recent update completed. */
	private long lastUpdateCompleted;
	/** The start time of the most recent update. */
	private long lastUpdateStarted;

	/** Construct a new World. */
	public World() {
		this(new Zone(0, new Dimension2D(DEFAULT_SIZE, DEFAULT_SIZE)));
	}

	/**
	 * Construct a new world with the specified Zone array.
	 * @param zones The zones used to create the world.
	 */
	public World(Zone... zones) {
		this.zones = new TreeMap<>();
		for (Zone z : zones)
		{
			this.zones.put(z.id, z);
		}
		worldObjects = new WorldObjectMap();
		lastUpdateCompleted = lastUpdateStarted = System.currentTimeMillis();
		scheduledForRemoval = new LinkedList<>();
		scheduledForAddition = new LinkedList<>();
		scheduledForOverwrite = new LinkedList<>();
	}

	/**
	 * Add an object to the world.
	 * @param object The object to add.
	 */
	public void addObject(WorldObject object) {
		int id = worldObjects.getAvailableId();
		this.addObject(object, id);
	}

	/**
	 * Add an object with the specified ID.
	 * @param object The object to add.
	 * @param id The id.
	 */
	public void addObject(WorldObject object, int id) {
		if (object == null)
			throw new IllegalArgumentException("addObject(WorldObject obj) argument 0 not allowed to be null!");

		if (worldObjects.get(id) != null)
			throw new IllegalArgumentException("addObject(WorldObject obj) not allowed to be an occupied id: "
				  + id + ".  Existing object: " + worldObjects.get(id));

		object.setId(id);
		object.setWorld(this);
		object.setVector2D(object.getVector2D());
		synchronized (worldObjects)
		{
			worldObjects.put(id, object);
		}
	}

	/**
	 * Get the location from the zone grid that contains the specified vector2D.
	 * @param zoneID The ID of the zone in which to look for the location.
	 * @param vector2D The vector2D.
	 * @return The location that contains the specified vector2D.
	 */
	public Location getLocationForCoordinates(int zoneID, Vector2D vector2D) {
		return zones.get(zoneID).getLocationForCoordinates(vector2D);
	}

	/** Update all objects in the world. */
	public void update() {
		lastUpdateStarted = System.currentTimeMillis();
		synchronized (worldObjects)
		{
			removeScheduledObjects();
			addScheduledObjects();
			overwriteScheduledObjects();
			setUpObjects();
			updateObjects();
			resetObjects();
		}
		lastUpdateCompleted = System.currentTimeMillis();
	}

	public void scheduleForRemoval(int id) {
		WorldObject o = getObject(id);
		synchronized (scheduledForRemoval)
		{
			scheduledForRemoval.add(o);
		}
	}

	public void scheduleForRemoval(WorldObject object) {
		synchronized (scheduledForRemoval)
		{
			scheduledForRemoval.add(object);
		}
	}

	public void scheduleForAddition(WorldObject object) {
		int id = worldObjects.getAvailableId();
		object.setId(id);
		this.scheduleForAddition(object, id);
	}

	public void scheduleForAddition(WorldObject object, int id) {
		if (object == null)
			throw new IllegalArgumentException("addObject(WorldObject obj) argument 0 not allowed to be null!");

		if (worldObjects.get(id) != null)
			throw new IllegalArgumentException("addObject(WorldObject obj) not allowed to be an occupied id: "
				  + id + ".  Existing object: " + worldObjects.get(id));

		synchronized (scheduledForAddition)
		{
			scheduledForAddition.add(object);
		}
	}

	public void scheduleForOverwrite(WorldObject o, int id) {
		o.setId(id);
		synchronized (scheduledForOverwrite)
		{
			scheduledForOverwrite.add(o);
		}
		synchronized (scheduledForRemoval)
		{
			scheduledForRemoval.remove(o);
		}
	}

	/**
	 * Get the number of objects currently in the world.
	 * @return The number of objects currently in the world.
	 */
	public int getObjectCount() { return worldObjects.size(); }

	public WorldObject getObject(int id) { return worldObjects.get(id); }

	/**
	 * Get a list of all valid Zone IDs in this world.
	 * @return A List of all IDs which are linked to a zone.
	 */
	public List<Integer> getZoneIDs() {
		synchronized (zones)
		{
			return new LinkedList<>(zones.keySet());
		}
	}

	/**
	 * Get the zone with the given id.
	 * @param id The id of the zone to retrieve.
	 * @return The zone with the given id.
	 */
	public Zone getZone(int id) {
		synchronized (zones)
		{
			return zones.get(id);
		}
	}

	/**
	 * Get the time at which the most recent update completed.
	 * @return The time at which the most recent update completed.
	 */
	public long getLastUpdateCompleted() { return lastUpdateCompleted; }

	/**
	 * Get the time at which the most recent update started.
	 * @return The time at which the most recent update started.
	 */
	public long getLastUpdateStarted() { return lastUpdateStarted; }

	/**
	 * Remove an object from the world.  Specifically, sets the index of the given object in the world's array to null.
	 * @param object The object to remove.
	 */
	protected void removeObject(WorldObject object) {
		synchronized (worldObjects)
		{
			if (object != null)
			{
				worldObjects.remove(object.getId());
			}
		}
	}

	protected void removeScheduledObjects() {
		synchronized (scheduledForRemoval)
		{
			scheduledForRemoval.forEach(this::removeObject);
			scheduledForRemoval.clear();
		}
	}

	protected void addScheduledObjects() {
		synchronized (scheduledForAddition)
		{
			scheduledForAddition.forEach(object -> addObject(object, object.getId()));
			scheduledForAddition.clear();
		}
	}

	protected void overwriteScheduledObjects() {
		LinkedList<WorldObject> temp;
		synchronized (scheduledForOverwrite)
		{
			temp = new LinkedList<>(scheduledForOverwrite);
			scheduledForOverwrite.clear();
		}
		temp.forEach(this::overwriteExistingWith);
	}

	private void overwriteExistingWith(WorldObject o) {
		WorldObject old = getObject(o.getId());
		if (old != null)
			removeObject(old);
		addObject(o);
	}

	private void resetObjects() {
		worldObjects.forEach((Integer i, WorldObject o) -> o.reset());
	}

	private void updateObjects() {
		worldObjects.forEach((Integer i, WorldObject o) -> o.update());
	}

	private void setUpObjects() {
		worldObjects.forEach((Integer i, WorldObject o) -> o.setUp());
	}
}
