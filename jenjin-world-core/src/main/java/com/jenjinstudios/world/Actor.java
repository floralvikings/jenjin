package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.state.MoveState;

import java.util.LinkedList;


/**
 * Implement a WorldObject which is capable of movement. </p> Actors start with a {@code MoveState} with {@code
 * MoveState.IDLE}.  Each update, the Actor checks to see if there are any MoveStates in the queue.  If there are, it
 * checks the first state in line for the number of steps needed before the state changes.  Once the number of steps has
 * been reached, the state switches to that of the first position in the queue, and the Actor's step counter is reset.
 * If an Actor "oversteps," which is determined if the Actor has taken more than the required number of steps to change
 * state, the Actor is moved back by the "overstepped" number of states, the Actor's state is updated, and the Actor
 * then takes the number of extra steps in the correct relativeAngle. </p> An Actor's state is considered "changed" when
 * the Actor is facing a new relativeAngle or moving in a new relativeAngle. An actor's state is considered "forced"
 * when the Actor attempts to make an illegal move, and the world forces the actor to halt.  The actor's forced state
 * will always be facing the angle of the most recently added move state (even if the state causes an illegal move) and
 * IDLE. The "steps until change" value is determined from the number of steps that were taken until the state was
 * forced.
 * @author Caleb Brinkman
 */
public class Actor extends SightedObject
{
	/** The speed of an Actor, in units per second. */
	public static final double MOVE_SPEED = 10.0d;
	/** The next move. */
	private final LinkedList<MoveState> stateChanges;
	/** Flags whether this actor has changed to a new state during this update. */
	private boolean newState;
	/** Flags whether the state of this actor was forced during this update. */
	private MoveState forcedState;
	/** The time at which this actor finished it's last step. */
	private long lastStepTime;
	private Angle newAngle;

	/**
	 * Construct an Actor with the given name.
	 * @param name The name.
	 */
	public Actor(String name) {
		super(name);
		stateChanges = new LinkedList<>();
	}

	@Override
	public void setUp() {
		super.setUp();
		synchronized (stateChanges)
		{
			stateChanges.clear();
		}
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

	@Override
	public void reset() {
		super.reset();
		if (newState)
		{
			newState = false;
			resetAngles();
			synchronized (stateChanges)
			{
				stateChanges.add(new MoveState(getAngle(), getVector2D(),
					  getLastStepTime()));
			}
		}
	}

	/**
	 * Get the list of state changes this actor has made since its last update.
	 * @return The list of state changes.
	 */
	public LinkedList<MoveState> getStateChanges() {
		synchronized (stateChanges) { return new LinkedList<>(stateChanges); }
	}

	/**
	 * Get whether this actor was forced into a state during the most recent update.
	 * @return Whether this actor was forced into a state during the most recent update.
	 */
	public MoveState getForcedState() {
		MoveState temp = forcedState;
		forcedState = null;
		return temp;
	}

	/**
	 * Set the forced state of this actor to the given state.
	 * @param forcedState The state to which this actor will be forced.
	 */
	public void setForcedState(MoveState forcedState) { this.forcedState = forcedState; }

	@Override
	public void setAngle(Angle angle) {
		if (!getAngle().equals(angle))
		{
			newState = true;
			this.newAngle = angle;
		}
	}

	/**
	 * Set the absolute and relative angles to their new-state counterparts.
	 */
	private void resetAngles() { super.setAngle(newAngle); }

	/** Take a step, changing state and correcting steps if necessary. */
	void step() {
		double stepLength = calcStepLength();
		if (!stepForward(stepLength))
		{
			setForcedState(new MoveState(getAngle().asIdle(), getVector2D(), lastStepTime));
			setAngle(getAngle().asIdle());
		}
	}

	/**
	 * Calculate the step length at the current time.
	 * @return The current step length.
	 */
	double calcStepLength() {
		return ((System.nanoTime() - (double) getLastStepTime()) / 1000000000) * Actor.MOVE_SPEED;
	}

	/**
	 * Take a step according to the current move state.
	 * @param stepLength The amount to step forward.
	 * @return Whether the step forward was successful.
	 */
	boolean stepForward(double stepLength) {
		if (getAngle().isIdle()) { return true; }
		Vector2D newVector = getVector2D().getVectorInDirection(stepLength, getAngle().getStepAngle());
		Location newLocation = getWorld().getLocationForCoordinates(getZoneID(), newVector);
		if (newLocation == null) { return false; }
		boolean walkable = !"false".equals(newLocation.getProperties().getProperty("walkable"));
		if (walkable) { setVector2D(newVector); }
		return walkable;
	}

	/**
	 * Get the time at which this actor finished it's last step.
	 * @return The time at which this actor finished it's last step.
	 */
	long getLastStepTime() { return lastStepTime; }

	/**
	 * Set the time at which this actor finished it's last step.  This method should only be used when the actor's step
	 * has to be modified outside of the normal step cycle.
	 * @param lastStepTime The new time to use for this actors last completed step.
	 */
	public void setLastStepTime(long lastStepTime) { this.lastStepTime = lastStepTime; }
}
