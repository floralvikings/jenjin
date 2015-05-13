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
		Vector2D vector2D = object.getPosition();
		double rad = calculateViewRadius(object);
		double r2 = rad * rad;
		for (WorldObject visible : world.getWorldObjects())
		{
			Vector2D otherVector = visible.getPosition();
			if (visible != object && isRoughlyVisible(object, visible, rad) &&
				  otherVector.getSquaredDistanceToVector(vector2D) <= r2)
			{
				worldObjects.add(visible);
			}
		}
		return worldObjects;
	}

	public static boolean isRoughlyVisible(WorldObject object, WorldObject visible, double rad) {
		Vector2D vector2D = object.getPosition();
		Vector2D otherVector = visible.getPosition();
		double minX = vector2D.getXCoordinate() - rad;
		double maxX = vector2D.getXCoordinate() + rad;
		double minY = vector2D.getYCoordinate() - rad;
		double maxY = vector2D.getYCoordinate() + rad;
		double otherX = otherVector.getXCoordinate();
		double otherY = otherVector.getYCoordinate();
		return otherX >= minX && otherX <= maxX && otherY >= minY && otherY <= maxY;
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
				Location location = ZoneUtils.getLocationForCoordinates(zone, worldObject.getPosition());
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
