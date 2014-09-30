package com.jenjinstudios.world.math;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.actor.Vision;
import com.jenjinstudios.world.util.ZoneUtils;

import java.util.*;

/**
 * @author Caleb Brinkman
 */
public class SightCalculator
{
	public static final String VISION_RADIUS_PROPERTY = "visionRadius";
	public static final double DEFAULT_VISION_RADIUS = 100d;

	public static void updateVisibleObjects(World world) {
		Collection<WorldObject> worldObjects = world.getWorldObjects().getWorldObjectCollection();
		Map<WorldObject, Set<WorldObject>> visibleMap = determineVisibility(worldObjects);
		setVisibleObjects(visibleMap);
	}

	private static void setVisibleObjects(Map<WorldObject, Set<WorldObject>> visibleMap) {
		Set<WorldObject> keySet = visibleMap.keySet();
		for (WorldObject key : keySet)
		{
			Object vision = key.getProperties().get(Vision.PROPERTY_NAME);
			if (vision != null && vision instanceof Vision)
			{
				((Vision) vision).setVisibleObjects(visibleMap.get(key));
			}
		}
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

	private static Map<WorldObject, Set<WorldObject>> determineVisibility(Collection<WorldObject> objects) {
		Map<WorldObject, Set<WorldObject>> map = createVisibilityMap(objects);
		HashSet<WorldObject> alreadyChecked = new HashSet<>();
		for (WorldObject worldObject : objects)
		{
			double radius = calculateViewRadius(worldObject);
			objects.stream().
				  filter(o -> o != worldObject && !alreadyChecked.contains(o)).
				  filter(v -> new Circle(worldObject, radius).contains(v)).
				  forEach(v -> addToEachVisibility(worldObject, v, map));
			alreadyChecked.add(worldObject);
		}
		return map;
	}

	private static Map<WorldObject, Set<WorldObject>> createVisibilityMap(Collection<WorldObject> objects) {
		Map<WorldObject, Set<WorldObject>> map = new HashMap<>();
		for (WorldObject o : objects)
		{
			map.put(o, new HashSet<>());
		}
		return map;
	}

	private static double calculateViewRadius(WorldObject worldObject) {
		Object customRadius = worldObject.getProperties().get(VISION_RADIUS_PROPERTY);
		return customRadius == null ? DEFAULT_VISION_RADIUS : (double) customRadius;
	}

	private static void addToEachVisibility(WorldObject a, WorldObject b, Map<WorldObject, Set<WorldObject>> map) {
		addObjectToVisibility(a, b, map);
		addObjectToVisibility(b, a, map);
	}

	private static void addObjectToVisibility(WorldObject a, WorldObject b, Map<WorldObject, Set<WorldObject>> map) {
		Object object = a.getProperties().get(Vision.PROPERTY_NAME);
		// This event will exist if the Object can see
		if (object != null && object instanceof Vision) { map.get(a).add(b); }
	}

	private static class Circle
	{
		private final Vector2D o;
		private final double radius;

		public Circle(WorldObject o, double radius) {
			this.o = o.getVector2D();
			this.radius = radius;
		}

		public boolean contains(WorldObject w) { return o.getDistanceToVector(w.getVector2D()) < radius; }

	}
}
