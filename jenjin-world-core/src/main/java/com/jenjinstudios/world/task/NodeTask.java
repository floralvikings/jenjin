package com.jenjinstudios.world.task;

import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.Node;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.object.WorldObject;

/**
 * Represents a task to be executed during the pre-update, in-update, or post-update world loop cycle of a WorldObject.
 *
 * @author Caleb Brinkman
 */
public interface NodeTask
{

	/**
	 * This method will be executed before the node updates.
	 *
	 * @param node The node on which the task should be executed
	 */
	void onPreUpdate(Node node);

	/**
	 * This method will be executed before the world updates.
	 *
	 * @param world The world on which the task should be executed
	 */
	void onPreUpdate(World world);

	/**
	 * This method will be executed before the zone updates.
	 *
	 * @param zone The zone on which the task should be executed.
	 */
	void onPreUpdate(Zone zone);

	/**
	 * This method will be executed before the cell updates.
	 *
	 * @param cell The cell on which the task should be executed.
	 */
	void onPreUpdate(Cell cell);

	/**
	 * This method will be executed before the WorldObject updates.
	 *
	 * @param worldObject The WorldObject on which the task will be executed.
	 */
	void onPreUpdate(WorldObject worldObject);

	/**
	 * This method will be executed when the node updates.
	 *
	 * @param node The node on which the task should be executed.
	 */
	void onUpdate(Node node);

	/**
	 * This method will be executed when the world updates.
	 *
	 * @param world The world on which the task should be executed.
	 */
	void onUpdate(World world);

	/**
	 * This method will be executed when the zone updates.
	 *
	 * @param zone The zone on which the task should be executed.
	 */
	void onUpdate(Zone zone);

	/**
	 * This method will be executed when the cell updates.
	 *
	 * @param cell The cell on which the task should be executed.
	 */
	void onUpdate(Cell cell);

	/**
	 * This method will be executed when the worldObject updates.
	 *
	 * @param worldObject The worldObject on which the task should be executed.
	 */
	void onUpdate(WorldObject worldObject);

	/**
	 * This method will be executed after the node updates.
	 *
	 * @param node The node on which the task should be executed.
	 */
	void onPostUpdate(Node node);

	/**
	 * This method will be executed after the world updates.
	 *
	 * @param world The world on which the task should be executed.
	 */
	void onPostUpdate(World world);

	/**
	 * This method will be executed after the zone updates.
	 *
	 * @param zone The zone on which the task should be executed.
	 */
	void onPostUpdate(Zone zone);

	/**
	 * This method will be executed after the cell updates.
	 *
	 * @param cell The cell on which the task should be executed.
	 */
	void onPostUpdate(Cell cell);

	/**
	 * This method will be executed after the worldObject updates.
	 *
	 * @param worldObject The worldObject on which the task should be executed.
	 */
	void onPostUpdate(WorldObject worldObject);
}
