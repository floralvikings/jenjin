package com.jenjinstudios.world;

import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.state.MoveState;

/**
 * The Client-Side representation of a player.
 * @author Caleb Brinkman
 */
public class ClientPlayer extends Actor
{
	/** The recent forced state. */
	private MoveState forcedMoveState;

	/**
	 * Construct an Actor with the given name.
	 * @param id The Actor's ID.
	 * @param name The name.
	 */
	public ClientPlayer(int id, String name) {
		super(name);
		super.setId(id);
		setVector2D(0, 0);
	}

	public void setAngle(Angle angle) {
		if (forcedMoveState != null &&
			  angle.equals(new Angle(forcedMoveState.absoluteAngle, forcedMoveState.relativeAngle)))
		{
			return;
		}
		forcedMoveState = null;
		super.setAngle(angle);
	}

	/** Mark that the actor has been forced to its current position. */
	public void forcePosition() {
		forcedMoveState = new MoveState(getAngle(), getVector2D(), getWorld().getLastUpdateStarted());
		setForcedState(forcedMoveState);
	}

	/**
	 * Calculate the step length at the current time.
	 * @return The current step length.
	 */
	@Override
	protected double calcStepLength() {
		return ((System.nanoTime() - (double) getLastStepTime()) / 1000000000)
				* ClientActor.MOVE_SPEED;
	}
}
