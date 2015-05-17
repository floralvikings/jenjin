package com.jenjinstudios.world.object;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.task.VisionTask;

import java.util.Set;

/**
 * Class containing vision information for an object, including visible objects
 * and vision radius.
 *
 * @author Caleb Brinkman
 */
public class Vision
{
	private static final double DEFAULT_VISION_RADIUS = Location.SIZE * 10;
	private final VisionTask visionTask = new VisionTask();
	private double radius = DEFAULT_VISION_RADIUS;

	/**
	 * Get the task used by this Vision to update visible objects.
	 *
	 * @return The task used by this Vision to update visible objects.
	 */
	VisionTask getVisionTask() { return visionTask; }

	/**
	 * Get the objects which are visible to this Vision.
	 *
	 * @return A set of objects visible to this Vision.
	 */
	public Set<WorldObject> getVisibleObjects() {
		return visionTask.getVisibleObjects();
	}

	/**
	 * Get the objects which have become visible to this Vision since the last
	 * update.
	 *
	 * @return The objects which have become visible to this Vision since the
	 * last update
	 */
	public Set<WorldObject> getNewlyVisibleObjects() {
		return visionTask.getNewlyVisibleObjects();
	}

	/**
	 * Get s set of objects which have become invisible to this Vision since
	 * last update.
	 *
	 * @return A set of objects which have become invisible to this Vision.
	 */
	public Set<WorldObject> getNewlyInvisibleObjects() {
		return visionTask.getNewlyInvisibleObjects();
	}

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
