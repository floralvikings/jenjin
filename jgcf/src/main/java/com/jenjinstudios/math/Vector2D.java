package com.jenjinstudios.math;

/**
 * Stores a set of x and z coordinates.
 *
 * @author Caleb Brinkman
 */
public class Vector2D
{
	/** The x coordinate. */
	private double xCoordinate;
	/** The z coordinate. */
	private double zCoordinate;

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

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Vector2D))
			return false;
		Vector2D vector2D = (Vector2D) obj;
		return vector2D.xCoordinate == xCoordinate && vector2D.zCoordinate == zCoordinate;
	}

	/**
	 * Get a Vector2D the specified distance away from the current vector at the specified angle in radians.
	 *
	 * @param distance The distance.
	 * @param angle    The angle in radians.
	 * @return The new Vector2D;
	 */
	public Vector2D getVectorInDirection(double distance, double angle)
	{
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		double newX = Round.round(xCoordinate + (distance * cos), 4);
		double newZ = Round.round(zCoordinate + (distance * sin), 4);
		return new Vector2D(newX, newZ);
	}

	@Override
	public String toString()
	{
		return "(" + xCoordinate + ", " + zCoordinate + ")";
	}
}
