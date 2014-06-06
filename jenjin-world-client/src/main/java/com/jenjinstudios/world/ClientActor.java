package com.jenjinstudios.world;

import com.jenjinstudios.world.math.MathUtil;

import static com.jenjinstudios.world.state.MoveState.IDLE;

/**
 * The {@code ClientActor} class is used to represent a server-side {@code Actor} object on the client side.  It is an
 * object capable of movement.
 * <p/>
 * Actors start with a {@code MoveState} with {@code MoveState.IDLE}.  Each update, the Actor checks to see if there are
 * any MoveStates in the queue.  If there are, it checks the first state in line for the number of steps needed before
 * the state changes.  Once the number of steps has been reached, the state switches to that of the first position in
 * the queue, and the Actor's step counter is reset.  If an Actor "oversteps," which is determined if the Actor has
 * taken more than the required number of steps to change state, the Actor is moved back by the "overstepped" number of
 * states, the Actor's state is updated, and the Actor then takes the number of extra steps in the correct
 * relativeAngle.
 * <p/>
 * An Actor's state is considered "changed" when the Actor is facing a new relativeAngle or moving in a new
 * relativeAngle.
 * @author Caleb Brinkman
 */
public class ClientActor extends WorldObject
{
	/** The length of each step. */
	public static double STEP_LENGTH = 5;
	/** The move speed of an Actor. */
	public static double MOVE_SPEED = 10.0d; // TODO Set this from server.
	/** The time at which this actor completed it's last step. */
	private long lastStepTime;
	private double relativeAngle;

	/**
	 * Construct an Actor with the given name.
	 * @param id The Actor's ID.
	 * @param name The name.
	 */
	public ClientActor(int id, String name) {
		super(name);
		setId(id);
	}

	@Override
	public void update() {
		if (getLastStepTime() == 0) {
			setLastStepTime(getWorld().getLastUpdateCompleted());
		}
		step();
		setLastStepTime(System.nanoTime());
	}

	/** Take a step, changing state and correcting steps if necessary. */
	private void step() {
		double stepLength = calcStepLength();
		stepForward(stepLength);
	}

	/** Take a step according to the current move state. */
	public void stepForward(double stepLength) {
		if (relativeAngle == IDLE) return;
		setVector2D(getVector2D().getVectorInDirection(stepLength, MathUtil.calcStepAngle(getAbsoluteAngle(), getRelativeAngle())));
	}

	/**
	 * Calculate the step length at the current time.
	 * @return The current step length.
	 */
	protected double calcStepLength() {
		return ((System.nanoTime() - (double) getLastStepTime()) / 1000000000)
				* MOVE_SPEED;
	}

	/**
	 * Get the time at which this actor completed its last step.
	 * @return The time at which this actor completed its last step.
	 */
	public long getLastStepTime() { return lastStepTime; }

	/**
	 * Set the time at which this actor completed its last step.  This should be called any time an actor's position is set
	 * manually so that the following step doesn't "overshoot".
	 * @param lastStepTime The new time at which the actor completed its last step.
	 */
	public void setLastStepTime(long lastStepTime) { this.lastStepTime = lastStepTime; }

	/**
	 * Get the relative angle of this actor.
	 * @return The relative angle of this actor.
	 */
	public double getRelativeAngle() { return relativeAngle; }

	/**
	 * Set the relative angle of this actor.
	 * @param relativeAngle The new relative angle.
	 */
	public void setRelativeAngle(double relativeAngle) { this.relativeAngle = relativeAngle; }

}
