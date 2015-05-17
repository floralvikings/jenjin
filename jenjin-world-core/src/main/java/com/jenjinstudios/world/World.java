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
	}

	public WorldObjectList getWorldObjects() { return worldObjects; }

	public void update() {
		synchronized (worldObjects)
		{
			worldObjects.refresh();
			worldObjects.forEach(o -> o.getTasks().forEach(t -> t.onPreUpdate(this, o)));
			worldObjects.forEach(o -> o.getTasks().forEach(t -> t.onUpdate(this, o)));
			worldObjects.forEach(o -> o.getTasks().forEach(t -> t.onPostUpdate(this, o)));
			scheduledUpdateTasks.forEach(Runnable::run);
			scheduledUpdateTasks.clear();
		}
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

}
