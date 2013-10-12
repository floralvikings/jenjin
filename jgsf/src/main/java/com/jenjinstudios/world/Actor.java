package com.jenjinstudios.world;

import com.jenjinstudios.math.Vector2D;
import com.jenjinstudios.world.state.MoveState;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.jenjinstudios.world.state.MoveState.IDLE;


/**
 * Implement a WorldObject which is capable of movement. </p> Actors start with a {@code MoveState} with {@code
 * MoveState.IDLE}.  Each update, the Actor checks to see if there are any MoveStates in the queue.  If there are, it
 * checks the first state in line for the number of steps needed before the state changes.  Once the number of steps has
 * been reached, the state switches to that of the first position in the queue, and the Actor's step counter is reset.
 * If an Actor "oversteps," which is determined if the Actor has taken more than the required number of steps to change
 * state, the Actor is moved back by the "overstepped" number of states, the Actor's state is updated, and the Actor
 * then takes the number of extra steps in the correct direction. </p> An Actor's state is considered "changed" when the
 * Actor is facing a new direction or moving in a new direction. An actor's state is considered "forced" when the Actor
 * attempts to make an illegal move, and the world forces the actor to halt.  The actor's forced state will always be
 * facing the angle of the most recently added move state (even if the state causes an illegal move) and IDLE. The
 * "steps until change" value is determined from the number of steps that were taken until the state was forced.
 *
 * @author Caleb Brinkman
 */
public class Actor extends SightedObject
{
	/** The length of each step. */
	public static final float STEP_LENGTH = 5;
	/** The logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(Actor.class.getName());
	/** The constant for 2*PI. */
	public static double TWO_PI = (2 * Math.PI);
	/** The next move. */
	private final LinkedList<MoveState> nextMoveStates;
	/** The current move. */
	private MoveState currentMoveState;
	/** The number of steps taken since the last move. */
	private int stepsTaken = 0;
	/** The number of steps in the last completed move. */
	private int stepsInLastCompletedMove;
	/** Flags whether this actor has changed to a new state during this update. */
	private boolean newState;
	/** Keeps track of the next state in the queue. */
	private MoveState nextState;

	/** Construct a new Actor. */
	public Actor()
	{ this(DEFAULT_NAME); }

	/**
	 * Construct an Actor with the given name.
	 *
	 * @param name The name.
	 */
	public Actor(String name)
	{
		super(name);
		currentMoveState = new MoveState(IDLE, 0, 0);
		nextMoveStates = new LinkedList<>();
	}

	/**
	 * Add a new MoveState to the actor's queue.
	 *
	 * @param newState The MoveState to add.
	 */
	public void addMoveState(MoveState newState)
	{
		if (nextState == null)
			nextState = newState;
		else nextMoveStates.add(newState);
	}

	@Override
	public void update()
	{
		// Reset the new state flag.
		newState = false;

		// Store the current state (before step)
		Location oldLocation = getLocation();

		// Test for a state change, and change state if necessary
		int overStepped = getOverSteps();
		// TODO This is where the cap for maximum number of over steps needs to be implemented.
		// If the number of "over steps" is too high, discard all future move states and raise the forced-state flag
		if (overStepped >= 0)
		{
			// Store the old position / state
			boolean wasIdle = currentMoveState.direction == IDLE;
			double oldStepAngle = calculateStepAngle();
			// Change the state
			doStateChange();
			double newStepAngle = calculateStepAngle();
			// Correct for any "over" steps.
			correctSteps(overStepped, oldStepAngle, newStepAngle, wasIdle);
		}
		stepForward(calculateStepAngle());
		stepsTaken++;

		// If we're in a new locations after stepping, update the visible array.
		if (oldLocation != getLocation() || getVisibleLocations().isEmpty())
			resetVisibleLocations();
		// Reset the array of visible actors.
		resetVisibleObjects();
	}

	/**
	 * Get the number of steps past the number needed for the state change, which will be negative if a state change is not
	 * needed.
	 *
	 * @return The number of steps past the number needed for the state change, which will be negative if a state change is
	 *         not needed.
	 */
	private int getOverSteps()
	{
		if (nextState == null)
			return -1;
		else return stepsTaken - nextState.stepsUntilChange;
	}

