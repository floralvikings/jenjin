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

	public Angle() {
		this(0.0, IDLE);
	}

	public Angle(double absoluteAngle) {
		this(absoluteAngle, IDLE);
	}

	public Angle(double absoluteAngle, double relativeAngle) {
		this.absoluteAngle = absoluteAngle;
		this.relativeAngle = relativeAngle;
	}

	public double getRelativeAngle() {
		return relativeAngle;
	}

	public double getAbsoluteAngle() {
		return absoluteAngle;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Angle)) return false;

		Angle angle = (Angle) o;

		return Double.compare(angle.absoluteAngle, absoluteAngle) == 0 && Double.compare(angle.relativeAngle,
			  relativeAngle) == 0;

	}

	public double getStepAngle() {
		double sAngle = relativeAngle != IDLE ? absoluteAngle + relativeAngle : IDLE;
		return (sAngle < 0) ? (sAngle + (Math.PI * 2)) : (sAngle % (Math.PI * 2));
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		temp = Double.doubleToLongBits(absoluteAngle);
		result = (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(relativeAngle);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	public Angle asIdle() {
		return new Angle(this.absoluteAngle, IDLE);
	}

	public boolean isNotIdle() { return relativeAngle != IDLE; }

	public double reverseStepAngle() {
		double sAngle = relativeAngle != IDLE ? absoluteAngle + relativeAngle - Math.PI : IDLE;
		return (sAngle < 0) ? (sAngle + (Math.PI * 2)) : (sAngle % (Math.PI * 2));
	}

	public String toString() {
		return "(" + relativeAngle + ", " + absoluteAngle + ")";
	}
}
