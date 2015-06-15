package com.jenjinstudios.world.actor;

import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.math.Orientation;

/**
 * Contains data related to Actor movement, including speed and relative orientation of movement.
 *
 * @author Caleb Brinkman
 */
public class Movement
{
	private static final double DEFAULT_MOVE_SPEED = Cell.CELL_SIZE;
	private double speed = DEFAULT_MOVE_SPEED;
	private Orientation orientation = Orientation.NOWHERE;

	/**
	 * Get the speed at which the Actor with this Movement should move, in units per second.
	 *
	 * @return The speed at which the Actor with this Movement should mvoe.
	 */
	public double getSpeed() { return speed; }

	/**
	 * Set the speed at which the Actor with this Movement should move, in units per second.
	 *
	 * @param speed The new speed at which the Actor with this movement will move.
	 */
	public void setSpeed(double speed) { this.speed = speed; }

	/**
	 * Get the relative orientation in which the actor with this movement will move. i.e. forward, backward, etc...
	 *
	 * @return The relative orientation in which the actor with this movement will move.
	 */
	public Orientation getOrientation() { return orientation; }

	/**
	 * Set the relative orientation in which the actor with this movement will move. i.e. forward, backward, etc...
	 *
	 * @param orientation The orientation in which the actor with this movement will move.
	 */
	public void setOrientation(Orientation orientation) { this.orientation = orientation; }
}
