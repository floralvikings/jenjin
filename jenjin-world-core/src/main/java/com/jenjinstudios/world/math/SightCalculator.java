package com.jenjinstudios.world.math;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.object.WorldObject;
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
		Vector2D vector2D = object.getGeometry2D().getPosition();
		double rad = object.getVision().getRadius();
		double r2 = rad * rad;
		for (WorldObject visible : world.getWorldObjects())
		{
			Vector2D otherVector = visible.getGeometry2D().getPosition();
			if (visible != object && isRoughlyVisible(object, visible, rad) &&
				  otherVector.getSquaredDistanceToVector(vector2D) <= r2)
			{
				worldObjects.add(visible);
			}
		}
		return worldObjects;
	}

	public static boolean isRoughlyVisible(WorldObject object, WorldObject visible, double rad) {
		Vector2D vector2D = object.getGeometry2D().getPosition();
		Vector2D otherVector = visible.getGeometry2D().getPosition();
		double minX = vector2D.getXValue() - rad;
		double maxX = vector2D.getXValue() + rad;
		double minY = vector2D.getYValue() - rad;
		double maxY = vector2D.getYValue() + rad;
		double otherX = otherVector.getXValue();
		double otherY = otherVector.getYValue();
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
				Location location = ZoneUtils.getLocationForCoordinates(zone, worldObject.getGeometry2D().getPosition
					  ());
				int radius = (int) (worldObject.getVision().getRadius() /
					  Location.SIZE);
				FieldOfVisionCalculator fov = new FieldOfVisionCalculator(zone, location, radius);
				locations.addAll(fov.scan());
			}
		}
		return locations;
	}

}
