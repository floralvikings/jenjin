package com.jenjinstudios.world;

import com.jenjinstudios.jgsf.WorldServer;
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
	public static final double STEP_LENGTH = (double) Location.SIZE / (double) WorldServer.DEFAULT_UPS;
	/** The maximum number of steps this actor is allowed to correct. */
	public static final int MAX_CORRECT = 10;
	/** The next move. */
	private final LinkedList<MoveState> nextMoveStates;
	/** The current move. */
	private MoveState currentMoveState;
	/** The number of steps taken since the last move. */
	private int stepsTaken = 0;
	/** Flags whether this actor has changed to a new state during this update. */
	private boolean newState;
	/** Keeps track of the next state in the queue. */
	private MoveState nextState;
	/** Flags whether the state of this actor was forced during this update. */
	private boolean forcedState;

	/** Construct a new Actor. */
	public Actor() { this(DEFAULT_NAME); }

	/**
	 * Construct an Actor with the given name.
	 * @param name The name.
	 */
	public Actor(String name) {
		super(name);
		currentMoveState = new MoveState(IDLE, 0, 0);
		nextMoveStates = new LinkedList<>();
	}

	/**
	 * Add a new MoveState to the actor's queue.
	 * @param newState The MoveState to add.
	 */
	public void addMoveState(MoveState newState) {
		if (nextState == null)
		{ nextState = newState; } else nextMoveStates.add(newState);
	}

	@Override
	public void update() {
		resetFlags();
		Location locationBeforeStep = getLocation();
		step();
		// If we're in a new locations after stepping, update the visible array.
		if (locationBeforeStep != getLocation() || getVisibleLocations().isEmpty())
			resetVisibleLocations();
		// Reset the array of visible actors.
		resetVisibleObjects();
	}

	/** Take a step, changing state and correcting steps if necessary. */
	public void step() {
		int overstepped = getOverstepped();
		try
		{
			if (overstepped >= MAX_CORRECT)
			{
				setForcedState(currentMoveState);
			} else if (overstepped >= 0)
			{
				doStateChange(overstepped);
			}
			stepForward();
		} catch (InvalidLocationException ex)
		{
			setForcedState(new MoveState(IDLE, stepsTaken, currentMoveState.absoluteAngle));
		}
		stepsTaken++;
	}

	/**
	 * Perform a state change.
	 * @param overStepped The number of steps beyond what the actor should have taken.
	 * @throws InvalidLocationException If correcting the actor's steps causes the actor to move into an invalid location.
	 */
	private void doStateChange(int overStepped) throws InvalidLocationException {
		MoveState oldState = currentMoveState;
		resetState();
		correctOverSteps(overStepped, oldState);
	}

	/**
	 * Correct the given number of steps at the specified angles.
	 * @param overstepped The number of steps over.
	 * @param oldState The
	 * @throws InvalidLocationException If the step correction tries to place the actor in an invalid location.
	 */
	private void correctOverSteps(int overstepped, MoveState oldState) throws InvalidLocationException {
		if (oldState.relativeAngle != MoveState.IDLE)
		{
			for (int i = 0; i < overstepped; i++) { stepBack(oldState.stepAngle); }
		}
		for (int i = 0; i < overstepped; i++) { stepForward(); }
		stepsTaken = overstepped;
	}

	/**
	 * Take a step back in according to the given forward angle.
	 * @param stepAngle The angle in which to move backward.
	 * @throws InvalidLocationException If stepping back causes the actor to be placed in an invalid location.
	 */
	private void stepBack(double stepAngle) throws InvalidLocationException {
		stepAngle -= Math.PI;
		setVector2D(getVector2D().getVectorInDirection(STEP_LENGTH, stepAngle));
	}

	/**
	 * Take a step according to the current move state.
	 * @throws InvalidLocationException If the step forward would place the actor in an invalid location.
	 */
	public void stepForward() throws InvalidLocationException {
		if (currentMoveState.relativeAngle == IDLE) return;
		setVector2D(getVector2D().getVectorInDirection(STEP_LENGTH, currentMoveState.stepAngle));
	}

	/**
	 * Determine if a state change is necessary.
	 * @return The number of steps needed to "correct" to set the actor to the correct state.  A negative number means no
	 *         state change is necessary.
	 */
	private int getOverstepped() { return (nextState != null) ? stepsTaken - nextState.stepsUntilChange : -1; }

	/** Reset the flags used by this actor. */
	public void resetFlags() {
		newState = false;
		forcedState = false;
	}

	/**
	 * Get whether this actor has initialized a new state during this update.
	 * @return Whether the actor has changed moved states since the beginning of this update.
	 */
	public boolean isNewState() { return newState; }

	/**
	 * Get the relativeAngle in which the object is currently facing.
	 * @return The relativeAngle in which the object is currently facing.
	 */
	public double getMoveAngle() { return currentMoveState.absoluteAngle; }

	/**
	 * Get the number of steps taken since the last state change.
	 * @return The number of steps taken since the last state change.
	 */
	public int getStepsTaken() { return stepsTaken; }

	/**
	 * Get the actor's current move state.
	 * @return The actor's current move state.
	 */
	public MoveState getCurrentMoveState() { return currentMoveState; }

	/**
	 * Get whether this actor was forced into a state during the most recent update.
	 * @return Whether this actor was forced into a state during the most recent update.
	 */
	public boolean isForcedState() { return forcedState; }

	/**
	 * Force the state of this actor to the given state and raise the forcedState flag.
	 * @param forced The state to which to force this actor.
	 */
	public void setForcedState(MoveState forced) {
		forcedState = true;
		nextMoveStates.clear();
		nextState = forced;
		stepBackToValid(currentMoveState.stepAngle);
		resetState();
	}

	/**
	 * Step the actor back to a valid location.
	 * @param stepAngle The angle the actor is moving.
	 */
	private void stepBackToValid(double stepAngle) {
		stepAngle -= Math.PI;
		boolean isValid = false;
		int stepsToTake = 1;
		while (!isValid)
		{
			try
			{
				setVector2D(getVector2D().getVectorInDirection(STEP_LENGTH * stepsToTake, stepAngle));
				isValid = true;
			} catch (InvalidLocationException ex)
			{
				stepsToTake++;
			}
		}
	}

	/** Reset the move state, relativeAngle, and newState flag when changing the move state. */
	private void resetState() {
		stepsTaken = 0;
		currentMoveState = nextState;
		nextState = nextMoveStates.poll();
		newState = true;
		setDirection(currentMoveState.absoluteAngle);
	}

	/**
	 * Get the relative angle of movement of this actor.
	 * @return The relative angle of movement of this actor.
	 */
	public double getMoveDirection() { return currentMoveState.relativeAngle; }
}
