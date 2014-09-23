package com.jenjinstudios.world;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * The {@code SightedObject} class is a {@code WorldObject} which maintains a range of locations which are visible to
 * it.  It also maintains a list of visible {@code WorldObject}s  which are visible to it.  This class is intended to be
 * used by a non-static WorldObject implementation.
 * @author Caleb Brinkman
 */
public class SightedObject extends WorldObject
{
	private final TreeMap<Integer, WorldObject> visibleObjects;
	private final Set<WorldObject> newlyVisibleObjects;
	private final Set<WorldObject> newlyInvisibleObjects;
	private final Set<WorldObject> visibleLastSetUp;

	public SightedObject(String name) {
		super(name);
		visibleLastSetUp = new HashSet<>();
		visibleObjects = new TreeMap<>();
		newlyVisibleObjects = new HashSet<>();
		newlyInvisibleObjects = new HashSet<>();
	}

	@Override
	public void setUp() {
		super.setUp();
		setUpVisibleObjects();
	}

	private void setUpVisibleObjects() {
		newlyVisibleObjects.clear();
		newlyInvisibleObjects.clear();
		newlyInvisibleObjects.addAll(visibleLastSetUp.stream().filter(o ->
			  !getVisibleObjects().containsKey(o.getId())).collect(Collectors.toList()));
		getVisibleObjects().values().stream().filter(o ->
			  !visibleLastSetUp.contains(o)).forEach(newlyVisibleObjects::add);
		visibleLastSetUp.clear();
		visibleLastSetUp.addAll(getVisibleObjects().values());
	}

	public AbstractMap<Integer, WorldObject> getVisibleObjects() {
		synchronized (visibleObjects)
		{
			return new TreeMap<>(visibleObjects);
		}
	}

	public void addVisibleObject(WorldObject visible) {
		synchronized (visibleObjects)
		{
			visibleObjects.put(visible.getId(), visible);
		}
	}

	public void clearVisibleObjects() {
		synchronized (visibleObjects)
		{
			visibleObjects.clear();
		}
	}

	public Set<WorldObject> getNewlyVisibleObjects() {
		synchronized (newlyVisibleObjects)
		{
			return new HashSet<>(newlyVisibleObjects);
		}
	}

	public Set<WorldObject> getNewlyInvisibleObjects() {
		synchronized (newlyInvisibleObjects)
		{
			return new HashSet<>(newlyInvisibleObjects);
		}
	}
}
