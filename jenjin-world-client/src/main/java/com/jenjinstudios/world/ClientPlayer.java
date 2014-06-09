package com.jenjinstudios.world;

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

	/**
	 * Set the absolute angle of this player.
	 * @param absoluteAngle The new absolute angle of this player.
	 */
	public void setAbsoluteAngle(double absoluteAngle) {
		// Helps reduce repetitive messages trying to force walking through a wall.
		if (forcedMoveState != null && absoluteAngle == forcedMoveState.absoluteAngle)
		{
			return;
		}
		forcedMoveState = null;
		super.setAbsoluteAngle(absoluteAngle);
	}

	public void setRelativeAngle(double relativeAngle) {
		// TODO Better way of handling this check?
		// Helps reduce repetitive messages trying to force walking through a wall.
		if (forcedMoveState != null && relativeAngle == forcedMoveState.relativeAngle)
		{
			return;
		}
		forcedMoveState = null;
		super.setRelativeAngle(relativeAngle);
	}

	/** Mark that the actor has been forced to its current position. */
	public void forcePosition() {
		forcedMoveState = new MoveState(getNewRelAngle(), getNewAbsAngle(),
				getVector2D(), getWorld().getLastUpdateStarted());
		setForcedState(forcedMoveState);
	}

	/**
	 * Calculate the step length at the current time.
	 * @return The current step length.
	 */
	protected double calcStepLength() {
		return ((System.nanoTime() - (double) getLastStepTime()) / 1000000000)
				* ClientActor.MOVE_SPEED;
	}
}
