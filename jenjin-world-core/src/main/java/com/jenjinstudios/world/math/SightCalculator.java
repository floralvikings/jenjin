package com.jenjinstudios.world.math;

import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.object.WorldObject;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Caleb Brinkman
 */
public class SightCalculator
{

	public static Collection<WorldObject> getVisibleObjects(World world, WorldObject object)
	{
		Collection<WorldObject> worldObjects = new LinkedList<>();
		Vector vector = object.getGeometry().getPosition();
		double rad = object.getVision().getRadius();
		double r2 = rad * rad;
		for (WorldObject visible : world.getWorldObjects())
		{
			Vector otherVector = visible.getGeometry().getPosition();
			if (visible != object && isRoughlyVisible(object, visible, rad) &&
				  otherVector.getSquaredDistanceToVector(vector) <= r2)
			{
				worldObjects.add(visible);
			}
		}
		return worldObjects;
	}

	public static boolean isRoughlyVisible(WorldObject object, WorldObject visible, double rad) {
		Vector vector = object.getGeometry().getPosition();
		Vector otherVector = visible.getGeometry().getPosition();
		double minX = vector.getXValue() - rad;
		double maxX = vector.getXValue() + rad;
		double minY = vector.getYValue() - rad;
		double maxY = vector.getYValue() + rad;
		double otherX = otherVector.getXValue();
		double otherY = otherVector.getYValue();
		return otherX >= minX && otherX <= maxX && otherY >= minY && otherY <= maxY;
	}

	public static Collection<WorldObject> getVisibleObjects(WorldObject object) {
		//TODO Implement
		return new LinkedList<>();
	}

	public static Collection<Cell> getVisibleCells(WorldObject object) {
		// TODO Implement, will require graph algorithms
		return null;
	}

}
