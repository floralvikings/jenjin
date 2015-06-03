package com.jenjinstudios.world;

import com.jenjinstudios.world.object.WorldObject;
import com.jenjinstudios.world.task.WorldObjectTaskAdapter;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The root of the game world-tree; children are zones.
 *
 * @author Caleb Brinkman
 */
public class World extends Node
{
	private final transient Collection<Runnable> scheduledUpdateTasks = new ConcurrentLinkedQueue<>();
	private final Map<String, Zone> children;

	/**
	 * Construct a new World.
	 */
	public World() {
		children = new HashMap<>(1);
		addTask(new ScheduledOneOffTask());
	}

	/**
	 * Get all WorldObjects which are direct children of any Cell children of the Zone children of this World.
	 *
	 * @return All WorldObjects which are direct children of any Cell children of the Zone children of this World.
	 */
	public Collection<WorldObject> getWorldObjects() {
		Collection<WorldObject> worldObjects = new LinkedList<>();

		children.values().forEach(zone -> worldObjects.addAll(zone.getWorldObjects()));

		return worldObjects;
	}

	/**
	 * Schedule a task to be run at the end of the next update cycle.  This task will be executed only once.
	 * @param task The task to execute.
	 */
	public void scheduleUpdateTask(Runnable task) {
		synchronized (scheduledUpdateTasks)
		{
			scheduledUpdateTasks.add(task);
		}
	}

	@Override
	public Node getParent() { return null; }

	@Override
	public Collection<Zone> getChildren() { return children.values(); }

	@Override
	public Node removeChildRecursively(Node child) {
		Node r = children.remove(child.getId());
		if (r == null) {
			Iterator<Zone> iterator = children.values().iterator();
			while (iterator.hasNext() && (r == null)) {
				r = iterator.next().removeChildRecursively(child);
			}
		}
		return r;
	}

	private class ScheduledOneOffTask extends WorldObjectTaskAdapter
	{
		@Override
		public void onPostUpdate(Node node) {
			scheduledUpdateTasks.forEach(Runnable::run);
			scheduledUpdateTasks.clear();
		}
	}
}
