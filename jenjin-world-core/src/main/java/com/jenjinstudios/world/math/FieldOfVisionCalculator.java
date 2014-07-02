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
	private Location center;
	private int centerX;
	private int centerY;
	private int width;
	private int height;
	private List<Location> visibleLocations;

	public FieldOfVisionCalculator(Zone zone, Location center, int radius) {
		this.radius = radius;
		this.zone = zone;
		this.center = center;
		this.centerX = center.X_COORDINATE;
		this.centerY = center.Y_COORDINATE;
		width = (radius + centerX) < zone.getXSize() ? (radius + centerX) : zone.getXSize();
		height = (radius + centerY) < zone.getYSize() ? (radius + centerY) : zone.getYSize();
		visibleLocations = new LinkedList<>();
		visibleLocations.add(center);
	}

	public List<Location> scan() {
		scanOctant(1, 1, new SlopePair(1.0f, 0.0f));
		scanOctant(2, 1, new SlopePair(1.0f, 0.0f));
		scanOctant(3, 1, new SlopePair(1.0f, 0.0f));
		scanOctant(4, 1, new SlopePair(1.0f, 0.0f));
		scanOctant(5, 1, new SlopePair(1.0f, 0.0f));
		scanOctant(6, 1, new SlopePair(1.0f, 0.0f));
		scanOctant(7, 1, new SlopePair(1.0f, 0.0f));
		scanOctant(8, 1, new SlopePair(1.0f, 0.0f));
		return visibleLocations;
	}

	private void scanOctant(int octant, int row, SlopePair slopePair) {
		float newStartSlope = 0.0f;
		boolean previouslyBlocked = false;
		if (slopePair.getStartSlope() < slopePair.getEndSlope())
		{
			return;
		}
		for (int distance = row; distance <= radius && !previouslyBlocked; distance++)
		{
			int deltaY = -distance;
			for (int deltaX = -distance; deltaX <= 0; deltaX++)
			{
				int currentX = calcCurrentX(deltaX, deltaY, octant);
				int currentY = calcCurrentY(deltaX, deltaY, octant);

				if (!inRange(currentX, currentY)) { continue; }

				float centerSlope = calcCenterSlope(currentX, currentY, octant);

				if (slopePair.getStartSlope() < centerSlope)
				{
					continue;
				} else if (slopePair.getEndSlope() > centerSlope)
				{
					break;
				}

				double rad = calcRadius(deltaX, deltaY);
				addIfInRange(currentX, currentY, rad);

				boolean currentBlocked = blocksVision(currentX, currentY);
				if (previouslyBlocked)
				{
					if (currentBlocked)
					{
						newStartSlope = calcRightSlope(currentX, currentY, octant);
					} else
					{
						previouslyBlocked = false;
						slopePair = new SlopePair(newStartSlope, slopePair.endSlope);
					}
				} else
				{
					if (currentBlocked && distance < radius)
					{
						previouslyBlocked = true;
						scanOctant(octant, distance + 1, new SlopePair(slopePair.startSlope, calcLeftSlope(currentX, currentY, octant)));
						newStartSlope = calcRightSlope(currentX, currentY, octant);
					}
				}
			}
		}
	}

	private int calcCurrentX(int deltaX, int deltaY, int octant) {
		int offset = 0;
		switch (octant)
		{
			case 1:
			case 6:
				offset = deltaX;
				break;
			case 2:
			case 5:
				offset = -deltaX;
				break;
			case 7:
			case 8:
				offset = deltaY;
				break;
			case 3:
			case 4:
				offset = -deltaY;
				break;
		}
		return centerX + offset;
	}

	private int calcCurrentY(int deltaX, int deltaY, int octant) {
		int offset = 0;
		switch (octant)
		{
			case 1:
			case 2:
				offset = deltaY;
				break;
			case 6:
			case 5:
				offset = -deltaY;
				break;
			case 3:
			case 8:
				offset = deltaX;
				break;
			case 7:
			case 4:
				offset = -deltaX;
				break;
		}
		return centerY + offset;
	}

	private boolean inRange(int currentX, int currentY) {
		Location loc = zone.getLocationOnGrid(currentX, currentY);
		return loc != null && currentX >= 0 && currentY >= 0 && currentX <= this.width && currentY <= this.height;
	}

	private void addIfInRange(int currentX, int currentY, double rad) {
		if (rad <= radius)
		{
			Location loc = zone.getLocationOnGrid(currentX, currentY);
			visibleLocations.add(loc);
		}
	}

	private float calcCenterSlope(int x, int y, int octant) {
		Location loc = zone.getLocationOnGrid(x, y);
		double dx = Math.abs(centerX - loc.X_COORDINATE);
		double dy = Math.abs(centerY - loc.Y_COORDINATE);
		float slope = 0.0f;
		switch (octant)
		{
			case 1:
			case 2:
			case 5:
			case 6:
				slope = (float) (dx / dy);
				break;
			case 3:
			case 4:
			case 7:
			case 8:
				slope = (float) (dy / dx);
				break;
		}

		return slope;
	}

	private float calcRightSlope(int x, int y, int octant) {
		Location loc = zone.getLocationOnGrid(x, y);
		Vector2D centerVector = center.getCenter();
		Vector2D brushCorner = loc.getCenter();
		double dx, dy;
		float slope = 0.0f;
		switch (octant)
		{
			case 1:
			case 4:
				brushCorner = loc.getSouthEastCorner();
				break;
			case 2:
			case 7:
				brushCorner = loc.getSouthWestCorner();
				break;
			case 3:
			case 6:
				brushCorner = loc.getNorthEastCorner();
				break;
			case 5:
			case 8:
				brushCorner = loc.getNorthWestCorner();
				break;
		}
		dx = Math.abs(centerVector.getXCoordinate() - brushCorner.getXCoordinate());
		dy = Math.abs(centerVector.getYCoordinate() - brushCorner.getYCoordinate());
		switch (octant)
		{
			case 1:
			case 2:
			case 5:
			case 6:
				slope = (float) (dx / dy);
				break;
			case 3:
			case 4:
			case 7:
			case 8:
				slope = (float) (dy / dx);
				break;
		}
		return slope;
	}

	private float calcLeftSlope(int x, int y, int octant) {
		Location loc = zone.getLocationOnGrid(x, y);
		Vector2D centerVector = center.getCenter();
		Vector2D brushCorner = loc.getCenter();
		float slope = 0.0f;
		double dx, dy;
		switch (octant)
		{
			case 1:
			case 4:
				brushCorner = loc.getNorthWestCorner();
				break;
			case 2:
			case 7:
				brushCorner = loc.getNorthEastCorner();
				break;
			case 3:
			case 6:
				brushCorner = loc.getSouthWestCorner();
				break;
			case 5:
			case 8:
				brushCorner = loc.getSouthEastCorner();
				break;
		}

		dx = Math.abs(centerVector.getXCoordinate() - brushCorner.getXCoordinate());
		dy = Math.abs(centerVector.getYCoordinate() - brushCorner.getYCoordinate());

		switch (octant)
		{
			case 1:
			case 2:
			case 5:
			case 6:
				slope = (float) (dx / dy);
				break;
			case 3:
			case 4:
			case 7:
			case 8:
				slope = (float) (dy / dx);
				break;
		}

		return slope;
	}

	public float calcRadius(float dx, float dy) {
		double dx2 = dx * dx;
		double dy2 = dy * dy;
		double sum = dx2 + dy2;
		return (float) Math.sqrt(sum);
	}

	public boolean blocksVision(int x, int y) {
		Location loc = zone.getLocationOnGrid(x, y);
		return loc == null || "true".equals(loc.getProperties().getProperty("blocksVision"));
	}

	private static class SlopePair
	{
		private float startSlope;
		private final float endSlope;

		private SlopePair(float startSlope, float endSlope) {
			this.startSlope = startSlope;
			this.endSlope = endSlope;
		}

		public float getStartSlope() { return startSlope; }

		public float getEndSlope() { return endSlope; }

	}
}
