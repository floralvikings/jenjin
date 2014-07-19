package com.jenjinstudios.world.client;

import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.math.Angle;
import com.jenjinstudios.world.math.Vector2D;

/**
 * The Client-Side representation of a player.
 * @author Caleb Brinkman
 */
public class ClientPlayer extends Actor
{

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
		if (getForcedState() == null || !angle.equals(getForcedState().angle))
		{
			setForcedState(null);
			super.setAngle(angle);
		}
	}

	/**
	 * Calculate the step length at the current time.
	 * @return The current step length.
	 */
	@Override
	public double calcStepLength() {
		return ((System.currentTimeMillis() - (double) getLastStepTime()) / 1000) * getMoveSpeed();
	}
}
