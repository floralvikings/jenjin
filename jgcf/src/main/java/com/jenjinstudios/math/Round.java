package com.jenjinstudios.math;

/**
 * This class is used to simplify rounding.
 * @author Caleb Brinkman
 */
public class Round
{
	/**
	 * Round the given number to the given decimal place.
	 * @param value The number to round.
	 * @param decimalPlaces The number of decimals to be rounded to.
	 * @return The rounded value.
	 */
	public static double round(double value, int decimalPlaces) {
		if (decimalPlaces < 0) throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, decimalPlaces);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}
}
