package com.jenjinstudios.world.state;

/**
 * The {@code MovementState} class is used to establish what an {@code Actor}'s movement state is.  The {@code
 * stepsUntilChange} field indicates the number of steps (updates) the Actor should take before it changes to the {@code
 * MovementDirection} specified.
 * @author Caleb Brinkman
 */
public class MoveState
{
	/** The constant for 2*PI. */
	public static double TWO_PI = (2 * Math.PI);
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
	/** The number of steps in the last move. */
	public final int stepsUntilChange;
	/** The relativeAngle of movement. */
	public final double relativeAngle;
	/** The angle of movement. */
	public final double absoluteAngle;
	/** The actual angle of movement given the relativeAngle and move angle. */
	public final double stepAngle;

	/**
	 * Construct a new MoveState.
	 * @param relativeAngle The relativeAngle of movement.
	 * @param stepsUntilChange The steps in the last movement.
	 * @param absoluteAngle The angle of movement.
	 */
	public MoveState(double relativeAngle, int stepsUntilChange, double absoluteAngle) {
		this.relativeAngle = relativeAngle;
		this.stepsUntilChange = stepsUntilChange;
		this.absoluteAngle = absoluteAngle;

		double sAngle = relativeAngle != IDLE ? absoluteAngle + relativeAngle : IDLE;
		stepAngle = (sAngle < 0) ? (sAngle + TWO_PI) : (sAngle % TWO_PI);
	}

	@Override
	public String toString() {
		return "RelAng: " + relativeAngleString() + ", AbsAng: " + absoluteAngle + " Steps: " + stepsUntilChange;
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
