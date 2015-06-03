package com.jenjinstudios.world;

/**
 * Represents a three dimensional integer point.
 *
 * @author Caleb Brinkman
 */
class Point
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
	Point(int xCoordinate, int yCoordinate, int zCoordinate)
	{
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
		this.zCoordinate = zCoordinate;
	}

	public int getxCoordinate() {
		return xCoordinate;
	}

	public int getyCoordinate() {
		return yCoordinate;
	}

	public int getzCoordinate() {
		return zCoordinate;
	}
}
