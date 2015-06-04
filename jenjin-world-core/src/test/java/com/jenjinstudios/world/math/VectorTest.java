package com.jenjinstudios.world.math;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests the Vector class; specifically, the 3D math functionality.
 *
 * @author Caleb Brinkman
 */
public class VectorTest
{
	/**
	 * Tests the getVectorInDirection method.
	 */
	@Test
	public void testGetVectorInDirection() {
		Vector origin = new Vector(0, 0, 0);

		double randDist = Math.random() * 10;
		double randXyAngle = Math.random() * Math.PI;
		double randZAngle = Math.random() * Math.PI;

		Orientation orientation = new Orientation(randXyAngle, randZAngle);

		Vector resultVector = origin.getVectorInDirection(randDist, orientation);
		double resultDistance = origin.getDistanceToVector(resultVector);

		Assert.assertEquals(resultDistance, randDist, 0.01D, "Result should be one unit distant.");
	}

	/**
	 * Test the equals and hashCode methods.
	 */
	@Test
	public void testEqualsAndHashCode() {
		double randX = Math.random();
		double randY = Math.random();
		double randZ = Math.random();

		Vector vector1 = new Vector(randX, randY, randZ);
		Vector vector2 = new Vector(randX, randY, randZ);

		Assert.assertEquals(vector1, vector2, "Vectors should be equal");
		Assert.assertEquals(vector1.hashCode(), vector2.hashCode(), "Hash codes should be equal");
	}

	/**
	 * Test the toString method.
	 */
	@Test
	public void testToString() {
		double randX = Math.random();
		double randY = Math.random();
		double randZ = Math.random();

		Vector vector = new Vector(randX, randY, randZ);
		String vectorString = "Vector{" +
			  "xValue=" + vector.getXValue() +
			  ", yValue=" + vector.getYValue() +
			  ", zValue=" + vector.getZValue() +
			  '}';

		Assert.assertEquals(vector.toString(), vectorString, "Vector strings should be equal.");
	}
}
