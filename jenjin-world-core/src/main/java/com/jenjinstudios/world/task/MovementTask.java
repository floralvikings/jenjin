package com.jenjinstudios.world.task;

import com.jenjinstudios.world.object.Actor;
import com.jenjinstudios.world.reflection.DynamicMethod;

/**
 * Causes an actor to during an update.
 *
 * @author Caleb Brinkman
 */
public class MovementTask extends NodeTask
{
	/**
	 * Move the Actor forward according to its movement speed and the time past since the last time it was updated.
	 *
	 * @param actor The actor to move forward.
	 */
	@DynamicMethod
	public void onUpdate(Actor actor) {
		// TODO Implement movement
	}
}
