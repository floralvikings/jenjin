package com.jenjinstudios.world.actor;

import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.object.WorldObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Class containing vision information for an object, including visible objects
 * and vision radius.
 *
 * @author Caleb Brinkman
 */
public class Vision
{
	private static final double DEFAULT_VISION_RADIUS = Cell.CELL_SIZE * 10;
	private final NewlyInvisibleObserver newlyInvisibleObserver;
	private final NewlyVisibleObserver newlyVisibleObserver;
	private final Set<WorldObject> visibleObjects;
	private double radius;

	/**
	 * Construct a new Vision object.
	 */
	public Vision() {
		newlyInvisibleObserver = new NewlyInvisibleObserver();
		newlyVisibleObserver = new NewlyVisibleObserver();
		newlyInvisibleObserver.registerEventHandler(new NewlyInvisibleHandler());
		newlyVisibleObserver.registerEventHandler(new NewlyVisibleHandler());
		visibleObjects = new HashSet<>(10);
		radius = DEFAULT_VISION_RADIUS;
	}

	/**
	 * Get the observer used to watch for newly visible objects.
	 *
	 * @return The observer used to watch for newly visible objects.
	 */
	public NewlyVisibleObserver getNewlyVisibleObserver() { return newlyVisibleObserver; }

	/**
	 * Get the observer used to watch for newly invisible objects.
	 *
	 * @return The observer used to watch for newly invisible objects.
	 */
	public NewlyInvisibleObserver getNewlyInvisibleObserver() { return newlyInvisibleObserver; }

	/**
	 * Get the objects which are visible to this Vision.
	 *
	 * @return A set of objects visible to this Vision.
	 */
	public Collection<WorldObject> getVisibleObjects() { return new HashSet<>(visibleObjects); }

	/**
	 * Add an object to the collection of visible objects.
	 *
	 * @param object The object to add.
	 */
	public void addVisibleObject(WorldObject object) { visibleObjects.add(object); }

	/**
	 * Remove the specified object from the collection of visible objects.
	 *
	 * @param object The object to be removed.
	 */
	public void removeVisibleObject(WorldObject object) { visibleObjects.remove(object); }

	/**
	 * Get the radius in units of vision for this Vision.
	 *
	 * @return The radius in units of area visible to this Vision.
	 */
	public double getRadius() { return radius; }

	/**
	 * Set the radius of the area visible to this Vision.
	 *
	 * @param radius The radius of the area visible to this Vision.
	 */
	public void setRadius(int radius) { this.radius = radius; }
}
