package com.jenjinstudios.world.event;

import com.jenjinstudios.world.World;
import com.jenjinstudios.world.object.WorldObject;

import java.util.Collection;
import java.util.HashSet;

import static com.jenjinstudios.world.math.SightCalculator.getVisibleObjects;

/**
 * Observes world objects and triggers events when objects become invisible to
 * them.
 *
 * @author Caleb Brinkman
 */
public class NewlyInvisibleObserver extends
	  WorldObjectObserver<NewlyInvisibleEvent>
{
	private Collection<WorldObject> lastVisible = new HashSet<>(10);

	@Override
	public NewlyInvisibleEvent observePreUpdate(World world, WorldObject obj) {
		return null;
	}

	@Override
	public NewlyInvisibleEvent observeUpdate(World world, WorldObject obj) {
		return null;
	}

	@Override
	public NewlyInvisibleEvent observePostUpdate(World world,
												 WorldObject obj)
	{
		Collection<WorldObject> current = getVisibleObjects(world, obj);
		lastVisible.removeAll(current);

		NewlyInvisibleEvent newlyInvisibleEvent = null;
		if (!current.isEmpty()) {
			newlyInvisibleEvent = new NewlyInvisibleEvent(obj, lastVisible);
		}

		lastVisible = current;
		return newlyInvisibleEvent;
	}
}
