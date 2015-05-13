package com.jenjinstudios.world.math;

/**
 * Stores a set of x and y coordinates.
 * @author Caleb Brinkman
 */
public class Vector2D
{
	/** The vector representing (0,0). */
	public static final Vector2D ORIGIN = new Vector2D(0, 0);
	/** The x coordinate. */
	private final double xCoordinate;
	/** The y coordinate. */
	private final double yCoordinate;

	/**
	 * Construct a new set of vector2D copying another set.
	 * @param vector2D The vector2D to copy.
	 */
	public Vector2D(Vector2D vector2D) {
		this(vector2D.getXValue(), vector2D.getYValue());
	}

	/**
	 * Construct a new set of coordinates.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 */
	public Vector2D(double x, double y) {
		xCoordinate = x;
		yCoordinate = y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Vector2D)) return false;

		Vector2D vector2D = (Vector2D) o;

		return Double.compare(vector2D.xCoordinate, xCoordinate) == 0 &&
			Double.compare(vector2D.yCoordinate, yCoordinate) == 0;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		temp = Double.doubleToLongBits(xCoordinate);
		result = (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(yCoordinate);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * Get the y coordinate.
	 * @return The y coordinate.
	 */
	public double getYValue() {
		return yCoordinate;
	}

	/**
	 * Get the x coordinate.
	 * @return The x coordinate.
	 */
	public double getXValue() {
		return xCoordinate;
	}


	@Override
	public String toString() {
		return String.format("(%.3f, %.3f)", xCoordinate, yCoordinate);
	}

	/**
	 * Get a Vector2D the specified distance away from the current vector at the specified angle in radians.
	 * @param distance The distance.
	 * @param angle The angle in radians.
	 * @return The new Vector2D;
	 */
	public Vector2D getVectorInDirection(double distance, double angle) {
		Vector2D vector2D;
		if (angle != Angle.IDLE)
		{
			double cos = Math.cos(angle);
			double sin = Math.sin(angle);
			double newX = MathUtil.round(xCoordinate + (distance * cos), 4);
			double newY = MathUtil.round(yCoordinate + (distance * sin), 4);
			vector2D = new Vector2D(newX, newY);
		} else
		{
			vector2D = new Vector2D(this);
		}
		return vector2D;
	}

	/**
	 * Get the angle to the given vector.
	 * @param vector2D The vector toward which the return will point.
	 * @return The angle to the supplied vector.
	 */
	public double getAngleToVector(Vector2D vector2D) {
		Double angle = Double.NEGATIVE_INFINITY;
		if (!vector2D.equals(this))
		{
			double xDist = vector2D.getXValue() - xCoordinate;
			double yDist = vector2D.getYValue() - yCoordinate;
			angle = java.lang.Math.atan2(yDist, xDist);
		}
		return angle;
	}

	/**
	 * Get the distance to the given vector.
	 * @param vector2D The vector.
	 * @return The distance.
	 */
	public double getDistanceToVector(Vector2D vector2D) {
		double squaredDistance = getSquaredDistanceToVector(vector2D);
		return MathUtil.round(java.lang.Math.sqrt(squaredDistance), 4);
	}

	public double getSquaredDistanceToVector(Vector2D vector2D) {
		double xSquare = java.lang.Math.pow(vector2D.getXValue() - xCoordinate, 2);
		double ySquare = java.lang.Math.pow(vector2D.getYValue() - yCoordinate, 2);
		return xSquare + ySquare;
	}
}
