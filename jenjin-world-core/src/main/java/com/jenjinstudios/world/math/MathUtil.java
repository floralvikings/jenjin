package com.jenjinstudios.world.math;

import static com.jenjinstudios.world.state.MoveState.IDLE;

/**
 * This class is used to simplify rounding.
 * @author Caleb Brinkman
 */
public class MathUtil
{
	/**
	 * MathUtil the given number to the given decimal place.
	 * @param value The number to round.
	 * @param decimalPlaces The number of decimals to be rounded to.
	 * @return The rounded value.
	 */
	public static double round(double value, int decimalPlaces) {
		if (decimalPlaces < 0) throw new IllegalArgumentException();

		long factor = (long) java.lang.Math.pow(10, decimalPlaces);
		value = value * factor;
		long tmp = java.lang.Math.round(value);
		return (double) tmp / factor;
	}

	/**
	 * Given the specified relative and absolute angles, determine the angle in which an actor should move.
	 * @param abs The absolute angle.
	 * @param rel The relative angle.
	 * @return The value of the angles combined.
	 */
	public static double calcStepAngle(double abs, double rel) {
		double sAngle = rel != IDLE ? abs + rel : IDLE;
		return (sAngle < 0) ? (sAngle + (Math.PI * 2)) : (sAngle % (Math.PI * 2));
	}
}
