package com.jenjinstudios.world.object;

import com.jenjinstudios.world.reflection.DynamicMethod;
import com.jenjinstudios.world.task.NodeTask;

/**
 * Tracks the beginning and end of an object's update cycle.
 *
 * @author Caleb Brinkman
 */
public class TimingTask extends NodeTask
{
	/**
	 * Set the timing of the beginning of the WorldObject's update.
	 *
	 * @param object The object.
	 */
	@DynamicMethod
	public void onPreUpdate(WorldObject object) {
		object.getTiming().setLastUpdateStartTime(System.currentTimeMillis());
	}

	/**
	 * Set the timing of the end of the WorldObject's update.
	 *
	 * @param object The object.
	 */
	@DynamicMethod
	public void onPostUpdate(WorldObject object) {
		object.getTiming().setLastUpdateEndTime(System.currentTimeMillis());
	}
}
