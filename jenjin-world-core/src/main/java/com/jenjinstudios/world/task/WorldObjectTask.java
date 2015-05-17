package com.jenjinstudios.world.task;

import com.jenjinstudios.world.World;
import com.jenjinstudios.world.object.WorldObject;

/**
 * Represents a task to be executed during the pre-update, in-update, or post-update world loop cycle of a WorldObject.
 *
 * @author Caleb Brinkman
 */
public interface WorldObjectTask
{
	/**
	 * This method will be executed before the object updates.
	 *
	 * @param world The world in which the task should be executed
	 * @param worldObject The object on which to perform a task.
	 */
	void onPreUpdate(World world, WorldObject worldObject);

	/**
	 * This method will be executed during the object's update.
	 *
	 * @param world The world in which the task should be executed
	 * @param worldObject The object on which to perform the task.
	 */
	void onUpdate(World world, WorldObject worldObject);

	/**
	 * This method will be executed after the object updates.
	 *
	 * @param world The world in which the task should be executed
	 * @param worldObject The object on which to perform the task.
	 */
	void onPostUpdate(World world, WorldObject worldObject);
}
