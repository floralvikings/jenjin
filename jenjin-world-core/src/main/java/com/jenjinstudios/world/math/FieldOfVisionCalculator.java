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
		width = (radius + centerX) < zone.xSize ? (radius + centerX) : zone.xSize;
		height = (radius + centerY) < zone.ySize ? (radius + centerY) : zone.ySize;
		visibleLocations = new LinkedList<>();
		visibleLocations.add(center);
	}

	public List<Location> scan() {
		for (int i = 1; i <= 8; i++)
			scanOctant(i, 1, new SlopePair(1.0f, 0.0f));
		return visibleLocations;
	}

	private void scanOctant(int octant, int row, SlopePair slopePair) {
		if (slopePair.getStartSlope() < slopePair.getEndSlope())
		{
			return;
		}
		ScanState scanState = new ScanState();
		scanState.newStartSlope = 0.0f;
		scanState.previouslyBlocked = false;
		scanState.octant = octant;
		scanState.radius = radius;
		for (scanState.distance = row; scanState.shouldContinue(); scanState.distance++)
		{
			scanState.deltaY = -scanState.distance;
			for (scanState.deltaX = -scanState.distance; scanState.deltaX <= 0; scanState.deltaX++)
			{
				scanState.currentX = calcCurrentX(scanState.deltaX, scanState.deltaY, octant);
				scanState.currentY = calcCurrentY(scanState.deltaX, scanState.deltaY, octant);

				if (!inRange(scanState.currentX, scanState.currentY)) { continue; }

				float centerSlope = calcCenterSlope(scanState.currentX, scanState.currentY, octant);

				if (slopePair.getStartSlope() < centerSlope)
				{
					continue;
				} else if (slopePair.getEndSlope() > centerSlope)
				{
					break;
				}

				checkLocation(slopePair, scanState);
			}
		}
	}

	private void checkLocation(SlopePair slopePair, ScanState scanState) {
		double currentRadius = calcRadius(scanState.deltaX, scanState.deltaY);
		addIfInRange(scanState.currentX, scanState.currentY, currentRadius);

		boolean currentBlocked = blocksVision(scanState.currentX, scanState.currentY);
		if (scanState.previouslyBlocked)
		{
			if (currentBlocked)
			{
				scanState.newStartSlope = calcRightSlope(scanState.currentX, scanState.currentY, scanState.octant);
			} else
			{
				scanState.previouslyBlocked = false;
				slopePair.setStartSlope(scanState.newStartSlope);
			}
		} else
		{
			if (currentBlocked && scanState.distance < radius)
			{
				scanState.previouslyBlocked = true;
				float newLeftSlope = calcLeftSlope(scanState.currentX, scanState.currentY, scanState.octant);
				SlopePair newSlopePair = new SlopePair(slopePair.startSlope, newLeftSlope);
				scanOctant(scanState.octant, scanState.distance + 1, newSlopePair);
				scanState.newStartSlope = calcRightSlope(scanState.currentX, scanState.currentY, scanState.octant);
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
		return calcSlopeInOctant(octant, centerVector, brushCorner);
	}

	private float calcLeftSlope(int x, int y, int octant) {
		Location loc = zone.getLocationOnGrid(x, y);
		Vector2D centerVector = center.getCenter();
		Vector2D brushCorner = loc.getCenter();
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
		return calcSlopeInOctant(octant, centerVector, brushCorner);
	}

	private float calcSlopeInOctant(int octant, Vector2D origin, Vector2D endpoint) {
		double dx;
		double dy;
		dx = Math.abs(origin.getXCoordinate() - endpoint.getXCoordinate());
		dy = Math.abs(origin.getYCoordinate() - endpoint.getYCoordinate());
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
		private float endSlope;

		private SlopePair(float startSlope, float endSlope) {
			this.startSlope = startSlope;
			this.endSlope = endSlope;
		}

		public float getStartSlope() { return startSlope; }

		public float getEndSlope() { return endSlope; }

		public void setStartSlope(float startSlope) { this.startSlope = startSlope; }
	}

	private static class ScanState
	{
		public boolean previouslyBlocked;
		public float newStartSlope;
		public int octant;
		public int distance;
		public float radius;
		public int deltaX, deltaY;
		public int currentX, currentY;

		public boolean shouldContinue() { return distance <= radius && !previouslyBlocked; }
	}
}
