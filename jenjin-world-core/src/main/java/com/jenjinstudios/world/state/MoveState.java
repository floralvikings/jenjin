package com.jenjinstudios.world.state;

import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector;

/**
 * The {@code MovementState} class is used to establish what an {@code Actor}'s movement state is.
 * @author Caleb Brinkman
 */
public class MoveState
{
	public final Angle angle;
	/** The position at which this change took place. */
	public final Vector position;
	/** The time at which the change took place. */
	public final long timeOfChange;

	/**
	 * Construct a new MoveState.
	 * @param position The position at which the state change took place.
	 * @param timeOfChange The time in nanoseconds at which the state change took place.
	 */
	public MoveState(Angle angle, Vector position, long timeOfChange) {
		this.angle = angle;
		this.position = position;
		this.timeOfChange = timeOfChange;
	}
}
