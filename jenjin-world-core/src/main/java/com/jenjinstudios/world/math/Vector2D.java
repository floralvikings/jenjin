package com.jenjinstudios.world.math;

import com.jenjinstudios.world.state.MoveState;

/**
 * Stores a set of x and y coordinates.
 * @author Caleb Brinkman
 */
public class Vector2D
{
	/** The vector representing (0,0). */
	public static final Vector2D ORIGIN = new Vector2D(0, 0);
	/** The x coordinate. */
	private double xCoordinate;
	/** The y coordinate. */
	private double yCoordinate;

	/**
	 * Construct a new set of vector2D copying another set.
	 * @param vector2D The vector2D to copy.
	 */
	public Vector2D(Vector2D vector2D) {
		this(vector2D.getXCoordinate(), vector2D.getYCoordinate());
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

	/**
	 * Get the y coordinate.
	 * @return The y coordinate.
	 */
	public double getYCoordinate() {
		return yCoordinate;
	}

	/**
	 * Get the x coordinate.
	 * @return The x coordinate.
	 */
	public double getXCoordinate() {
		return xCoordinate;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Vector2D))
			return false;
		Vector2D vector2D = (Vector2D) obj;
		return vector2D.xCoordinate == xCoordinate && vector2D.yCoordinate == yCoordinate;
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
		if (angle == MoveState.IDLE) return new Vector2D(this);
		double cos = java.lang.Math.cos(angle);
		double sin = java.lang.Math.sin(angle);
		double newX = MathUtil.round(xCoordinate + (distance * cos), 4);
		double newY = MathUtil.round(yCoordinate + (distance * sin), 4);
		return new Vector2D(newX, newY);
	}

	/**
	 * Get the angle to the given vector.
	 * @param vector2D The vector toward which the return will point.
	 * @return The angle to the supplied vector.
	 */
	public double getAngleToVector(Vector2D vector2D) {
		if (vector2D.equals(this))
		{
			// Negative infinity specifies that the vectors are the same (can't get an angle).
			return Double.NEGATIVE_INFINITY;
		}
		double xDist = vector2D.getXCoordinate() - xCoordinate;
		double yDist = vector2D.getYCoordinate() - yCoordinate;
		return java.lang.Math.atan2(yDist, xDist);
	}

	/**
	 * Get the distance to the given vector.
	 * @param vector2D The vector.
	 * @return The distance.
	 */
	public double getDistanceToVector(Vector2D vector2D) {
		double xSquare = java.lang.Math.pow(vector2D.getXCoordinate() - xCoordinate, 2);
		double ySquare = java.lang.Math.pow(vector2D.getYCoordinate() - yCoordinate, 2);
		return MathUtil.round(java.lang.Math.sqrt(xSquare + ySquare), 4);
	}
}
