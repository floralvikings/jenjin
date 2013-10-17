package com.jenjinstudios.math;

import com.jenjinstudios.world.state.MoveState;

/**
 * Stores a set of x and z coordinates.
 *
 * @author Caleb Brinkman
 */
public class Vector2D
{
	/** The vector representing (0,0). */
	public static final Vector2D ORIGIN = new Vector2D(0, 0);
	/** The x coordinate. */
	private double xCoordinate;
	/** The z coordinate. */
	private double zCoordinate;

	/**
	 * Construct a new set of vector2D copying another set.
	 *
	 * @param vector2D The vector2D to copy.
	 */
	public Vector2D(Vector2D vector2D)
	{
		this(vector2D.getXCoordinate(), vector2D.getZCoordinate());
	}

	/**
	 * Get the z coordinate.
	 *
	 * @return The z coordinate.
	 */
	public double getZCoordinate()
	{
		return zCoordinate;
	}

	/**
	 * Set the z coordinate.
	 *
	 * @param zCoordinate The new Z coordinate.
	 */
	public void setZCoordinate(double zCoordinate)
	{
		this.zCoordinate = zCoordinate;
	}

	/**
	 * Get the x coordinate.
	 *
	 * @return The x coordinate.
	 */
	public double getXCoordinate()
	{
		return xCoordinate;
	}

	/**
	 * Set the x coordinate.
	 *
	 * @param xCoordinate The new x coordinate.
	 */
	public void setXCoordinate(double xCoordinate)
	{
		this.xCoordinate = xCoordinate;
	}

	/**
	 * Construct a new set of coordinates.
	 *
	 * @param x The x coordinate.
	 * @param y The z coordinate.
	 */
	public Vector2D(double x, double y)
	{
		xCoordinate = x;
		zCoordinate = y;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Vector2D))
			return false;
		Vector2D vector2D = (Vector2D) obj;
		return vector2D.xCoordinate == xCoordinate && vector2D.zCoordinate == zCoordinate;
	}

	@Override
	public String toString()
	{
		return "(" + xCoordinate + ", " + zCoordinate + ")";
	}

	/**
	 * Get a Vector2D the specified distance away from the current vector at the specified angle in radians.
	 *
	 * @param distance The distance.
	 * @param angle    The angle in radians.
	 *
	 * @return The new Vector2D;
	 */
	public Vector2D getVectorInDirection(double distance, double angle)
	{
		if (angle == MoveState.IDLE) return new Vector2D(this);
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		double newX = Round.round(xCoordinate + (distance * cos), 4);
		double newZ = Round.round(zCoordinate + (distance * sin), 4);
		return new Vector2D(newX, newZ);
	}

	/**
	 * Get the angle to the given vector.
	 *
	 * @param vector2D The vector toward which the return will point.
	 *
	 * @return The angle to the supplied vector.
	 */
	public double getAngleToVector(Vector2D vector2D)
	{
		double xDist = vector2D.getXCoordinate() - xCoordinate;
		double zDist = vector2D.getZCoordinate() - zCoordinate;
		return Math.atan2(zDist, xDist);
	}

	/**
	 * Get the distance to the given vector.
	 *
	 * @param vector2D The vector.
	 *
	 * @return The distance.
	 */
	public double getDistanceToVector(Vector2D vector2D)
	{
		double xSquare = Math.pow(vector2D.getXCoordinate() - xCoordinate, 2);
		double zSquare = Math.pow(vector2D.getZCoordinate() - zCoordinate, 2);
		return Math.sqrt(xSquare + zSquare);
	}
}
