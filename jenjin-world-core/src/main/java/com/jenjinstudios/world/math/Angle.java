package com.jenjinstudios.world.math;

/**
 * @author Caleb Brinkman
 */
public class Angle
{
	/** The constant used for an "idle" move state. */
	public static final double IDLE = Double.NEGATIVE_INFINITY;
	/** The forward state. */
	public static final double FRONT = Math.PI * 0;
	/** The forward-right state. */
	public static final double FRONT_RIGHT = Math.PI * -0.25;
	/** The right state. */
	public static final double RIGHT = Math.PI * -0.5;
	/** The back-right state. */
	public static final double BACK_RIGHT = Math.PI * -0.75;
	/** The backward state. */
	public static final double BACK = Math.PI;
	/** The back-left state. */
	public static final double BACK_LEFT = Math.PI * 0.75;
	/** The left state. */
	public static final double LEFT = Math.PI * 0.5;
	/** The front-left state. */
	public static final double FRONT_LEFT = Math.PI * 0.25;
	private double absoluteAngle;
	private double relativeAngle;

	public Angle(double absoluteAngle) {
		this(absoluteAngle, IDLE);
	}

	public Angle(double absoluteAngle, double relativeAngle) {
		this.absoluteAngle = absoluteAngle;
		this.relativeAngle = relativeAngle;
	}
}
