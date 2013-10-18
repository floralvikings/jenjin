package com.jenjinstudios.world;

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
	/** The maximum number of steps this actor is allowed to correct. */
	public static final int MAX_CORRECT = 10;
	/** The logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(Actor.class.getName());
	/** The next move. */
	private final LinkedList<MoveState> nextMoveStates;
	/** The current move. */
	private MoveState currentMoveState;
	/** The number of steps taken since the last move. */
	private int stepsTaken = 0;
	/** The number of steps until the actor changed to the current state. */
	private int stepsUntilChange;
	/** Flags whether this actor has changed to a new state during this update. */
	private boolean newState;
	/** Keeps track of the next state in the queue. */
	private MoveState nextState;
	/** Flags whether the state of this actor was forced during this update. */
	private boolean forcedState;

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
		resetFlags();
		Location locationBeforeStep = getLocation();
		step();
		// If we're in a new locations after stepping, update the visible array.
		if (locationBeforeStep != getLocation() || getVisibleLocations().isEmpty())
			resetVisibleLocations();
		// Reset the array of visible actors.
		resetVisibleObjects();
	}

	/** Reset the flags used by this actor. */
	private void resetFlags()
	{
		newState = false;
		forcedState = false;
	}

	/** Take a step, changing state and correcting steps if necessary. */
	public void step()
	{
		tryStateChange();
		stepForward();
		stepsTaken++;
	}

	/** Change to the next state, and correct for any over steps. */
	private void tryStateChange()
	{
		if (nextState == null) return;
		int overstepped = stepsTaken - nextState.stepsUntilChange;
		System.out.println("Correcting: " + overstepped);
		if (overstepped >= MAX_CORRECT)
		{
			stopMaxCorrect();
		} else if (overstepped >= 0)
		{
			doStateChange(overstepped);
		}
	}

	/**
	 * Perform a state change.
	 *
	 * @param overStepped The number of steps beyond what the actor should have taken.
	 */
	private void doStateChange(int overStepped)
	{
		// Store the old state.
		MoveState oldState = currentMoveState;
		stepsUntilChange = nextState.stepsUntilChange;
		resetState();
		correctOverSteps(overStepped, oldState);
	}

	/** Reset the move state, direction, and newState flag when changing the move state. */
	private void resetState()
	{
		currentMoveState = nextState;
		nextState = nextMoveStates.poll();
		newState = true;
		setDirection(currentMoveState.moveAngle);
	}

	/** Stop the actor from correcting more steps than the allowed maximum. */
	private void stopMaxCorrect()
	{
		nextState = null;
		nextMoveStates.clear();
		setForcedState(currentMoveState);
	}

	/**
	 * Correct the given number of steps at the specified angles.
	 *
	 * @param overstepped The number of steps over.
	 * @param oldState    The
	 */
	private void correctOverSteps(int overstepped, MoveState oldState)
	{
		if (oldState.direction != MoveState.IDLE)
		{
			for (int i = 0; i < overstepped; i++)
			{
				stepBack(oldState.stepAngle);
			}
		}
		for (int i = 0; i < overstepped; i++)
		{
			stepForward();
		}
		stepsTaken = overstepped;
	}

	/** Take a step according to the current move state. */
	public void stepForward()
	{
		if (currentMoveState.direction == IDLE) return;
		try
		{
			setVector2D(getVector2D().getVectorInDirection(STEP_LENGTH, currentMoveState.stepAngle));
		} catch (InvalidLocationException ex)
		{
			setForcedState(new MoveState(IDLE, stepsTaken, currentMoveState.moveAngle));
		}
	}

	/**
	 * Take a step back in according to the given forward angle.
	 *
	 * @param stepAngle The angle in which to move backward.
	 */
	private void stepBack(double stepAngle)
	{
		stepAngle -= Math.PI;
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
	 * Get whether this actor has initialized a new state during this update.
	 *
	 * @return Whether the actor has changed moved states since the beginning of this update.
	 */
	public boolean isNewState()
	{return newState;}

	/**
	 * Get the direction in which the object is currently facing.
	 *
	 * @return The direction in which the object is currently facing.
	 */
	public double getMoveAngle()
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
	 * @return The number of steps until the actor changed to the current state.
	 */
	public int getStepsUntilChange()
	{return stepsUntilChange;}

	/**
	 * Get the actor's current move state.
	 *
	 * @return The actor's current move state.
	 */
	public MoveState getCurrentMoveState()
	{ return currentMoveState; }

	/**
	 * Get whether this actor was forced into a state during the most recent update.
	 *
	 * @return Whether this actor was forced into a state during the most recent update.
	 */
	public boolean isForcedState()
	{ return forcedState; }

	/**
	 * Force the state of this actor to the given state and raise the forcedState flag.
	 *
	 * @param forced The state to which to force this actor.
	 */
	public void setForcedState(MoveState forced)
	{
		System.out.println("Forcing State: " + forced);
		stepBack(currentMoveState.stepAngle);
		this.currentMoveState = forced;
		forcedState = true;
	}

	/**
	 * Get the relative angle of movement of this actor.
	 *
	 * @return The relative angle of movement of this actor.
	 */
	public double getMoveDirection()
	{
		return currentMoveState.direction;
	}
}
