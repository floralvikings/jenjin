package com.jenjinstudios.world.math;

import java.math.BigDecimal;

/**
 * This class is used to simplify rounding.
 * @author Caleb Brinkman
 */
public class MathUtil
{
	private MathUtil() { }

	/**
	 * MathUtil the given number to the given decimal place.
	 * @param value The number to round.
	 * @param decimalPlaces The number of decimals to be rounded to.
	 * @return The rounded value.
	 */
	public static double round(double value, int decimalPlaces) {
		BigDecimal bd = new BigDecimal(value);
		BigDecimal rounded = bd.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
		return rounded.doubleValue();
	}

}
