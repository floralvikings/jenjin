package com.jenjinstudios.world.math;

/**
 * Represents an orientation in 3D space; that is to say, the rotation in radians on the x, y, and z axes.
 *
 * @author Caleb Brinkman
 */
public class Orientation
{
	private final double xyAngle;
	private final double zAngle;

	/**
	 * Construct a new Orientation with the given rotation on the x, y, and z axes.
	 *
	 * @param xyAngle The rotation on the x axis.
	 * @param zAngle The rotation on the z axis.
	 */
	public Orientation(double xyAngle, double zAngle) {
		this.xyAngle = xyAngle;
		this.zAngle = zAngle;
	}

	/**
	 * Get the rotation of this orientation on the x axis.
	 *
	 * @return The rotation of this orientation on the x axis.
	 */
	public double getXyAngle() { return xyAngle; }

	/**
	 * Get the rotation of this orientation on the z axis.
	 *
	 * @return The rotation of this orientatino on the z axis.
	 */
	public double getzAngle() { return zAngle; }
}
