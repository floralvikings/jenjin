package com.jenjinstudios.world.state;

/**
 * The {@code MovementState} class is used to establish what an {@code Actor}'s movement state is.  The
 * {@code stepsUntilChange} field indicates the number of steps (updates) the Actor should take before it changes to
 * the {@code MovementDirection} specified.
 *
 * @author Caleb Brinkman
 */
public class MoveState
{
	/** The number of steps in the last move. */
	public final int stepsUntilChange;
	/** The direction of movement. */
	public final MoveDirection direction;
	/** The angle of movement. */
	public final double moveAngle;
	/** Caches the MoveDirection values. */
	private static final MoveDirection[] values = MoveDirection.values();

	/**
	 * Construct a new MoveState.
	 *
	 * @param direction        The direction of movement.
	 * @param stepsUntilChange The steps in the last movement.
	 * @param moveAngle        The angle of movement.
	 */
	public MoveState(int direction, int stepsUntilChange, double moveAngle)
	{
		this(values[direction], stepsUntilChange, moveAngle);
	}

	/**
	 * Construct a new MoveState.
	 *
	 * @param direction        The direction of movement.
	 * @param stepsUntilChange The steps in the last movement.
	 * @param moveAngle        The angle of movement.
	 */
	public MoveState(MoveDirection direction, int stepsUntilChange, double moveAngle)
	{
		this.direction = direction;
		this.stepsUntilChange = stepsUntilChange;
		this.moveAngle = moveAngle;
	}

}
