package com.jenjinstudios.world.math;

/**
 * Stores a set of x and y coordinates.
 *
 * @author Caleb Brinkman
 */
public class Vector2D implements Vector
{
	/** The vector representing (0,0). */
	public static final Vector2D ORIGIN = new Vector2D(0, 0);
	private final double xValue;
	private final double yValue;

	/**
	 * Construct a new set of vector2D copying another set.
	 *
	 * @param vector2D The vector2D to copy.
	 */
	public Vector2D(Vector vector2D) {
		this(vector2D.getXValue(), vector2D.getYValue());
	}

	/**
	 * Construct a new set of coordinates.
	 *
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 */
	public Vector2D(double x, double y) {
		xValue = x;
		yValue = y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Vector2D)) return false;

		Vector vector2D = (Vector) o;
		return (Double.compare(vector2D.getXValue(), xValue) == 0) &&
			  (Double.compare(vector2D.getYValue(), yValue) == 0);
	}

	@Override
	public int hashCode() {
		long temp = Double.doubleToLongBits(xValue);
		int result = (int) (temp ^ (temp >>> 32));
		long newTemp = Double.doubleToLongBits(yValue);
		result = (31 * result) + (int) (newTemp ^ (newTemp >>> 32));
		return result;
	}

	/**
	 * Get the y coordinate.
	 *
	 * @return The y coordinate.
	 */
	@Override
	public double getYValue() {
		return yValue;
	}

	/**
	 * Get the x coordinate.
	 *
	 * @return The x coordinate.
	 */
	@Override
	public double getXValue() {
		return xValue;
	}


	@Override
	public String toString() {
		return String.format("(%.3f, %.3f)", xValue, yValue);
	}

	/**
	 * Get a Vector2D the specified distance away from the current vector at the specified angle in radians.
	 *
	 * @param distance The distance.
	 * @param angle The angle in radians.
	 *
	 * @return The new Vector2D;
	 */
	public Vector2D getVectorInDirection(double distance, double angle) {
		Vector2D vector2D;
		if (Double.compare(angle, Angle.IDLE) == 0) {
			vector2D = new Vector2D(this);
		} else {
			double cos = StrictMath.cos(angle);
			double sin = StrictMath.sin(angle);
			double newX = MathUtil.round(xValue + (distance * cos), 4);
			double newY = MathUtil.round(yValue + (distance * sin), 4);
			vector2D = new Vector2D(newX, newY);
		}
		return vector2D;
	}

	/**
	 * Get the angle to the given vector.
	 *
	 * @param vector The vector toward which the return will point.
	 *
	 * @return The angle to the supplied vector.
	 */
	public double getAngleToVector(Vector vector) {
		Double angle = Double.NEGATIVE_INFINITY;
		if (!vector.equals(this)) {
			double xDist = vector.getXValue() - xValue;
			double yDist = vector.getYValue() - yValue;
			angle = StrictMath.atan2(yDist, xDist);
		}
		return angle;
	}

	/**
	 * Get the distance to a vector, squared; this is more performant than retrieving the actual distance and is useful
	 * for comparison.
	 *
	 * @param vector The vector for which to return the distance from this vector squared.
	 *
	 * @return The distance from this vector to the supplied vector, squared.
	 */
	@Override
	public double getSquaredDistanceToVector(Vector vector) {
		double xSquare = StrictMath.pow(vector.getXValue() - xValue, 2);
		double ySquare = StrictMath.pow(vector.getYValue() - yValue, 2);
		return xSquare + ySquare;
	}
}
