package com.jenjinstudios.world.task;

import com.jenjinstudios.world.World;
import com.jenjinstudios.world.object.WorldObject;

/**
 * Tracks the beginning and end of an object's update cycle.
 *
 * @author Caleb Brinkman
 */
public class TimingTask extends WorldObjectTaskAdapter
{
	@Override
	public void onPreUpdate(World world, WorldObject worldObject) {
		worldObject.setLastUpdateStartTime(System.currentTimeMillis());
	}

	@Override
	public void onPostUpdate(World world, WorldObject worldObject) {
		worldObject.setLastUpdateEndTime(System.currentTimeMillis());
	}
}
