package com.jenjinstudios.world.task;

import com.jenjinstudios.world.Cell;
import com.jenjinstudios.world.Node;
import com.jenjinstudios.world.World;
import com.jenjinstudios.world.Zone;
import com.jenjinstudios.world.object.WorldObject;

/**
 * Provides empty methods for a WorldObjectTask so that users can implement only needed methods.
 *
 * @author Caleb Brinkman
 */
public abstract class NodeTaskAdapter implements NodeTask
{

	@Override
	public void onPreUpdate(Node node) { }

	@Override
	public void onPreUpdate(World world) { }

	@Override
	public void onPreUpdate(Zone zone) { }

	@Override
	public void onPreUpdate(Cell cell) { }

	@Override
	public void onPreUpdate(WorldObject worldObject) { }

	@Override
	public void onUpdate(Node node) { }

	@Override
	public void onUpdate(World world) { }

	@Override
	public void onUpdate(Zone zone) { }

	@Override
	public void onUpdate(Cell cell) { }

	@Override
	public void onUpdate(WorldObject worldObject) { }

	@Override
	public void onPostUpdate(Node node) { }

	@Override
	public void onPostUpdate(World world) { }

	@Override
	public void onPostUpdate(Zone zone) { }

	@Override
	public void onPostUpdate(Cell cell) { }

	@Override
	public void onPostUpdate(WorldObject worldObject) { }
}
