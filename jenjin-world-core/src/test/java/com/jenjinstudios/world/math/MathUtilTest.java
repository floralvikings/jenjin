package com.jenjinstudios.world.math;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Caleb Brinkman
 */
public class MathUtilTest
{
	@Test(expectedExceptions = RuntimeException.class)
	public void testConstructor() {
		new MathUtil();
	}

	@Test
	public void testRound() {
		double exact = 123.45678;
		double round = MathUtil.round(exact, 2);
		Assert.assertEquals(round, 123.46, "Rounding " + exact + " to 2 decimals.");
	}

	@Test
	public void testCalcStepAngle() {
		double absAngle = Math.PI * .5;
		double relAngle = Math.PI;
		double stepAngle = MathUtil.calcStepAngle(absAngle, relAngle);
		double expectedAngle = Math.PI * 1.5;
		Assert.assertEquals(stepAngle, expectedAngle);
	}

	@Test
	public void testCastCircle() {
		List<Vector2D> expectedCircle = new LinkedList<>();
		expectedCircle.add(new Vector2D(15, 10));
		expectedCircle.add(new Vector2D(10, 15));
		expectedCircle.add(new Vector2D(5, 10));
		expectedCircle.add(new Vector2D(10, 15));
		expectedCircle.add(new Vector2D(5, 10));
		expectedCircle.add(new Vector2D(10, 5));
		expectedCircle.add(new Vector2D(15, 10));
		expectedCircle.add(new Vector2D(10, 5));
		expectedCircle.add(new Vector2D(15, 11));
		expectedCircle.add(new Vector2D(11, 15));
		expectedCircle.add(new Vector2D(5, 11));
		expectedCircle.add(new Vector2D(9, 15));
		expectedCircle.add(new Vector2D(5, 9));
		expectedCircle.add(new Vector2D(9, 5));
		expectedCircle.add(new Vector2D(15, 9));
		expectedCircle.add(new Vector2D(11, 5));
		expectedCircle.add(new Vector2D(15, 12));
		expectedCircle.add(new Vector2D(12, 15));
		expectedCircle.add(new Vector2D(5, 12));
		expectedCircle.add(new Vector2D(8, 15));
		expectedCircle.add(new Vector2D(5, 8));
		expectedCircle.add(new Vector2D(8, 5));
		expectedCircle.add(new Vector2D(15, 8));
		expectedCircle.add(new Vector2D(12, 5));
		expectedCircle.add(new Vector2D(14, 13));
		expectedCircle.add(new Vector2D(13, 14));
		expectedCircle.add(new Vector2D(6, 13));
		expectedCircle.add(new Vector2D(7, 14));
		expectedCircle.add(new Vector2D(6, 7));
		expectedCircle.add(new Vector2D(7, 6));
		expectedCircle.add(new Vector2D(14, 7));
		expectedCircle.add(new Vector2D(13, 6));
		List<Vector2D> circle = MathUtil.castCircle(10, 10, 5);
		Assert.assertEquals(circle, expectedCircle);
	}
}
