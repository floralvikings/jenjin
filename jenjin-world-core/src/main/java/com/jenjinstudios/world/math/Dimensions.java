package com.jenjinstudios.world.math;

/**
 * Represents width, depth, and height in 3D space.
 */
public class Dimensions
{
	private final int width;
	private final int depth;
	private final int height;

	/**
	 * Construct a new dimension with the given width, depth, and height.
	 *
	 * @param width The width.
	 * @param depth The depth.
	 * @param height The height.
	 */
	public Dimensions(int width, int depth, int height)
	{
		this.width = width;
		this.depth = depth;
		this.height = height;
	}

	/**
	 * Get the width of this dimension.
	 *
	 * @return The width of this dimension.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get the depth of this dimension.
	 *
	 * @return The depth of this dimension.
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * Get the height of this dimension.
	 *
	 * @return The height of this dimension.
	 */
	public int getHeight() {
		return height;
	}
}
