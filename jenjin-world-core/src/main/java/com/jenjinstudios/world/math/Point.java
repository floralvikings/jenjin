package com.jenjinstudios.world.math;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a three dimensional integer point.  Because every Cell in the World has one point, this is likely to be
 * one of the most instantiated classes in the program.  As a result, it utilizes a simple bit shifting algorithm to
 * store each (immutable) Point in a HashMap
 *
 * @author Caleb Brinkman
 */
public class Point
{
	private static final Map<Long, Point> POINTS = new HashMap<>(Short.MAX_VALUE * 6);
	private static final int X_SHIFT = 32;
	private static final int Y_SHIFT = 16;
	private static final long BIT_MASK = 0xffffffffL;
	private final short xCoordinate;
	private final short yCoordinate;
	private final short zCoordinate;

	/**
	 * Get the point with the given coordinates.
	 *
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @param z The z coordinate.
	 *
	 * @return The point with the specified coordinates.
	 */
	public static Point getPoint(short x, short y, short z) {
		long longFromCoords = longFromCoords(x, y, z);
		Point point = POINTS.get(longFromCoords);
		if (point == null) {
			point = new Point(x, y, z);
			POINTS.put(longFromCoords, point);
		}
		return point;
	}

	/**
	 * Construct a new Point with the given coordinates.
	 *
	 * @param xCoordinate The X coordinate of the cell.
	 * @param yCoordinate The Y coordinate of the cell.
	 * @param zCoordinate The Z coordinate of the cell.
	 */
	private Point(short xCoordinate, short yCoordinate, short zCoordinate)
	{
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
		this.zCoordinate = zCoordinate;
	}

	/**
	 * Get the x coordinate of this point.
	 *
	 * @return The x coordinate of this point.
	 */
	public short getXCoordinate() { return xCoordinate; }

	/**
	 * Get the y coordinate of this point.
	 *
	 * @return The y coordinate of this point.
	 */
	public short getYCoordinate() { return yCoordinate; }

	/**
	 * Get the z coordinate of this point.
	 *
	 * @return The z coordinate of this point.
	 */
	public short getZCoordinate() { return zCoordinate; }

	/**
	 * Determine whether the specified point is adjacent to this one.
	 *
	 * @param point The point to test for adjacence.
	 *
	 * @return Whether the points are adjacent.
	 */
	public boolean isAdjacentTo(Point point) {
		// PERFORMANCE This could probably be improved by short-circuiting each of the tests.
		boolean xAdj = Math.abs(xCoordinate - point.getXCoordinate()) <= 1;
		boolean yAdj = Math.abs(yCoordinate - point.getYCoordinate()) <= 1;
		boolean zAdj = Math.abs(zCoordinate - point.getZCoordinate()) <= 1;
		return xAdj && yAdj && zAdj;
	}

	private static long longFromCoords(short x, short y, short z) {
		long xShift = (long) x << X_SHIFT;
		long yShift = (y & BIT_MASK) << Y_SHIFT;
		long zShift = z & BIT_MASK;
		return xShift | yShift | zShift;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if ((obj == null) || (getClass() != obj.getClass())) return false;

		Point point = (Point) obj;

		if (xCoordinate != point.getXCoordinate()) return false;
		if (yCoordinate != point.getYCoordinate()) return false;
		return zCoordinate == point.getZCoordinate();

	}

	@Override
	public int hashCode() {
		int result = xCoordinate;
		result = (31 * result) + yCoordinate;
		result = (31 * result) + zCoordinate;
		return result;
	}

	@Override
	public String toString() {
		return "Point{" +
			  "xCoordinate=" + xCoordinate +
			  ", yCoordinate=" + yCoordinate +
			  ", zCoordinate=" + zCoordinate +
			  '}';
	}
}
