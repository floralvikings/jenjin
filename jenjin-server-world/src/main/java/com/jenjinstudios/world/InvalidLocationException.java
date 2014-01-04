package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Vector2D;

/**
 * An {@code InvalidLocationException} is used to indicate that the supplied coordinates specify an invalid location.
 * @author Caleb Brinkman
 */
public class InvalidLocationException extends Exception
{
	/**
	 * Construct a new InvalidLocationException for the given coordinates.
	 * @param coordinates The coordinates of the invalid location.
	 */
	public InvalidLocationException(Vector2D coordinates) {
		super("Location does not exist at: " + coordinates);
	}
}
