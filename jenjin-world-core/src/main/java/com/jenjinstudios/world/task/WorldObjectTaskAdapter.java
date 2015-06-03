package com.jenjinstudios.world.task;

import com.jenjinstudios.world.Node;

/**
 * Provides empty methods for a WorldObjectTask so that users can implement only needed methods.
 *
 * @author Caleb Brinkman
 */
public abstract class WorldObjectTaskAdapter implements WorldObjectTask
{

	@Override
	public void onPreUpdate(Node node) { }

	@Override
	public void onUpdate(Node node) { }

	@Override
	public void onPostUpdate(Node node) { }
}
