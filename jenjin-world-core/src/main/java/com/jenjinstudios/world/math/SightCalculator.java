package com.jenjinstudios.world.math;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.actor.Vision;
import com.jenjinstudios.world.event.PreUpdateEvent;
import com.jenjinstudios.world.util.ZoneUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * @author Caleb Brinkman
 */
public class SightCalculator
{
	public static final String VISION_RADIUS_PROPERTY = "visionRadius";
	public static final double DEFAULT_VISION_RADIUS = 100d;
	private static final Logger LOGGER = Logger.getLogger(SightCalculator.class.getName());

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
			  o.getPreUpdateEvent(Vision.EVENT_NAME) != null);
		filter.forEach(o -> ((Vision) o.getPreUpdateEvent(Vision.EVENT_NAME))
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

	private static void addToEachVisibility(WorldObject seeing, WorldObject visible) {
		addObjectToVisibility(seeing, visible);
		addObjectToVisibility(visible, seeing);
	}

	private static void addObjectToVisibility(WorldObject a, WorldObject b) {
		PreUpdateEvent event = a.getPreUpdateEvent(Vision.EVENT_NAME);
		// This event will exist if the Object can see
		if (event != null)
		{
			Vision vision = (Vision) event;
			// Don't want to add the object to the visibility tree if it's already there
			if (!vision.getVisibleObjects().containsKey(b.getId()))
			{
				LOGGER.log(Level.FINEST, "{0} can see {1}", new Object[]{a, b});
				vision.addVisibleObject(b);
			}
		}
	}
}
