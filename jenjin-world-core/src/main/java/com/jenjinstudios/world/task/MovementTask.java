package com.jenjinstudios.world.task;

import com.jenjinstudios.world.Actor;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.util.ActorUtils;

/**
 * Causes an actor to during an update.
 *
 * @author Caleb Brinkman
 */
public class MovementTask extends WorldObjectTaskAdapter
{
	@Override
	public void onPreUpdate(World world, WorldObject worldObject) {
		if (worldObject instanceof Actor) {
			Actor actor = (Actor) worldObject;
			actor.setForcedState(null);
		}
	}

	@Override
	public void onUpdate(World world, WorldObject worldObject) {
		if (worldObject instanceof Actor) {
			Actor actor = (Actor) worldObject;
			if (actor.getGeometry2D().getOrientation().isNotIdle()) {
				ActorUtils.stepForward(actor);
			}
		}
	}
}
