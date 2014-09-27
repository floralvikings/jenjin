package com.jenjinstudios.world;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Contains all the Zones, Locations and GameObjects.
 * @author Caleb Brinkman
 */
public class World
{

	/** The list of in-world Zones. */
	private final TreeMap<Integer, Zone> zones = new TreeMap<>();
	/** The GameObjects contained in the world. */
	private transient final WorldObjectMap worldObjects = new WorldObjectMap(this);
	/** The time at which the most recent update completed. */
	private transient long lastUpdateCompleted;
	/** The start time of the most recent update. */
	private transient long lastUpdateStarted;

	/** Construct a new World. */
	public World() { }

	/**
	 * Construct a new world with the specified Zone array.
	 * @param zones The zones used to create the world.
	 */
	public World(Zone... zones) {
		for (Zone z : zones)
		{
			this.zones.put(z.getId(), z);
		}
		lastUpdateCompleted = lastUpdateStarted = System.currentTimeMillis();
	}

	public WorldObjectMap getWorldObjects() { return worldObjects; }

	/** Update all objects in the world. */
	public void update() {
		lastUpdateStarted = System.currentTimeMillis();
		synchronized (worldObjects)
		{
			worldObjects.removeScheduledObjects();
			worldObjects.addScheduledObjects();
			worldObjects.overwriteScheduledObjects();
			setUpObjects();
			updateObjects();
			resetObjects();
		}
		lastUpdateCompleted = System.currentTimeMillis();
	}

	/**
	 * Get a list of all valid Zone IDs in this world.
	 * @return A List of all IDs which are linked to a zone.
	 */
	public List<Integer> getZoneIDs() { return new LinkedList<>(zones.keySet()); }

	public Map<Integer, Zone> getZones() { return zones; }

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

	private void resetObjects() { worldObjects.forEach((Integer i, WorldObject o) -> o.reset()); }

	private void updateObjects() { worldObjects.forEach((Integer i, WorldObject o) -> o.update()); }

	private void setUpObjects() { worldObjects.forEach((Integer i, WorldObject o) -> o.setUp()); }
}
