package com.jenjinstudios.world.math;

import java.util.Objects;

/**
 * Represents a pair of spatial coordinates; can be used to represent position or (rectangular) size.
 *
 * @author Caleb Brinkman
 */
public interface Vector
{
	/**
	 * Represents a Vector that returns {@code 0} for all values (the origin).
	 */
	@SuppressWarnings(
		  {
				"AnonymousInnerClassWithTooManyMethods",
				"AnonymousInnerClass",
				"OverlyComplexAnonymousInnerClass"
		  }
	)
	Vector ORIGIN = new Vector()
	{
		@Override
		public double getXValue() { return 0; }

		@Override
		public double getYValue() { return 0; }

		@Override
		public double getZValue() { return 0; }

		@Override
		public double getSquaredDistanceToVector(Vector vector) {
			return Objects.equals(vector, this) ? 0 : vector.getSquaredDistanceToVector(this);
		}

		@Override
		public double getDistanceToVector(Vector vector) {
			double squaredDistance = getSquaredDistanceToVector(vector);
			return MathUtil.round(Math.sqrt(squaredDistance), 4);
		}
	};

	/**
	 * Get the X value of this vector.
	 *
	 * @return The X value of this vector.
	 */
	default double getXValue() { throw new UnsupportedOperationException("X Values Not Supported"); }

	/**
	 * Get the Y value of this vector.
	 *
	 * @return The Y value of this vector.
	 */
	default double getYValue() {throw new UnsupportedOperationException("Y Values Not Supported"); }

	/**
	 * Get the Z value of this vector.
	 *
	 * @return The Z value of this vector.
	 */
	default double getZValue() { throw new UnsupportedOperationException("Z Values Not Supported"); }

	/**
	 * Get the distance to a vector, squared; this is more performant than retrieving the actual distance and is useful
	 * for comparison.
	 *
	 * @param vector The vector for which to return the distance from this vector squared.
	 *
	 * @return The distance from this vector to the supplied vector, squared.
	 */
	double getSquaredDistanceToVector(Vector vector);

	/**
	 * Get the distance to the given vector.
	 *
	 * @param vector The vector.
	 *
	 * @return The distance.
	 */
	default double getDistanceToVector(Vector vector) {
		double squaredDistance = getSquaredDistanceToVector(vector);
		return MathUtil.round(Math.sqrt(squaredDistance), 4);
	}

	/**
	 * Get a Vector the specified distance away from the current vector at the specified angle in radians.
	 *
	 * @param distance The distance.
	 * @param angle The angle in radians.
	 *
	 * @return The new Vector;
	 */
	default Vector getVectorInDirection(double distance, double angle) {
		throw new UnsupportedOperationException("Not supported");
	}
}
