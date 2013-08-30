package com.jenjinstudios.world.state;

import static java.lang.Math.PI;

/**
 * Stores movement direction state.
 *
 * @author Caleb Brinkman
 */
public enum MoveDirection
{
	/** The idle state. */
	IDLE,
	/** The forward state. */
	FRONT(PI * .5),
	/** The forward-right state. */
	FRONT_RIGHT(PI * .25),
	/** The right state. */
	RIGHT(PI * 0),
	/** The back-right state. */
	BACK_RIGHT(PI * 1.75),
	/** The backward state. */
	BACK(PI * 1.5),
	/** The back-left state. */
	BACK_LEFT(PI * 1.25),
	/** The left state. */
	LEFT(PI * 1),
	/** The front-left state. */
	FRONT_LEFT(PI * .75);

	/** Create an idle move state. */
	private MoveDirection()
	{

	}

	/**
	 * Create a move state as specified.
	 *
	 * @param d The specified move state.
	 */
	@SuppressWarnings("unused")
	private MoveDirection(double d)
	{

	}
}
