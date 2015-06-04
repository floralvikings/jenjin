package com.jenjinstudios.world;

import com.jenjinstudios.world.object.WorldObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
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
	}

	/**
	 * Get all WorldObjects which are direct children of any Cell children of the Zone children of this World.
	 *
	 * @return All WorldObjects which are direct children of any Cell children of the Zone children of this World.
	 */
	public Iterable<WorldObject> getWorldObjects() {
		Collection<WorldObject> worldObjects = new LinkedList<>();

		children.values().forEach(zone -> worldObjects.addAll(zone.getWorldObjects()));

		return worldObjects;
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

	@Override
	public Node getParent() { return null; }

	@Override
	public Collection<Zone> getChildren() { return children.values(); }

	@Override
	public void preUpdate() {
		getTasks().forEach(t -> t.onPreUpdate(this));
		getObservers().forEach(t -> t.onPreUpdate(this));
		for (Zone zone : getChildren()) {
			zone.preUpdate();
		}
	}

	@Override
	public void update() {
		getTasks().forEach(t -> t.onUpdate(this));
		getObservers().forEach(t -> t.onUpdate(this));
		getChildren().forEach(Zone::update);
	}

	@Override
	public void postUpdate() {
		getTasks().forEach(t -> t.onPostUpdate(this));
		getObservers().forEach(t -> t.onPostUpdate(this));
		getChildren().forEach(Zone::postUpdate);
		oneTimeTasks.forEach(Runnable::run);
		oneTimeTasks.clear();
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

}
