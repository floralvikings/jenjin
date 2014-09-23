package com.jenjinstudios.world;

import com.jenjinstudios.world.math.FieldOfVisionCalculator;
import com.jenjinstudios.world.math.Vector2D;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The {@code SightedObject} class is a {@code WorldObject} which maintains a range of locations which are visible to
 * it.  It also maintains a list of visible {@code WorldObject}s  which are visible to it.  This class is intended to be
 * used by a non-static WorldObject implementation.
 * @author Caleb Brinkman
 */
public class SightedObject extends WorldObject
{
	/** The radius of the square of visible locations. */
	private static final int VIEW_RADIUS = 11;
	private final ArrayList<Location> visibleLocations;
	private final TreeMap<Integer, WorldObject> visibleObjects;
	private final Set<WorldObject> newlyVisibleObjects;
	private final Set<WorldObject> newlyInvisibleObjects;
	private final Set<WorldObject> visibleBeforeUpdate;
	private Vector2D vectorBeforeUpdate;
	private Vector2D vectorAfterUpdate;

	public SightedObject(String name) {
		super(name);
		visibleBeforeUpdate = new HashSet<>();
		visibleObjects = new TreeMap<>();
		visibleLocations = new ArrayList<>();
		newlyVisibleObjects = new HashSet<>();
		newlyInvisibleObjects = new HashSet<>();
		vectorBeforeUpdate = getVector2D();
	}

	@Override
	public void setUp() {
		super.setUp();
		vectorBeforeUpdate = getVector2D();
		setUpVisibleObjects();
		if (!vectorBeforeUpdate.equals(vectorAfterUpdate))
		{
			resetVisibleLocations();
		}
	}

	private void setUpVisibleObjects() {
		newlyVisibleObjects.clear();
		newlyInvisibleObjects.clear();
		newlyInvisibleObjects.addAll(visibleBeforeUpdate.stream().filter(o ->
			  !getVisibleObjects().containsKey(o.getId())).collect(Collectors.toList()));
		getVisibleObjects().values().stream().filter(o ->
			  !visibleBeforeUpdate.contains(o)).forEach(newlyVisibleObjects::add);
		visibleBeforeUpdate.clear();
		visibleBeforeUpdate.addAll(getVisibleObjects().values());
	}

	@Override
	public void reset() {
		vectorAfterUpdate = getVector2D();
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

	public AbstractCollection<Location> getVisibleLocations() { return new LinkedList<>(visibleLocations); }

	private void resetVisibleLocations() {
		synchronized (visibleLocations)
		{
			visibleLocations.clear();
		}
		if (getLocation() != null)
		{
			Zone zone = getWorld().getZone(getZoneID());
			FieldOfVisionCalculator fov = new FieldOfVisionCalculator(zone, getLocation(), VIEW_RADIUS);
			synchronized (visibleLocations)
			{
				visibleLocations.addAll(fov.scan());
			}
		}
	}

}
