package com.jenjinstudios.world.task;

import com.jenjinstudios.world.Node;
import com.jenjinstudios.world.object.WorldObject;

/**
 * Tracks the beginning and end of an object's update cycle.
 *
 * @author Caleb Brinkman
 */
public class TimingTask extends WorldObjectTaskAdapter
{

	@Override
	public void onPreUpdate(Node node) {
		if (node instanceof WorldObject) {
			WorldObject object = (WorldObject) node;
			object.getTiming().setLastUpdateStartTime(System.currentTimeMillis());
		}
	}

	@Override
	public void onPostUpdate(Node node) {
		if (node instanceof WorldObject) {
			WorldObject object = (WorldObject) node;
			object.getTiming().setLastUpdateEndTime(System.currentTimeMillis());
		}
	}
}
