package com.jenjinstudios.world.math;

import org.testng.Assert;
import org.testng.annotations.*;

/**
 * @author Caleb Brinkman
 */
public class MathUtilTest
{
	@Test
	public void testRound() {
		double exact = 123.45678;
		double round = MathUtil.round(exact, 2);
		Assert.assertEquals(round, 123.46, "Rounding " + exact + " to 2 decimals.");
	}
}
