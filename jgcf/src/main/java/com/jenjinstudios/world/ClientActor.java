package com.jenjinstudios.world;

import com.jenjinstudios.math.Vector2D;
import com.jenjinstudios.world.state.MoveState;

import java.util.LinkedList;

import static com.jenjinstudios.world.state.MoveState.IDLE;

/**
 * The {@code ClientActor} class is used to represent a server-side {@code Actor} object on the client side.  It is an
 * object capable of movement.
 * <p/>
 * Actors start with a {@code MoveState} with {@code MoveiDirection.IDLE}.  Each update, the Actor checks to see if
 * there are any MoveStates in the queue.  If there are, it checks the first state in line for the number of steps
 * needed before the state changes.  Once the number of steps has been reached, the state switches to that of the first
 * position in the queue, and the Actor's step counter is reset.  If an Actor "oversteps," which is determined if the
 * Actor has taken more than the required number of steps to change state, the Actor is moved back by the "overstepped"
 * number of states, the Actor's state is updated, and the Actor then takes the number of extra steps in the correct
 * relativeAngle.
 * <p/>
 * An Actor's state is considered "changed" when the Actor is facing a new relativeAngle or moving in a new
 * relativeAngle.
 * @author Caleb Brinkman
 */
public class ClientActor extends ClientObject
{
	/** The length of each step. */
	public static double STEP_LENGTH = 5;
	/** The next moves. */
	private final LinkedList<MoveState> nextMoveStates;
	/** The next move. */
	private MoveState nextState;
	/** The current move. */
	private MoveState currentMoveState;
	/** The number of steps taken since the last move. */
	private int stepsTaken = 0;

	/**
	 * Construct an Actor with the given name.
	 * @param id The Actor's ID.
	 * @param name The name.
	 */
	public ClientActor(int id, String name) {
		super(id, name);
		nextMoveStates = new LinkedList<>();
		currentMoveState = new MoveState(MoveState.IDLE, 0, 0);
	}

	/**
	 * Add a new MoveState to the actor's queue.
	 * @param newState The MoveState to add.
	 */
	public void addMoveState(MoveState newState) {
		synchronized (nextMoveStates)
		{
			if (nextState == null)
				nextState = newState;
			else nextMoveStates.add(newState);
		}
	}

	@Override
	public void update() {
		step();
	}

	/** Take a step, changing state and correcting steps if necessary. */
	private void step() {
		if (getOverstepped() >= 0) { correctOverSteps(getOverstepped()); }
		stepForward();
		stepsTaken++;
	}

	/** Take a step according to the current move state. */
	public void stepForward() {
		if (currentMoveState.relativeAngle == IDLE) return;
		setVector2D(getVector2D().getVectorInDirection(STEP_LENGTH, currentMoveState.stepAngle));

	}

	/**
	 * Correct the given number of steps at the specified angles.
	 * @param overstepped The number of steps over.
	 */
	private void correctOverSteps(int overstepped) {
		double stepAmount = STEP_LENGTH * overstepped;
		Vector2D backVector = getVector2D().getVectorInDirection(stepAmount, currentMoveState.stepAngle - Math.PI);
		Vector2D newVector = backVector.getVectorInDirection(stepAmount, nextState.stepAngle);
		setVector2D(newVector);
		stepsTaken = overstepped;
		resetState();
	}

	/**
	 * Determine if a state change is necessary.
	 * @return The number of steps needed to "correct" to set the actor to the correct state.  A negative number means no
	 *         state change is necessary.
	 */
	private int getOverstepped() { return (nextState != null) ? stepsTaken - nextState.stepsUntilChange : -1; }

	/** Reset the move state, relativeAngle, and newState flag when changing the move state. */
	private void resetState() {
		stepsTaken = 0;
		currentMoveState = nextState;
		nextState = nextMoveStates.poll();
		setDirection(currentMoveState.absoluteAngle);
	}

	/**
	 * Set the number of steps taken since the last move.
	 * @param stepsTaken The number of steps taken since the last move.
	 */
	public void setStepsTaken(int stepsTaken) {
		this.stepsTaken = stepsTaken;
	}

	/**
	 * Sets the current move state.  Should only be called when the actor is created.
	 * @param currentMoveState The current move state.
	 */
	public void setCurrentMoveState(MoveState currentMoveState) {
		this.currentMoveState = currentMoveState;
	}
}
