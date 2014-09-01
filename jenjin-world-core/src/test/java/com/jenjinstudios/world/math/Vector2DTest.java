package com.jenjinstudios.world.math;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the coordinates class.
 * @author Caleb Brinkman
 */
public class Vector2DTest
{
	/** Test the coordinates methods. */
	@Test
	public void testVector2D() {
		Vector2D vector2D01 = new Vector2D(5, 7);
		Assert.assertEquals(7, vector2D01.getYCoordinate(), 0);
		Assert.assertEquals(5, vector2D01.getXCoordinate(), 0);

		Vector2D vector2D02 = new Vector2D(vector2D01);
		Assert.assertEquals(7, vector2D02.getYCoordinate(), 0);
		Assert.assertEquals(5, vector2D02.getXCoordinate(), 0);

		Assert.assertEquals(5, vector2D02.getXCoordinate(), 0);
		Assert.assertEquals(7, vector2D02.getYCoordinate(), 0);
	}

	/** Test the relativeAngle math methods. */
	@Test
	public void getGetVectorInDirection() {
		Vector2D original = new Vector2D(5, 5);
		testRight(original);
		testLeft(original);
		testBack(original);
		testForward(original);
		testForwardRight(original);
		testBackRight(original);
		testBackLeft(original);
	}

	private void testBackLeft(Vector2D original) {
		double expectedX;
		double expectedY;
		Vector2D stepped;
		double backLeft = Math.PI * 1.25;
		expectedX = 5 - Math.sqrt(2) / 2;
		expectedY = 5 - Math.sqrt(2) / 2;
		stepped = original.getVectorInDirection(1, backLeft);
		Assert.assertEquals(expectedX, stepped.getXCoordinate(), 0.1);
		Assert.assertEquals(expectedY, stepped.getYCoordinate(), 0.1);
	}

	private void testBackRight(Vector2D original) {
		double expectedX;
		double expectedY;
		Vector2D stepped;
		double backRight = Math.PI * -.25;
		expectedX = 5 + Math.sqrt(2) / 2;
		expectedY = 5 - Math.sqrt(2) / 2;
		stepped = original.getVectorInDirection(1, backRight);
		Assert.assertEquals(expectedX, stepped.getXCoordinate(), 0.1);
		Assert.assertEquals(expectedY, stepped.getYCoordinate(), 0.1);
	}

	private void testForwardRight(Vector2D original) {
		Vector2D stepped;
		double forwardRight = Math.PI * .25;
		double expectedX = 5 + Math.sqrt(2) / 2;
		double expectedY = 5 + Math.sqrt(2) / 2;
		stepped = original.getVectorInDirection(1, forwardRight);
		Assert.assertEquals(expectedX, stepped.getXCoordinate(), 0.001);
		Assert.assertEquals(expectedY, stepped.getYCoordinate(), 0.001);
	}

	private void testForward(Vector2D original) {
		Vector2D stepped;
		double forward = Math.PI * .5;
		stepped = original.getVectorInDirection(1, forward);
		Assert.assertEquals(5, stepped.getXCoordinate(), 0.001);
		Assert.assertEquals(6, stepped.getYCoordinate(), 0.001);
	}

	private void testBack(Vector2D original) {
		Vector2D stepped;
		double back = Math.PI * 1.5;
		stepped = original.getVectorInDirection(1, back);
		Assert.assertEquals(5, stepped.getXCoordinate(), 0.001);
		Assert.assertEquals(4, stepped.getYCoordinate(), 0.001);
	}

	private void testLeft(Vector2D original) {
		Vector2D stepped;
		double left = Math.PI;
		stepped = original.getVectorInDirection(1, left);
		Assert.assertEquals(4, stepped.getXCoordinate(), 0.001);
		Assert.assertEquals(5, stepped.getYCoordinate(), 0.001);
	}

	private void testRight(Vector2D original) {
		double right = 0;
		Vector2D stepped = original.getVectorInDirection(1, right);
		Assert.assertEquals(6, stepped.getXCoordinate(), 0.001);
		Assert.assertEquals(5, stepped.getYCoordinate(), 0.001);
	}

	@Test
	public void testEquals() {
		Vector2D vector1 = new Vector2D(10, 10);
		Vector2D vector2 = new Vector2D(10, 10);
		Assert.assertTrue(vector1.equals(vector2));
	}

	@Test
	public void testEqualsNonVector() {
		Vector2D vector1 = new Vector2D(10, 10);
		String foo = "bar";
		//noinspection EqualsBetweenInconvertibleTypes
		Assert.assertFalse(vector1.equals(foo));
	}

	@Test
	public void testGetAngleToVector() {
		Vector2D vector1 = new Vector2D(0, 0);
		Vector2D vector2 = new Vector2D(5, 5);
		double angle = vector1.getAngleToVector(vector2);
		double expectedAngle = Math.PI * .25;
		Assert.assertEquals(angle, expectedAngle);
	}

	@Test
	public void testGetAngleToEqualVector() {
		Vector2D vector1 = new Vector2D(10, 10);
		Vector2D vector2 = new Vector2D(10, 10);
		double angle = vector1.getAngleToVector(vector2);
		double expectedAngle = Double.NEGATIVE_INFINITY;
		Assert.assertEquals(angle, expectedAngle);
	}

	@Test
	public void testGetDistanceToVector() {
		Vector2D vector1 = new Vector2D(0, 0);
		Vector2D vector2 = new Vector2D(0, 5);
		double distance = vector1.getDistanceToVector(vector2);
		double expectedDistance = 5;
		Assert.assertEquals(distance, expectedDistance);
	}

	@Test
	public void testHashCode() {
		Vector2D vector1 = new Vector2D(10, 10);
		Vector2D vector2 = new Vector2D(vector1);

		int hash1 = vector1.hashCode();
		int hash2 = vector2.hashCode();

		Assert.assertEquals(hash2, hash1);
	}

}
