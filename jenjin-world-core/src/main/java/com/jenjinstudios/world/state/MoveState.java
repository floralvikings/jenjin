package com.jenjinstudios.world.state;

import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;

/**
 * The {@code MovementState} class is used to establish what an {@code Actor}'s movement state is.
 * @author Caleb Brinkman
 */
public class MoveState
{
	/** The relativeAngle of movement. */
	public final double relativeAngle;
	/** The angle of movement. */
	public final double absoluteAngle;
	/** The position at which this change took place. */
	public final Vector2D position;
	/** The time at which the change took place. */
	public final long timeOfChange;

	/**
	 * Construct a new MoveState.
	 * @param relativeAngle The relativeAngle of movement.
	 * @param absoluteAngle The angle of movement.
	 * @param position The position at which the state change took place.
	 * @param timeOfChange The time in nanoseconds at which the state change took place.
	 */
	public MoveState(double relativeAngle, double absoluteAngle, Vector2D position, long timeOfChange) {
		this.relativeAngle = relativeAngle;
		this.absoluteAngle = absoluteAngle;
		this.position = position;
		this.timeOfChange = timeOfChange;

	}

	@Override
	public String toString() {
		return "RelAng: " + relativeAngleString() + ", AbsAng: " + absoluteAngle;
	}

	/**
	 * Get a string representation of the relative angle.  Used to report cardinal directions instead of numbers.
	 * @return The string representation of the relative angle.
	 */
	private String relativeAngleString()
	{
		String cheese = String.valueOf(relativeAngle);
		if (relativeAngle == Angle.FRONT)
		{
			cheese = "FRONT";
		} else if (relativeAngle == Angle.FRONT_RIGHT)
		{
			cheese = "FRONT_RIGHT";
		} else if (relativeAngle == Angle.FRONT_LEFT)
		{
			cheese = "FRONT_LEFT";
		} else if (relativeAngle == Angle.BACK)
		{
			cheese = "BACK";
		} else if (relativeAngle == Angle.BACK_LEFT)
		{
			cheese = "BACK_LEFT";
		} else if (relativeAngle == Angle.BACK_RIGHT)
		{
			cheese = "BACK_RIGHT";
		} else if (relativeAngle == Angle.LEFT)
		{
			cheese = "LEFT";
		} else if (relativeAngle == Angle.RIGHT)
		{
			cheese = "RIGHT";
		}
		return cheese;
	}
}
