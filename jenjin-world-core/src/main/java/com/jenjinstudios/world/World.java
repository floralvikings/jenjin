package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Dimension2D;
import com.jenjinstudios.world.math.SightCalculator;
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
		worldObjects = new WorldObjectMap(this);

		lastUpdateCompleted = lastUpdateStarted = System.currentTimeMillis();
	}

	public WorldObjectMap getWorldObjects() { return worldObjects; }

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
			worldObjects.removeScheduledObjects();
			worldObjects.addScheduledObjects();
			worldObjects.overwriteScheduledObjects();
			SightCalculator.updateVisibleObjects(worldObjects.getWorldObjectCollection());
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
