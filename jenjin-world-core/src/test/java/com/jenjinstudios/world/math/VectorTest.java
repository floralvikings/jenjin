package com.jenjinstudios.world.math;

import org.testng.Assert;
import org.testng.annotations.Test;

import static java.lang.Math.*;

/**
 * Tests the Vector class; specifically, the 3D math functionality.
 *
 * @author Caleb Brinkman
 */
public class VectorTest
{
	private static final double ROOT_2 = 1.4142135623730951;
	private static final double ROOT_3 = 1.7320508075688772;

	/**
	 * Tests the getVectorInDirection method.
	 */
	@Test
	public void testGetVectorInDirection() {
		Vector origin = new Vector(0, 0, 0);

		Orientation north = new Orientation(PI * 0.5, 0);
		Vector northVector = origin.getVectorInDirection(1.0, north);
		Assert.assertEquals(northVector, new Vector(0.0, 0.0, 1.0), "Vectors should be equal");

		Orientation south = new Orientation(PI * 1.5, 0);
		Vector southVector = origin.getVectorInDirection(1.0, south);
		Assert.assertEquals(southVector, new Vector(0.0, 0.0, -1.0), "Vectors should be equal");

		Orientation west = new Orientation(PI, 0);
		Vector westVector = origin.getVectorInDirection(1.0, west);
		Assert.assertEquals(westVector, new Vector(-1.0, 0.0, 0.0), "Vectors should be equal");

		Orientation east = new Orientation(0, 0);
		Vector eastVector = origin.getVectorInDirection(1.0, east);
		Assert.assertEquals(eastVector, new Vector(1.0, 0.0, 0.0), "Vectors should be equal");

		Orientation up = new Orientation(0, PI * 0.5);
		Vector upVector = origin.getVectorInDirection(1.0, up);
		Assert.assertEquals(upVector, new Vector(0.0, 1.0, 0.0), "Vectors should be equal");

		Orientation upNorth = new Orientation(PI * 0.5, PI * 0.25);
		Vector upNorthVector = origin.getVectorInDirection(1.0, upNorth);
		Assert.assertEquals(upNorthVector, new Vector(0.0, ROOT_2 / 2, ROOT_2 / 2), "Vectors should be equal");

		Orientation upEast = new Orientation(0, PI * 0.25);
		Vector upEastVector = origin.getVectorInDirection(1.0, upEast);
		Assert.assertEquals(upEastVector, new Vector(ROOT_2 / 2, ROOT_2 / 2, 0), "Vectors should be equal");

		Orientation northEast = new Orientation(PI * 0.25, 0);
		Vector northEastVector = origin.getVectorInDirection(1.0, northEast);
		Assert.assertEquals(northEastVector, new Vector(ROOT_2 / 2, 0, ROOT_2 / 2), "Vectors should be equal");

		Orientation upNorthEast = new Orientation(acos(ROOT_2 / 3), asin(ROOT_3 / 3));
		Vector upNEVector = origin.getVectorInDirection(17.320508075688775, upNorthEast);
		Assert.assertEquals(upNEVector, new Vector(10, 10, 10), "Vectors should be equal");
	}

	/**
	 * Test the equals and hashCode methods.
	 */
	@Test
	public void testEqualsAndHashCode() {
		double randX = random();
		double randY = random();
		double randZ = random();

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
		double randX = random();
		double randY = random();
		double randZ = random();

		Vector vector = new Vector(randX, randY, randZ);
		String vectorString = "Vector{" +
			  "xValue=" + vector.getXValue() +
			  ", yValue=" + vector.getYValue() +
			  ", zValue=" + vector.getZValue() +
			  '}';

		Assert.assertEquals(vector.toString(), vectorString, "Vector strings should be equal.");
	}
}
