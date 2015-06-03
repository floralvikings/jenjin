package com.jenjinstudios.world.task;

import com.jenjinstudios.world.Node;

/**
 * Represents a task to be executed during the pre-update, in-update, or post-update world loop cycle of a WorldObject.
 *
 * @author Caleb Brinkman
 */
public interface WorldObjectTask
{

	/**
	 * This method will be executed before the node updates.
	 *
	 * @param node The node on which the task should be executed
	 */
	void onPreUpdate(Node node);

	/**
	 * This method will be executed when the node updates.
	 *
	 * @param node The node on which the task should be executed.
	 */
	void onUpdate(Node node);

	/**
	 * This method will be executed after the node updates.
	 *
	 * @param node The node on which the task should be executed.
	 */
	void onPostUpdate(Node node);
}
