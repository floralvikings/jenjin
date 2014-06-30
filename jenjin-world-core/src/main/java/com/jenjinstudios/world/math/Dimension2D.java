package com.jenjinstudios.world.math;

public class Dimension2D
{
	private final int xSize;
	private final int ySize;

	/**
	 * @param xSize The x length of the zone.
	 * @param ySize The y length of zone.
	 */
	public Dimension2D(int xSize, int ySize) {
		this.xSize = xSize;
		this.ySize = ySize;
	}

	public int getXSize() {
		return xSize;
	}

	public int getYSize() {
		return ySize;
	}
}
