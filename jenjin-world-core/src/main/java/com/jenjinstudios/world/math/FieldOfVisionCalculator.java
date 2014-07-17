package com.jenjinstudios.world.math;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.Zone;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Caleb Brinkman
 */
public class FieldOfVisionCalculator
{
	private Zone zone;
	private int radius;
	private int centerX;
	private int centerY;
	private Location center;

	public FieldOfVisionCalculator(Zone zone, Location center, int radius) {
		this.center = center;
		this.radius = radius;
		this.zone = zone;
		this.centerX = center.X_COORDINATE;
		this.centerY = center.Y_COORDINATE;
	}

	public List<Location> scan() {
		List<Location> visibles = new LinkedList<>();
		visibles.add(center);
		for (int x = centerX - radius; x < centerX + radius; x++)
		{
			addColumn(visibles, x);
		}
		return visibles;
	}

	private void addColumn(List<Location> visibles, int x) {
		for (int y = centerY - radius; y < centerY + radius; y++)
		{
			Location current = zone.getLocationOnGrid(x, y);
			if (current != null && inRange(current))
			{
				visibles.add(current);
			}
		}
	}

	public boolean inRange(Location location) {
		double deltaX = location.X_COORDINATE - centerX;
		double deltaY = location.Y_COORDINATE - centerY;
		double distance = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
		return distance < radius;
	}
}
