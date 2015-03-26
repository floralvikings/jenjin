package com.jenjinstudios.world;

import com.jenjinstudios.world.collections.WorldObjectList;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Contains all the Zones, Locations and GameObjects.
 * @author Caleb Brinkman
 */
public class World
{

	/** The list of in-world Zones. */
	private final TreeMap<Integer, Zone> zones = new TreeMap<>();
	/** The GameObjects contained in the world. */
	private final transient WorldObjectList worldObjects = new WorldObjectList(this);
	private final transient Collection<Runnable> scheduledUpdateTasks = new ConcurrentLinkedQueue<>();
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

	public WorldObjectList getWorldObjects() { return worldObjects; }

	public void update() {
		lastUpdateStarted = System.currentTimeMillis();
		synchronized (worldObjects)
		{
			worldObjects.refresh();
			worldObjects.forEach(WorldObject::preUpdate);
			worldObjects.forEach(WorldObject::update);
			worldObjects.forEach(WorldObject::postUpdate);
			scheduledUpdateTasks.forEach(Runnable::run);
		}
		lastUpdateCompleted = System.currentTimeMillis();
	}

	public Map<Integer, Zone> getZones() { return zones; }

	/**
	 * Schedule a task to be executed after the next update.
	 *
	 * @param task The task to execute.
	 */
	public void scheduleUpdateTask(Runnable task) {
		synchronized (scheduledUpdateTasks)
		{
			scheduledUpdateTasks.add(task);
		}
	}

	public long getLastUpdateCompleted() { return lastUpdateCompleted; }

	public long getLastUpdateStarted() { return lastUpdateStarted; }
}