	/** Change to the next state, and correct for any over steps. */
	private void doStateChange()
	{
		stepsInLastCompletedMove = nextState.stepsUntilChange;
		currentMoveState = nextState;

		if (!nextMoveStates.isEmpty())
			nextState = nextMoveStates.remove();

		newState = true;
		stepsTaken = 0;
		setDirection(currentMoveState.moveAngle);

	}

	/**
	 * Correct the given number of steps at the specified angles.
	 *
	 * @param overstepped  The number of steps over.
	 * @param oldStepAngle The angle of previous movement.
	 * @param newStepAngle The angle of current movement.
	 * @param wasIdle      Whether the previous state was IDLE.
	 */
	private void correctSteps(int overstepped, double oldStepAngle, double newStepAngle, boolean wasIdle)
	{
		if (!wasIdle)
			for (int i = 0; i < overstepped; i++)
				stepBack(oldStepAngle);
		for (int i = 0; i < overstepped; i++)
			stepForward(newStepAngle);
	}

	/**
	 * Take a step according to the current move state.
	 *
	 * @param stepAngle The angle in which the actor should step.
	 */
	public void stepForward(double stepAngle)
	{
		if (currentMoveState.direction == IDLE) return;
		Vector2D oldPos = getVector2D();
		try
		{
			setVector2D(getVector2D().getVectorInDirection(STEP_LENGTH, stepAngle));
		} catch (InvalidLocationException ex)
		{
			// TODO This is where we need to force-idle, and raise the forced-state flag.
			forceIdle(oldPos);
		}
	}

	/**
	 * Force the actor into an idle state, clear all pending states, and raise the forced state flag.
	 *
	 * @param oldPosition The position before the invalid location.
	 */
	private void forceIdle(Vector2D oldPosition)
	{
		currentMoveState = new MoveState(IDLE, stepsTaken, currentMoveState.moveAngle);
		try
		{
			setVector2D(oldPosition);
		} catch (InvalidLocationException e)
		{
			LOGGER.log(Level.SEVERE, "Couldn't reset vector after illegal set.");
		}
		nextMoveStates.clear();
	}

	/**
	 * Take a step back in according to the given forward angle.
	 *
	 * @param stepAngle The angle in which to move backward.
	 */
	private void stepBack(double stepAngle)
	{
		stepAngle -= Math.PI;

		if (currentMoveState.direction == IDLE) return;

		try
		{
			setVector2D(getVector2D().getVectorInDirection(STEP_LENGTH, stepAngle));
		} catch (InvalidLocationException ex)
		{
			// Something very strange is happening if corrected steps lead out of bounds...
			LOGGER.log(Level.SEVERE, "Error while correcting client steps.", ex);
		}
	}

	/**
	 * Calculate the angle of the direction in which the actor should step.
	 *
	 * @return The angle of the direction in which the actor should step.
	 */
	private double calculateStepAngle()
	{
		double sAngle = getDirection() + currentMoveState.direction;
		return (sAngle < 0) ? (sAngle + TWO_PI) : (sAngle % TWO_PI);
	}

	/**
	 * Get whether this actor has initialized a new state during this update.
	 *
	 * @return Whether the actor has changed moved states since the beginning of this update.
	 */
	public boolean isNewState()
	{return newState;}

	/**
	 * Get the current direction in which the object is moving.
	 *
	 * @return The current direction in which the object is moving.
	 */
	public double getCurrentDirection()
	{return currentMoveState.direction;}

	/**
	 * Get the direction in which the object is currently facing.
	 *
	 * @return The direction in which the object is currently facing.
	 */
	public double getCurrentAngle()
	{return currentMoveState.moveAngle;}

	/**
	 * Get the number of steps taken since the last state change.
	 *
	 * @return The number of steps taken since the last state change.
	 */
	public int getStepsTaken()
	{return stepsTaken;}

	/**
	 * Get the steps taken to complete the previous move.
	 *
	 * @return The steps taken to complete the previous move.
	 */
	public int getStepsInLastCompletedMove()
	{return stepsInLastCompletedMove;}

	/**
	 * Get the actor's current move state.
	 *
	 * @return The actor's current move state.
	 */
	public MoveState getCurrentMoveState()
	{ return currentMoveState; }
}
