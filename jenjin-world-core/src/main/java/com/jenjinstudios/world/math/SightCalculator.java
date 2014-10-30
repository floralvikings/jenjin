package com.jenjinstudios.world.math;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.util.ZoneUtils;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Caleb Brinkman
 */
public class SightCalculator
{
	public static final String VISION_RADIUS_PROPERTY = "visionRadius";
	public static final double DEFAULT_VISION_RADIUS = 100d;

	public static Collection<WorldObject> getVisibleObjects(WorldObject object) {
		World world = object.getWorld();
		if (world == null) throw new IllegalStateException("WorldObject " + object + " does not have a set World.");
		Collection<WorldObject> worldObjects = new LinkedList<>();
		Vector2D vector2D = object.getVector2D();
		double radius = calculateViewRadius(object);
		radius *= radius;
		for (WorldObject visible : world.getWorldObjects())
		{
			Vector2D otherVector = visible.getVector2D();
			if (visible != object && otherVector.getSquaredDistanceToVector(vector2D) <= radius)
			{
				worldObjects.add(visible);
			}
		}
		return worldObjects;
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

	private static double calculateViewRadius(WorldObject worldObject) {
		Object customRadius = worldObject.getProperties().get(VISION_RADIUS_PROPERTY);
		return customRadius == null ? DEFAULT_VISION_RADIUS : (double) customRadius;
	}

}
