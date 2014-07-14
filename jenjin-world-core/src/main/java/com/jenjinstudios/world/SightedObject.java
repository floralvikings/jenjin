package com.jenjinstudios.world;

import com.jenjinstudios.world.math.FieldOfVisionCalculator;
import com.jenjinstudios.world.math.Vector2D;

import java.util.*;

/**
 * The {@code SightedObject} class is a {@code WorldObject} which maintains a range of locations which are visible to
 * it.  It also maintains a list of visible {@code WorldObject}s  which are visible to it.  This class is intended to be
 * used by a non-static WorldObject implementation.
 * @author Caleb Brinkman
 */
public class SightedObject extends WorldObject
{
	/** The radius of the square of visible locations. */
	public static final int VIEW_RADIUS = 11;
	private final ArrayList<Location> visibleLocations;
	private final TreeMap<Integer, WorldObject> visibleObjects;
	private final Set<WorldObject> newlyVisibleObjects;
	private final Set<WorldObject> newlyInvisibleObjects;
	private Vector2D vectorBeforeUpdate;

	public SightedObject(String name) {
		super(name);
		visibleObjects = new TreeMap<>();
		visibleLocations = new ArrayList<>();
		newlyVisibleObjects = new HashSet<>();
		newlyInvisibleObjects = new HashSet<>();
		vectorBeforeUpdate = getVector2D();
	}

	@Override
	public void setWorld(World world) {
		super.setWorld(world);
		resetVisibleLocations();
	}

	@Override
	public void setUp() {
		vectorBeforeUpdate = getVector2D();
	}

	@Override
	public void reset() {
		// If we're in a new locations after stepping, update the visible array.
		World world = getWorld();
		Location oldLoc = world.getLocationForCoordinates(getZoneID(), vectorBeforeUpdate);
		if (oldLoc != getLocation() || getVisibleLocations().isEmpty())
			resetVisibleLocations();
		resetVisibleObjects();
	}

	public AbstractMap<Integer, WorldObject> getVisibleObjects() {
		synchronized (visibleObjects)
		{
			return new TreeMap<>(visibleObjects);
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

	public AbstractCollection<Location> getVisibleLocations() { return visibleLocations; }

	private void resetVisibleLocations() {
		visibleLocations.clear();
		if (getLocation() != null)
		{
			Zone zone = getWorld().getZone(getZoneID());
			FieldOfVisionCalculator fov = new FieldOfVisionCalculator(zone, getLocation(), VIEW_RADIUS);
			visibleLocations.addAll(fov.scan());
		}
	}

	private void resetVisibleObjects() {
		ArrayList<WorldObject> currentlyVisible = getCurrentlyVisibleObjects();
		Collection<WorldObject> visibles;
		synchronized (visibleObjects)
		{
			visibles = visibleObjects.values();
		}

		addNewlyInvisibleObjects(currentlyVisible, visibles);
		addNewlyVisibleObjects(currentlyVisible, visibles);
		setCurrentlyVisibleObjects(currentlyVisible);

	}

	private void addNewlyVisibleObjects(ArrayList<WorldObject> currentlyVisible, Collection<WorldObject> visibles) {
		synchronized (newlyVisibleObjects)
		{
			newlyVisibleObjects.clear();
			newlyVisibleObjects.addAll(currentlyVisible);
			newlyVisibleObjects.removeAll(visibles);
		}
	}

	private void addNewlyInvisibleObjects(ArrayList<WorldObject> currentlyVisible, Collection<WorldObject> visibles) {
		synchronized (newlyInvisibleObjects)
		{
			newlyInvisibleObjects.clear();
			newlyInvisibleObjects.addAll(visibles);
			newlyInvisibleObjects.removeAll(currentlyVisible);
		}
	}

	private ArrayList<WorldObject> getCurrentlyVisibleObjects() {
		ArrayList<WorldObject> currentlyVisible = new ArrayList<>();
		for (Location loc : visibleLocations)
		{
			addCurrentlyVisibleObjectsInLocation(currentlyVisible, loc);
		}
		return currentlyVisible;
	}

	private void setCurrentlyVisibleObjects(ArrayList<WorldObject> currentlyVisible) {
		synchronized (visibleObjects)
		{
			visibleObjects.clear();
			for (WorldObject object : currentlyVisible)
			{
				visibleObjects.put(object.getId(), object);
			}
		}
	}

	private void addCurrentlyVisibleObjectsInLocation(ArrayList<WorldObject> currentlyVisible, Location loc) {
		for (WorldObject object : loc.getObjects())
		{
			if (object != this)
			{
				currentlyVisible.add(object);
			}
		}
	}
}
