package com.jenjinstudios.world.math;

import com.jenjinstudios.world.SightedObject;
import com.jenjinstudios.world.WorldObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * @author Caleb Brinkman
 */
public class SightCalculator
{
	public static final String VISION_RADIUS_PROPERTY = "visionRadius";
	public static final double DEFAULT_VISION_RADIUS = 50d;

	public static void updateVisibleObjects(Collection<WorldObject> worldObjects) {
		clearVisibleObjects(worldObjects);
		updateVisibility(worldObjects);
	}

	private static void updateVisibility(Collection<WorldObject> worldObjects) {
		HashMap<Integer, Boolean> alreadyChecked = new HashMap<>();
		for (WorldObject worldObject : worldObjects)
		{
			Object customRadius = worldObject.getProperties().get(VISION_RADIUS_PROPERTY);
			double radius = customRadius == null ? DEFAULT_VISION_RADIUS : (double) customRadius;
			Stream<WorldObject> filter = worldObjects.stream().filter(o ->
				  o != worldObject && !alreadyChecked.containsKey(o.getId()));
			filter.forEach(visible -> determineVisibility(worldObject, visible, radius));
			alreadyChecked.put(worldObject.getId(), true);
		}
	}

	private static void clearVisibleObjects(Collection<WorldObject> worldObjects) {
		Stream<WorldObject> filter = worldObjects.stream().filter(o -> o instanceof SightedObject);
		filter.forEach(o -> ((SightedObject) o).clearVisibleObjects());
	}

	private static void determineVisibility(WorldObject worldObject, WorldObject visible, double radius) {
		Vector2D objectVector = worldObject.getVector2D();
		Vector2D visibleVector = visible.getVector2D();
		double distance = objectVector.getDistanceToVector(visibleVector);
		if (distance <= radius)
		{
			addToEachVisibility(worldObject, visible);
		}
	}

	private static void addToEachVisibility(WorldObject worldObject, WorldObject visible) {
		if (visible instanceof SightedObject)
		{
			((SightedObject) visible).addVisibleObject(worldObject);
		}
		if (worldObject instanceof SightedObject)
		{
			((SightedObject) worldObject).addVisibleObject(worldObject);
		}
	}
}
