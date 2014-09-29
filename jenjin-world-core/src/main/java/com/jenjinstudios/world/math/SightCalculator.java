package com.jenjinstudios.world.math;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.actor.VisionOnPreUpdate;
import com.jenjinstudios.world.event.PreUpdateEvent;
import com.jenjinstudios.world.util.ZoneUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Stream;

/**
 * @author Caleb Brinkman
 */
public class SightCalculator
{
	public static final String VISION_RADIUS_PROPERTY = "visionRadius";
	public static final double DEFAULT_VISION_RADIUS = 100d;

	public static void updateVisibleObjects(World world) {
		Collection<WorldObject> worldObjects = world.getWorldObjects().getWorldObjectCollection();
		clearVisibleObjects(worldObjects);
		updateVisibility(worldObjects);
	}

	public static Collection<Location> getVisibleLocations(WorldObject worldObject) {
		LinkedList<Location> locations = new LinkedList<>();
		World world = worldObject.getWorld();
		if (world != null)
		{
			int zoneId = worldObject.getZoneID();
			Zone zone = world.getZones().get(zoneId);
			if (zone != null)
			{
				Location location = ZoneUtils.getLocationForCoordinates(zone, worldObject.getVector2D());
				int radius = (int) (calculateViewRadius(worldObject) / Location.SIZE);
				FieldOfVisionCalculator fov = new FieldOfVisionCalculator(zone, location, radius);
				locations.addAll(fov.scan());
			}
		}
		return locations;
	}

	private static void updateVisibility(Collection<WorldObject> worldObjects) {
		HashMap<Integer, Boolean> alreadyChecked = new HashMap<>();
		for (WorldObject worldObject : worldObjects)
		{
			double radius = calculateViewRadius(worldObject);
			Stream<WorldObject> filter = worldObjects.stream().filter(o ->
				  o != worldObject && !alreadyChecked.containsKey(o.getId()));
			filter.forEach(visible -> determineVisibility(worldObject, visible, radius));
			alreadyChecked.put(worldObject.getId(), true);
		}
	}

	private static double calculateViewRadius(WorldObject worldObject) {
		Object customRadius = worldObject.getProperties().get(VISION_RADIUS_PROPERTY);
		return customRadius == null ? DEFAULT_VISION_RADIUS : (double) customRadius;
	}

	private static void clearVisibleObjects(Collection<WorldObject> worldObjects) {
		Stream<WorldObject> filter = worldObjects.stream().filter(o ->
			  o.getPreUpdateEvent(VisionOnPreUpdate.EVENT_NAME) != null);
		filter.forEach(o -> ((VisionOnPreUpdate) o.getPreUpdateEvent(VisionOnPreUpdate.EVENT_NAME))
			  .clearVisibleObjects());
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
		PreUpdateEvent e1 = worldObject.getPreUpdateEvent(VisionOnPreUpdate.EVENT_NAME);
		PreUpdateEvent e2 = visible.getPreUpdateEvent(VisionOnPreUpdate.EVENT_NAME);

		if (e1 != null)
		{
			((VisionOnPreUpdate) e1).addVisibleObject(visible);
		}
		if (e2 != null)
		{
			((VisionOnPreUpdate) e2).addVisibleObject(worldObject);
		}
	}
}
