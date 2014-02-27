package com.jenjinstudios.world;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * The {@code SightedObject} class is a {@code WorldObject} which maintains a range of locations which are visible to
 * it.  It also maintains a list of visible {@code WorldObject}s  which are visible to it.  This class is intended to be
 * used by a non-static WorldObject implementation.
 * @author Caleb Brinkman
 */
public class SightedObject extends WorldObject
{
	/** The radius of the square of visible locations. */
	public static final int VIEW_RADIUS = 10;
	/** The array of visible locations. */
	private final ArrayList<Location> visibleLocations;
	/** The container for visible objects. */
	private final TreeMap<Integer, WorldObject> visibleObjects;
	/** The list of newly visible objects. */
	private final ArrayList<WorldObject> newlyVisibleObjects;
	/** The list of newly invisible objects. */
	private final ArrayList<WorldObject> newlyInvisibleObjects;

	/**
	 * Construct a new SightedObject.
	 * @param name The name of this object.
	 */
	public SightedObject(String name) {
		super(name);
		visibleObjects = new TreeMap<>();
		visibleLocations = new ArrayList<>();
		newlyVisibleObjects = new ArrayList<>();
		newlyInvisibleObjects = new ArrayList<>();
	}

	/**
	 * Construct a new SightedObject.
	 * @param name The name of this object.
	 * @param id The id of the object.
	 */
	public SightedObject(String name, int id) {
		this(name);
		this.setId(id);
	}

	@Override
	public void setWorld(World world) {
		super.setWorld(world);
		resetVisibleLocations();
	}

	/**
	 * The container for visible objects.
	 * @return An ArrayList containing all objects visible to this actor.
	 */
	public TreeMap<Integer, WorldObject> getVisibleObjects() {
		synchronized (visibleObjects)
		{
			return new TreeMap<>(visibleObjects);
		}
	}

	/**
	 * Get newly visible objects.
	 * @return A list of all objects newly visible.
	 */
	public ArrayList<WorldObject> getNewlyVisibleObjects() {return newlyVisibleObjects;}

	/**
	 * Get newly invisible objects.
	 * @return A list of all objects newly invisible.
	 */
	public ArrayList<WorldObject> getNewlyInvisibleObjects() {return newlyInvisibleObjects;}

	/**
	 * Get the currently visible locations.
	 * @return The array list of currently visible locations.
	 */
	public ArrayList<Location> getVisibleLocations() { return visibleLocations; }

	/** Resets the array of currently visible location. */
	protected void resetVisibleLocations() {
		visibleLocations.clear();
		if (getLocation() != null)
		{
			visibleLocations.addAll(getLocation().getLocationsVisibleFrom());
		}
	}

	/** Reset the current list of visible objects. */
	protected void resetVisibleObjects() {
		ArrayList<WorldObject> currentlyVisible = new ArrayList<>();
		for (Location loc : visibleLocations)
		{
			for (WorldObject object : loc.getObjects())
			{
				if (object != this)
				{
					currentlyVisible.add(object);
				}
			}
		}

		newlyInvisibleObjects.clear();
		newlyInvisibleObjects.addAll(visibleObjects.values());
		newlyInvisibleObjects.removeAll(currentlyVisible);

		newlyVisibleObjects.clear();
		newlyVisibleObjects.addAll(currentlyVisible);
		newlyVisibleObjects.removeAll(visibleObjects.values());

		visibleObjects.clear();
		for (WorldObject object : currentlyVisible)
		{
			visibleObjects.put(object.getId(), object);
		}
	}
}
