package com.jenjinstudios.world.math;

import java.util.LinkedList;
import java.util.List;

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

	/**
	 * Use Bresenham's circle algorithm to find a circle of locations with the given center coordinates and radius.
	 * @param x0 The center x coordinate.
	 * @param y0 The center y coordinate.
	 * @param radius The radius of the circle.
	 * @return A list of locations containing the perimeter of the circle.
	 */
	public static List<Vector2D> castCircle(int x0, int y0, int radius) {
		int x = radius, y = 0;
		int radiusError = 1 - x;
		LinkedList<Vector2D> circle = new LinkedList<>();
		while (x >= y)
		{
			circle.add(new Vector2D(x + x0, y + y0));
			circle.add(new Vector2D(y + x0, x + y0));
			circle.add(new Vector2D(-x + x0, y + y0));
			circle.add(new Vector2D(-y + x0, x + y0));
			circle.add(new Vector2D(-x + x0, -y + y0));
			circle.add(new Vector2D(-y + x0, -x + y0));
			circle.add(new Vector2D(x + x0, -y + y0));
			circle.add(new Vector2D(y + x0, -x + y0));
			y++;
			if (radiusError < 0)
			{
				radiusError += 2 * y + 1;
			} else
			{
				x--;
				radiusError += 2 * (y - x + 1);
			}
		}
		return circle;
	}
}
