package com.jenjinstudios.world.state;

import com.jenjinstudios.world.math.Vector2D;

/**
 * The {@code MovementState} class is used to establish what an {@code Actor}'s movement state is.
 * @author Caleb Brinkman
 */
public class MoveState
{
	/** The constant used for an "idle" move state. */
	public static final double IDLE = Double.NEGATIVE_INFINITY;
	/** The forward state. */
	public static final double FRONT = Math.PI * 0;
	/** The forward-right state. */
	public static final double FRONT_RIGHT = Math.PI * -0.25;
	/** The right state. */
	public static final double RIGHT = Math.PI * -0.5;
	/** The back-right state. */
	public static final double BACK_RIGHT = Math.PI * -0.75;
	/** The backward state. */
	public static final double BACK = Math.PI;
	/** The back-left state. */
	public static final double BACK_LEFT = Math.PI * 0.75;
	/** The left state. */
	public static final double LEFT = Math.PI * 0.5;
	/** The front-left state. */
	public static final double FRONT_LEFT = Math.PI * 0.25;
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
		if(relativeAngle == FRONT) {
			cheese = "FRONT";
		}else if(relativeAngle == FRONT_RIGHT) {
			cheese = "FRONT_RIGHT";
		}else if (relativeAngle == FRONT_LEFT) {
			cheese = "FRONT_LEFT";
		}else if (relativeAngle == BACK) {
			cheese = "BACK";
		}else if(relativeAngle == BACK_LEFT) {
			cheese = "BACK_LEFT";
		}else if(relativeAngle == BACK_RIGHT) {
			cheese = "BACK_RIGHT";
		}else if(relativeAngle == LEFT) {
			cheese = "LEFT";
		}else if(relativeAngle == RIGHT) {
			cheese = "RIGHT";
		}
		return cheese;
	}
}
