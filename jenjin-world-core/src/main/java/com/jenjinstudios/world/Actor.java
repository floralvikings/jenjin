package com.jenjinstudios.world;

import com.jenjinstudios.world.math.MathUtil;
import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.state.MoveState;

import java.util.LinkedList;

import static com.jenjinstudios.world.state.MoveState.IDLE;


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
	/** The length of each step. */
	public static final double STEP_LENGTH = (double) Location.SIZE / 50d;
	/** The speed of an Actor, in units per second. */
	public static final double MOVE_SPEED = 10.0d;
	/** The next move. */
	private final LinkedList<MoveState> stateChanges;
	/** Flags whether this actor has changed to a new state during this update. */
	private boolean newState;
	/** Flags whether the state of this actor was forced during this update. */
	private boolean forcedState;
	/** The Location before a step is taken. */
	private Vector2D vectorBeforeStep;
	/** The time at which this actor finished it's last step. */
	private long lastStepTime;
	private double stepAngle;
	private double relativeAngle;
	private double newRelAngle;
	private double newAbsAngle;

	/**
	 * Construct an Actor with the given name.
	 * @param name The name.
	 */
	public Actor(String name) {
		super(name);
		stateChanges = new LinkedList<>();
		relativeAngle = MoveState.IDLE;
		// TODO Replace with world update time, if possible?  Since world hasn't been set here, may not be.
		synchronized (stateChanges) {
			stateChanges.add(new MoveState(IDLE, 0, 0, getVector2D(), System.nanoTime()));
		}
	}

	@Override
	public void setUp() {
		resetFlags();
		vectorBeforeStep = getVector2D();
		synchronized (stateChanges) {
			stateChanges.clear();
		}
	}

	@Override
	public void update() {
		if (getLastStepTime() == 0) {
			setLastStepTime(getWorld().getLastUpdateCompleted());
		}
		step();
		setLastStepTime(System.nanoTime());
	}

	@Override
	public void reset() {
		// If we're in a new locations after stepping, update the visible array.
		Location oldLoc = getWorld().getLocationForCoordinates(getZoneID(), vectorBeforeStep);
		if (oldLoc != getLocation() || getVisibleLocations().isEmpty())
			resetVisibleLocations();
		// Reset the array of visible actors.
		resetVisibleObjects();
		if (newState) {
			resetAngles();
			synchronized (stateChanges) {
				stateChanges.add(new MoveState(getRelativeAngle(), 0, getAbsoluteAngle(), getVector2D(), getLastStepTime()));
			}
			newState = false;
		}
	}

	/** Take a step, changing state and correcting steps if necessary. */
	public void step() {
		double stepLength = calcStepLength();
		if (!stepForward(stepLength)) {
			forcedState = true;
			setRelativeAngle(MoveState.IDLE);
		}
	}

	/**
	 * Calculate the step length at the current time.
	 * @return The current step length.
	 */
	protected double calcStepLength() {
		return ((System.nanoTime() - (double) getLastStepTime()) / 1000000000) * Actor.MOVE_SPEED;
	}

	/**
	 * Take a step according to the current move state.
	 * @param stepLength The amount to step forward.
	 * @return Whether the step forward was successful.
	 */
	public boolean stepForward(double stepLength) {
		if (getRelativeAngle() == IDLE) { return true; }
		Vector2D newVector = getVector2D().getVectorInDirection(stepLength, stepAngle);
		Location newLocation = getWorld().getLocationForCoordinates(getZoneID(), newVector);
		if (newLocation == null) { return false; }
		boolean walkable = !"false".equals(newLocation.getLocationProperties().getProperty("walkable"));
		if (walkable) { setVector2D(newVector); }
		return walkable;
	}

	/** Reset the flags used by this actor. */
	protected void resetFlags() {
		forcedState = false;
	}

	/**
	 * Get whether this actor has initialized a new state during this update.
	 * @return Whether the actor has changed moved states since the beginning of this update.
	 */
	public boolean isNewState() { return newState; }

	public LinkedList<MoveState> getStateChanges() {
		synchronized (stateChanges) { return new LinkedList<>(stateChanges); }
	}

	/**
	 * Get whether this actor was forced into a state during the most recent update.
	 * @return Whether this actor was forced into a state during the most recent update.
	 */
	public boolean isForcedState() { return forcedState; }

	/**
	 * Get the time at which this actor finished it's last step.
	 * @return The time at which this actor finished it's last step.
	 */
	public long getLastStepTime() { return lastStepTime; }

	/**
	 * Set the time at which this actor finished it's last step.  This method should only be used when the actor's step has
	 * to be modified outside of the normal step cycle.
	 * @param lastStepTime The new time to use for this actors last completed step.
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
	public void setRelativeAngle(double relativeAngle) {
		if (getRelativeAngle() == relativeAngle) { return; }
		newState = true;
		this.newRelAngle = relativeAngle;
	}

	@Override
	public void setAbsoluteAngle(double absoluteAngle) {
		if (getAbsoluteAngle() == absoluteAngle) { return; }
		newState = true;
		this.newAbsAngle = absoluteAngle;
	}

	private void resetAngles() {
		this.relativeAngle = newRelAngle;
		super.setAbsoluteAngle(newAbsAngle);
		stepAngle = MathUtil.calcStepAngle(getAbsoluteAngle(), getRelativeAngle());
	}

	protected double getNewRelAngle() { return newRelAngle; }

	protected double getNewAbsAngle() { return newAbsAngle; }

	protected void setForcedState(boolean forcedState) { this.forcedState = forcedState; }
}
