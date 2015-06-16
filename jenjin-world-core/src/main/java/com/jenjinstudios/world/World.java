package com.jenjinstudios.world;

import com.jenjinstudios.world.reflection.DynamicMethod;
import com.jenjinstudios.world.task.NodeTask;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The root of the game world-tree; children are zones.
 *
 * @author Caleb Brinkman
 */
public class World extends Node
{
	private final transient Collection<Runnable> oneTimeTasks = new ConcurrentLinkedQueue<>();
	private final Map<String, Zone> children;

	/**
	 * Construct a new World.
	 */
	public World() {
		children = new HashMap<>(1);
		addTask(new ExecuteOneOffsTask());
	}

	/**
	 * Schedule a task to be run at the end of the next update cycle.  This task will be executed only once.
	 *
	 * @param task The task to execute.
	 */
	public void scheduleOneTimeTask(Runnable task) {
		synchronized (oneTimeTasks) {
			oneTimeTasks.add(task);
		}
	}

	/**
	 * Add a zone to this world.
	 *
	 * @param zone The zone to add.
	 */
	public void addZone(Zone zone) {
		children.put(zone.getId(), zone);
		zone.setParent(this);
	}

	private void runOneTimeTasks() {
		oneTimeTasks.forEach(Runnable::run);
		oneTimeTasks.clear();
	}

	@Override
	public Node getParent() { return null; }

	@Override
	public Collection<Zone> getChildren() { return children.values(); }

	/**
	 * Get the zone with the specified identification string.
	 *
	 * @param id The identifier of the zone to retrieve.
	 *
	 * @return The zone with the specified identifier.
	 */
	public Zone getZone(String id) { return children.get(id); }

	/**
	 * Used to execute a world task once.
	 */
	public static class ExecuteOneOffsTask extends NodeTask
	{
		/**
		 * Execute the tasks stored in the world one time schedule, then clear them.
		 *
		 * @param world The world.
		 */
		@DynamicMethod
		public void onPostUpdate(World world) {
			world.runOneTimeTasks();
		}

	}

}
