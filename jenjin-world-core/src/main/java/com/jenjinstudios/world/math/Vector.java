package com.jenjinstudios.world.math;

/**
 * Represents a pair of spatial coordinates; can be used to represent position or (rectangular) size.
 *
 * @author Caleb Brinkman
 */
public class Vector
{
	private final double xValue;
	private final double yValue;
	private final double zValue;

	/**
	 * Construct a new Vector with the given x, y, and z values.
	 * @param x The x value.
	 * @param y The y value.
	 * @param z The z value.
	 */
	public Vector(double x, double y, double z) {
		this.xValue = x;
		this.yValue = y;
		this.zValue = z;
	}

	/**
	 * Get the X value of this vector.
	 *
	 * @return The X value of this vector.
	 */
	public double getXValue() { return xValue; }

	/**
	 * Get the Y value of this vector.
	 *
	 * @return The Y value of this vector.
	 */
	public double getYValue() { return yValue; }

	/**
	 * Get the Z value of this vector.
	 *
	 * @return The Z value of this vector.
	 */
	public double getZValue() { return zValue; }

	/**
	 * Get the distance to a vector, squared; this is more performant than retrieving the actual distance and is useful
	 * for comparison.
	 *
	 * @param vector The vector for which to return the distance from this vector squared.
	 *
	 * @return The distance from this vector to the supplied vector, squared.
	 */
	public double getSquaredDistanceToVector(Vector vector) {
		double xd = vector.getXValue() - xValue;
		double yd = vector.getYValue() - yValue;
		double zd = vector.getZValue() - zValue;
		return (xd * xd) + (yd * yd) + (zd * zd);
	}

	/**
	 * Get the distance to the given vector.
	 *
	 * @param vector The vector.
	 *
	 * @return The distance.
	 */
	public double getDistanceToVector(Vector vector) {
		double squaredDistance = getSquaredDistanceToVector(vector);
		return Math.sqrt(squaredDistance);
	}

	/**
	 * Get a Vector the specified distance away from the current vector at the specified angle in radians.
	 *
	 * @param distance The distance.
	 * @param orientation The orientation relative to this vector.
	 *
	 * @return The new Vector.
	 */
	public Vector getVectorInDirection(double distance, Orientation orientation) {
		double zAngleSin = Math.sin(orientation.getzAngle());
		double xUnit = Math.cos(orientation.getXyAngle()) * zAngleSin;
		double yUnit = Math.sin(orientation.getXyAngle()) * zAngleSin;
		double zUnit = Math.cos(orientation.getzAngle());

		double xVal = xValue + (distance * xUnit);
		double yVal = yValue + (distance * yUnit);
		double zVal = zValue + (distance * zUnit);

		return new Vector(xVal, yVal, zVal);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if ((obj == null) || (getClass() != obj.getClass())) return false;

		Vector vector = (Vector) obj;

		if (Double.compare(vector.getXValue(), xValue) != 0) return false;
		if (Double.compare(vector.getYValue(), yValue) != 0) return false;
		return Double.compare(vector.getZValue(), zValue) == 0;

	}

	@Override
	public int hashCode() {
		long temp = Double.doubleToLongBits(xValue);
		int result = (int) (temp ^ (temp >>> 32));
		long temp2 = Double.doubleToLongBits(yValue);
		result = (31 * result) + (int) (temp2 ^ (temp2 >>> 32));
		long temp3 = Double.doubleToLongBits(zValue);
		result = (31 * result) + (int) (temp3 ^ (temp3 >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return "Vector{" +
			  "xValue=" + xValue +
			  ", yValue=" + yValue +
			  ", zValue=" + zValue +
			  '}';
	}

}
