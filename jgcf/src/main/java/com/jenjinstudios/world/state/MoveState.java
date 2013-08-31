package com.jenjinstudios.world.state;

/**
 * The {@code MovementState} class is used to establish what an {@code Actor}'s movement state is.  The
 * {@code stepsInLastMove} field indicates the number of steps (updates) the Actor should take before it changes to
 * the {@code MovementDirection} specified.
 *
 * @author Caleb Brinkman
 */
public class MoveState
{
	/** The number of steps in the last move. */
	public final int stepsInLastMove;
	/** The direction of movement. */
	public final MoveDirection direction;
	/** The angle of movement. */
	public final double moveAngle;
	/** Caches the MoveDirection values. */
	private final MoveDirection[] values = MoveDirection.values();

	/**
	 * Construct a new MoveState.
	 *
	 * @param direction       The direction of movement.
	 * @param stepsInLastMove The steps in the last movement.
	 * @param moveAngle       The angle of movement.
	 */
	public MoveState(int direction, int stepsInLastMove, double moveAngle)
	{
		this.direction = values[direction];
		this.stepsInLastMove = stepsInLastMove;
		this.moveAngle = moveAngle;
	}

}
