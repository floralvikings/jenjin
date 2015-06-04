package com.jenjinstudios.world.math;

/**
 * Represents a three dimensional integer point.
 *
 * @author Caleb Brinkman
 */
public class Point
{
	private final int xCoordinate;
	private final int yCoordinate;
	private final int zCoordinate;

	/**
	 * Construct a new Point with the given coordinates.
	 *
	 * @param xCoordinate The X coordinate of the cell.
	 * @param yCoordinate The Y coordinate of the cell.
	 * @param zCoordinate The Z coordinate of the cell.
	 */
	public Point(int xCoordinate, int yCoordinate, int zCoordinate)
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
	public int getxCoordinate() {
		return xCoordinate;
	}

	/**
	 * Get the y coordinate of this point.
	 *
	 * @return The y coordinate of this point.
	 */
	public int getyCoordinate() {
		return yCoordinate;
	}

	/**
	 * Get the z coordinate of this point.
	 *
	 * @return The z coordinate of this point.
	 */
	public int getzCoordinate() {
		return zCoordinate;
	}
}
