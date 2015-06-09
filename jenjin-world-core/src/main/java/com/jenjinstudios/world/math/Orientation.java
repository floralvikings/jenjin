package com.jenjinstudios.world.math;

import static java.lang.Double.NEGATIVE_INFINITY;

/**
 * Represents an orientation in 3D space using yaw (the angle on the xz plane) and pitch (the angle on the yz plane).
 *
 * @author Caleb Brinkman
 */
public class Orientation
{
	/** Represents an orientation that points nowhere. */
	public static final Orientation SELF_ORIENTATION = new Orientation(NEGATIVE_INFINITY, NEGATIVE_INFINITY);
	private static final double DELTA = 5.96e-08;
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
	 * Returns whether this orientation is a cardinal orientation.
	 *
	 * @return Whether this orientation is a cardinal orientation.
	 */
	public boolean isCardinalOrOrdinal() {
		return (Math.abs(pitch % (Math.PI / 4)) < DELTA) && (Math.abs(yaw % (Math.PI / 4)) < DELTA);
	}

	@Override
	public String toString() {
		return "Orientation{" +
			  "pitch=" + pitch +
			  ", yaw=" + yaw +
			  '}';
	}
}
