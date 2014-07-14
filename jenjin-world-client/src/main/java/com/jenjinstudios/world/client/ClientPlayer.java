package com.jenjinstudios.world.client;

import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;
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
		setVector2D(Vector2D.ORIGIN);
	}

	@Override
	public void setAngle(Angle angle) {
		if (forcedMoveState == null || !angle.equals(forcedMoveState.angle))
		{
			forcedMoveState = null;
			super.setAngle(angle);
		}
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
	public double calcStepLength() {
		return ((System.nanoTime() - (double) getLastStepTime()) / 1000000000)
			  * ClientActor.MOVE_SPEED;
	}
}
