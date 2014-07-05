package com.jenjinstudios.world.math;

import com.jenjinstudios.world.Location;
import com.jenjinstudios.world.LocationUtil;
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
		if (slopePair.getStartSlope() >= slopePair.getEndSlope())
		{
			ScanState scanState = new ScanState();
			scanState.newStartSlope = 0.0f;
			scanState.previouslyBlocked = false;
			scanState.octant = octant;
			scanState.radius = radius;
			scanState.slopePair = slopePair;
			for (scanState.distance = row; scanState.shouldContinue(); scanState.distance++)
			{
				scanState.deltaY = -scanState.distance;
				scanColumn(scanState);
			}
		}
	}

	private void scanColumn(ScanState scanState) {
		for (scanState.deltaX = -scanState.distance; scanState.deltaX <= 0; scanState.deltaX++)
		{
			scanState.currentX = calcCurrentX(scanState);
			scanState.currentY = calcCurrentY(scanState);

			if (!inRange(scanState)) { continue; }

			float centerSlope = calcCenterSlope(scanState);

			if (scanState.slopePair.getStartSlope() < centerSlope)
			{
				continue;
			} else if (scanState.slopePair.getEndSlope() > centerSlope)
			{
				break;
			}

			checkLocation(scanState.slopePair, scanState);
		}
	}

	private void checkLocation(SlopePair slopePair, ScanState scanState) {
		double currentRadius = calcRadius(scanState);
		addIfInRange(scanState, currentRadius);

		boolean currentBlocked = blocksVision(scanState);
		if (scanState.previouslyBlocked)
		{
			if (currentBlocked)
			{
				scanState.newStartSlope = calcRightSlope(scanState);
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
				float newLeftSlope = calcLeftSlope(scanState);
				SlopePair newSlopePair = new SlopePair(slopePair.startSlope, newLeftSlope);
				scanOctant(scanState.octant, scanState.distance + 1, newSlopePair);
				scanState.newStartSlope = calcRightSlope(scanState);
			}
		}
	}

	private int calcCurrentX(ScanState scanState) {

		int offset = 0;
		switch (scanState.octant)
		{
			case 1:
			case 6:
				offset = scanState.deltaX;
				break;
			case 2:
			case 5:
				offset = -scanState.deltaX;
				break;
			case 7:
			case 8:
				offset = scanState.deltaY;
				break;
			case 3:
			case 4:
				offset = -scanState.deltaY;
				break;
		}
		return centerX + offset;
	}

	private int calcCurrentY(ScanState scanState) {
		int offset = 0;
		switch (scanState.octant)
		{
			case 1:
			case 2:
				offset = scanState.deltaY;
				break;
			case 6:
			case 5:
				offset = -scanState.deltaY;
				break;
			case 3:
			case 8:
				offset = scanState.deltaX;
				break;
			case 7:
			case 4:
				offset = -scanState.deltaX;
				break;
		}
		return centerY + offset;
	}

	private boolean inRange(ScanState scanState) {
		Location loc = zone.getLocationOnGrid(scanState.currentX, scanState.currentY);
		return loc != null && scanState.currentX >= 0 && scanState.currentY >= 0 &&
			scanState.currentX <= this.width && scanState.currentY <= this.height;
	}

	private void addIfInRange(ScanState scanState, double rad) {
		if (rad <= radius)
		{
			Location loc = zone.getLocationOnGrid(scanState.currentX, scanState.currentY);
			visibleLocations.add(loc);
		}
	}

	private float calcCenterSlope(ScanState scanState) {
		Location loc = zone.getLocationOnGrid(scanState.currentX, scanState.currentY);
		double dx = Math.abs(centerX - loc.X_COORDINATE);
		double dy = Math.abs(centerY - loc.Y_COORDINATE);
		float slope = 0.0f;
		switch (scanState.octant)
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

	private float calcRightSlope(ScanState scanState) {
		Location loc = zone.getLocationOnGrid(scanState.currentX, scanState.currentY);
		Vector2D centerVector = LocationUtil.getCenter(center);
		Vector2D brushCorner = LocationUtil.getCenter(loc);
		switch (scanState.octant)
		{
			case 1:
			case 4:
				brushCorner = LocationUtil.getSouthEastCorner(loc);
				break;
			case 2:
			case 7:
				brushCorner = LocationUtil.getSouthWestCorner(loc);
				break;
			case 3:
			case 6:
				brushCorner = LocationUtil.getNorthEastCorner(loc);
				break;
			case 5:
			case 8:
				brushCorner = LocationUtil.getNorthWestCorner(loc);
				break;
		}
		return calcSlopeInOctant(scanState.octant, centerVector, brushCorner);
	}

	private float calcLeftSlope(ScanState scanState) {
		Location loc = zone.getLocationOnGrid(scanState.currentX, scanState.currentY);
		Vector2D centerVector = LocationUtil.getCenter(center);
		Vector2D brushCorner = LocationUtil.getCenter(loc);
		switch (scanState.octant)
		{
			case 1:
			case 4:
				brushCorner = LocationUtil.getNorthWestCorner(loc);
				break;
			case 2:
			case 7:
				brushCorner = LocationUtil.getNorthEastCorner(loc);
				break;
			case 3:
			case 6:
				brushCorner = LocationUtil.getSouthWestCorner(loc);
				break;
			case 5:
			case 8:
				brushCorner = LocationUtil.getSouthEastCorner(loc);
				break;
		}
		return calcSlopeInOctant(scanState.octant, centerVector, brushCorner);
	}

	public float calcRadius(ScanState scanState) {
		double dx2 = scanState.deltaX * scanState.deltaX;
		double dy2 = scanState.deltaY * scanState.deltaY;
		double sum = dx2 + dy2;
		return (float) Math.sqrt(sum);
	}

	public boolean blocksVision(ScanState scanState) {
		Location loc = zone.getLocationOnGrid(scanState.currentX, scanState.currentY);
		return loc == null || "true".equals(loc.getProperties().getProperty("blocksVision"));
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
		public SlopePair slopePair;

		public boolean shouldContinue() { return distance <= radius && !previouslyBlocked; }
	}
}
