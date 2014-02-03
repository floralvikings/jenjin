package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Vector2D;
import com.jenjinstudios.world.state.MoveState;

import static com.jenjinstudios.world.state.MoveState.IDLE;

/**
 * The player class represents a player in the server-side world.
 * @author Caleb Brinkman
 */
public class Player extends Actor
{
	/**
	 * Construct a player with the given username.
	 * @param username The username.
	 */
	public Player(String username) {
		super(username);
	}

	/**
	 * Construct a player with the given username and id.
	 * @param username The username.
	 * @param id The id.
	 */
	public Player(String username, int id) {
		super(username, id);
	}

	/** Take a step, changing state and correcting steps if necessary. */
	public void step() {
		int overstepped = getOverstepped();
		MoveState idleState = new MoveState(IDLE, getStepsTaken(), getCurrentMoveState().absoluteAngle);
		if (overstepped < MAX_CORRECT)
		{
			boolean stepCorrectionSuccess = (overstepped < 0) || (correctOverSteps(overstepped));
			if (!stepCorrectionSuccess || !stepForward())
			{
				setForcedState(idleState);
			}
		} else
		{
			setForcedState(getCurrentMoveState());
			stepForward();
		}
		incrementStepCounter();
	}

	/**
	 * Determine if a state change is necessary.
	 * @return The number of steps needed to "correct" to set the actor to the correct state.  A negative number means no
	 *         state change is necessary.
	 */
	private int getOverstepped() {
		return (getNextState() != null) ? getStepsTaken() - getNextState().stepsUntilChange : -1;
	}

	/**
	 * Correct the given number of steps at the specified angles.
	 * @param overstepped The number of steps over.
	 * @return Whether correcting the state was successful.
	 */
	private boolean correctOverSteps(int overstepped) {
		double stepAmount = STEP_LENGTH * overstepped;
		Vector2D backVector = getVector2D().getVectorInDirection(stepAmount, getCurrentMoveState().stepAngle - Math.PI);
		Vector2D newVector = backVector.getVectorInDirection(stepAmount, getNextState().stepAngle);
		Location newLocation = getWorld().getLocationForCoordinates(getZoneID(), newVector);
		boolean success = newLocation != null && !"false".equals(newLocation.getLocationProperties().getProperty("walkable"));
		resetState();
		if (success)
		{
			setStepsTaken(overstepped);
			setVector2D(newVector);
		}
		return success;
	}
}
