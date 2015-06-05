package com.jenjinstudios.world.object;

import com.jenjinstudios.world.Cell;

/**
 * Represents a WorldObject which can move and see other objects.
 *
 * @author Caleb Brinkman
 */
public class Actor extends WorldObject
{
	private static final double DEFAULT_MOVE_SPEED = Cell.CELL_SIZE;
	private final Vision vision;
	private double moveSpeed = DEFAULT_MOVE_SPEED;

	/**
	 * Construct a new Actor with the given name.
	 *
	 * @param name The name of the actor.
	 */
	public Actor(String name) {
		super(name);
		vision = new Vision();
		addObserver(vision.getNewlyVisibleObserver());
		addObserver(vision.getNewlyInvisibleObserver());
	}

	/**
	 * Get the Vision of this actor.
	 *
	 * @return The Vision of this actor.
	 */
	public Vision getVision() { return vision; }

	/**
	 * Get the speed at which this actor moves.
	 *
	 * @return The speed (in units per second) at which this actor moves.
	 */
	public double getMoveSpeed() { return moveSpeed; }

	/**
	 * Set the speed (in units per second) at which this actor moves.
	 *
	 * @param moveSpeed The speed (in units per second) at which this actor moves.
	 */
	public void setMoveSpeed(double moveSpeed) { this.moveSpeed = moveSpeed; }
}
