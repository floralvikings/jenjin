package com.jenjinstudios.world;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.function.BiConsumer;

/**
 * Used to store WorldObjects.
 * @author Caleb Brinkman
 */
public class WorldObjectMap
{
	private final TreeMap<Integer, WorldObject> worldObjects = new TreeMap<>();
	private final HashSet<Integer> reservedIds = new HashSet<>();
	private final World world;
	private final LinkedList<WorldObject> scheduledForRemoval;
	private final LinkedList<WorldObject> scheduledForAddition;
	private final LinkedList<WorldObject> scheduledForOverwrite;

	public WorldObjectMap(World world) {
		this.world = world;
		scheduledForRemoval = new LinkedList<>();
		scheduledForAddition = new LinkedList<>();
		scheduledForOverwrite = new LinkedList<>();
	}

	public void put(int key, WorldObject value) { worldObjects.put(key, value); }

	public Collection<WorldObject> getWorldObjectCollection() { return new LinkedList<>(worldObjects.values()); }

	public int getAvailableId() {
		int currentKey = 0;
		boolean containsKey = worldObjects.get(currentKey) != null;
		boolean keyReserved = reservedIds.contains(currentKey);
		while (containsKey || keyReserved)
		{
			currentKey++;
			containsKey = worldObjects.get(currentKey) != null;
			keyReserved = reservedIds.contains(currentKey);
		}
		return currentKey;
	}

	private void addObject(WorldObject object) {
		int id = getAvailableId();
		this.addObject(object, id);
	}

	private void addObject(WorldObject object, int id) {
		object.setId(id);
		object.setWorld(world);
		object.setVector2D(object.getVector2D());
		synchronized (this)
		{
			put(id, object);
		}
	}

	public void forEach(BiConsumer<? super Integer, ? super WorldObject> action) { worldObjects.forEach(action); }

	public int getObjectCount() { return worldObjects.size(); }

	public WorldObject getObject(int id) { return worldObjects.get(id); }

	public void removeObject(int i) {
		synchronized (worldObjects)
		{
			worldObjects.remove(i);
		}
	}

	protected void removeObject(WorldObject object) {
		if (object != null)
		{
			removeObject(object.getId());
		}
	}

	protected void overwriteExistingWith(WorldObject o) {
		WorldObject old = getObject(o.getId());
		if (old != null)
			removeObject(old);
		addObject(o);
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
		if (object == null)
			throw new IllegalArgumentException("addObject(WorldObject obj) argument 0 not allowed to be null!");
		int id;
		synchronized (worldObjects)
		{
			id = getAvailableId();
		}
		this.scheduleForAddition(object, id);
	}

	public void scheduleForAddition(WorldObject object, int id) {
		if (object == null)
			throw new IllegalArgumentException("addObject(WorldObject obj) argument 0 not allowed to be null!");

		if (isIdReserved(id))
			throw new IllegalArgumentException("addObject(WorldObject obj) not allowed to be an occupied id: "
				  + id + ".  Existing object: " + getObject(id));

		reservedIds.add(id);
		object.setId(id);

		synchronized (scheduledForAddition)
		{
			scheduledForAddition.add(object);
		}
	}

	private boolean isIdReserved(int id) {return getObject(id) != null || reservedIds.contains(id);}

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
}
