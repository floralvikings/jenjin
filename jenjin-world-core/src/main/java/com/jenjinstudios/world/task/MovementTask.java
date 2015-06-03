package com.jenjinstudios.world.task;

import com.jenjinstudios.world.Node;
import com.jenjinstudios.world.object.WorldObject;
import com.jenjinstudios.world.util.ActorUtils;

/**
 * Causes an actor to during an update.
 *
 * @author Caleb Brinkman
 */
public class MovementTask extends WorldObjectTaskAdapter
{

	@Override
	public void onUpdate(Node node) {
		if (node instanceof WorldObject) {
			WorldObject object = (WorldObject) node;
			if (object.getGeometry().getOrientation().isNotIdle()) {
				ActorUtils.stepForward(object);
			}
		}
	}
}
