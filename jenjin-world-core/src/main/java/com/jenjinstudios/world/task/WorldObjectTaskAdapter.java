package com.jenjinstudios.world.task;

import com.jenjinstudios.world.World;
import com.jenjinstudios.world.object.WorldObject;

/**
 * Provides empty methods for a WorldObjectTask so that users can implement only needed methods.
 *
 * @author Caleb Brinkman
 */
public abstract class WorldObjectTaskAdapter implements WorldObjectTask
{
	@Override
	public void onPreUpdate(World world, WorldObject worldObject) { }

	@Override
	public void onUpdate(World world, WorldObject worldObject) { }

	@Override
	public void onPostUpdate(World world, WorldObject worldObject) { }
}
