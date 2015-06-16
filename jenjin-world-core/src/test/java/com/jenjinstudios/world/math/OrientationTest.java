package com.jenjinstudios.world.math;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the Orientation class.
 *
 * @author Caleb Brinkman
 */
public class OrientationTest
{
	/**
	 * Test the getDirectionalVector method.
	 */
	@Test
	public void testGetDirectionalVector() {
		Orientation upNorthEast = new Orientation(Math.PI * 0.25, Math.PI * 0.25);
		Vector directionalVector = upNorthEast.getDirectionalVector();
		Assert.assertEquals(new Vector(0.5, 1.4142135623730951 / 2, 0.5), directionalVector, "Vectors aren't equal");
	}
}
