package com.jenjinstudios.world.math;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Math.*;

/**
 * Represents an orientation in 3D space using yaw (the angle on the xz plane) and pitch (the angle on the yz plane).
 *
 * @author Caleb Brinkman
 */
public class Orientation
{
	private static final double PI_OVER_TWO = PI * 0.5;
	/** Represents an orientation that points straight north. */
	public static final Orientation NORTH = new Orientation(PI_OVER_TWO, 0);
	/** Represents an orientation that points straight east. */
	public static final Orientation EAST = new Orientation(0, 0);
	/** Represents an orientation that points nowhere; useful for speficying a "directionless" state. */
	public static final Orientation NOWHERE = new Orientation(NEGATIVE_INFINITY, NEGATIVE_INFINITY);
	private final double pitch;
	private final double yaw;

	/**
	 * Construct a new Orientation with the given rotation on the x, y, and z axes.
	 *
	 * @param yaw The angle on the xz plane (starting from the x-axis, counterclockwise) in radians.
	 * @param pitch The angle on the yz plane (starting from the z-axis, counterclockwise) in radians.
	 */
	public Orientation(double yaw, double pitch) {
		this.pitch = pitch;
		this.yaw = yaw;
	}

	/**
	 * Get the angle of this orientation on the yz plane.
	 *
	 * @return The rotation of this orientation on the yz plane.
	 */
	public double getPitch() { return pitch; }

	/**
	 * Get the angle of this orientation on the xz plane.
	 *
	 * @return The angle of this orientation on the yz plane.
	 */
	public double getYaw() { return yaw; }

	/**
	 * Get the directional unit vector corresponding to this Orientation.
	 *
	 * @return The directional unit vector corresponding to this Orientation.
	 */
	public Vector getDirectionalVector() {
		double x = cos(yaw) * cos(pitch);
		double y = sin(pitch);
		double z = sin(yaw) * cos(pitch);
		return new Vector(x, y, z);
	}

	@Override
	public String toString() {
		return "Orientation{" +
			  "pitch=" + pitch +
			  ", yaw=" + yaw +
			  '}';
	}
}
