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
		Location oldLoc = getWorld().getLocationForCoordinates(getZoneID(), vectorBeforeUpdate);
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

	public Iterable<WorldObject> getNewlyVisibleObjects() {
		synchronized (newlyVisibleObjects)
		{
			return new HashSet<>(newlyVisibleObjects);
		}
	}

	public Iterable<WorldObject> getNewlyInvisibleObjects() {
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

		newlyInvisibleObjects.clear();
		newlyInvisibleObjects.addAll(visibleObjects.values());
		newlyInvisibleObjects.removeAll(currentlyVisible);

		newlyVisibleObjects.clear();
		newlyVisibleObjects.addAll(currentlyVisible);
		newlyVisibleObjects.removeAll(visibleObjects.values());

		visibleObjects.clear();
		addCurrentlyVisibleObjects(currentlyVisible);
	}

	private ArrayList<WorldObject> getCurrentlyVisibleObjects() {
		ArrayList<WorldObject> currentlyVisible = new ArrayList<>();
		for (Location loc : visibleLocations)
		{
			addCurrentlyVisibleObjectsInLocation(currentlyVisible, loc);
		}
		return currentlyVisible;
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

	private void addCurrentlyVisibleObjects(ArrayList<WorldObject> currentlyVisible) {
		for (WorldObject object : currentlyVisible)
		{
			visibleObjects.put(object.getId(), object);
		}
	}
}
