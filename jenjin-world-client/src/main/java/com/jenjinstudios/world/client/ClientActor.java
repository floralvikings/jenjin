package com.jenjinstudios.world.client;

import com.jenjinstudios.world.WorldObject;

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
	public static double MOVE_SPEED = 10.0d;
	private long lastStepTime;

	public ClientActor(int id, String name) {
		super(name);
		setId(id);
	}

	@Override
	public void update() {
		if (getLastStepTime() == 0)
		{
			setLastStepTime(getWorld().getLastUpdateCompleted());
		}
		step();
		setLastStepTime(System.nanoTime());
	}

	private void step() {
		double stepLength = calcStepLength();
		stepForward(stepLength);
	}

	private void stepForward(double stepLength) {
		if (!getAngle().isIdle())
		{
			setVector2D(getVector2D().getVectorInDirection(stepLength, getAngle().getStepAngle()));
		}
	}

	double calcStepLength() {
		return ((System.nanoTime() - (double) getLastStepTime()) / 1000000000) * MOVE_SPEED;
	}

	long getLastStepTime() { return lastStepTime; }

	public void setLastStepTime(long lastStepTime) { this.lastStepTime = lastStepTime; }

}
