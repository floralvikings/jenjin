package com.jenjinstudios.world.event;

import com.jenjinstudios.world.World;
import com.jenjinstudios.world.object.WorldObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import static com.jenjinstudios.world.math.SightCalculator.getVisibleObjects;

/**
 * Observes a world object and dispatches a NewlyVisibleEvent when to handlers
 * when an object enters the observed object's view radius.
 *
 * @author Caleb Brinkman
 */
public class NewlyVisibleObserver extends WorldObjectObserver<NewlyVisibleEvent>
{
	private Collection<WorldObject> lastVisible = new HashSet<>(10);

	@Override
	public NewlyVisibleEvent observePreUpdate(World world, WorldObject obj) {
		return null;
	}

	@Override
	public NewlyVisibleEvent observeUpdate(World world, WorldObject obj) {
		return null;
	}

	@Override
	public NewlyVisibleEvent observePostUpdate(World world, WorldObject obj) {
		Collection<WorldObject> current = getVisibleObjects(world, obj);
		Collection<WorldObject> newVisible = new LinkedList<>(current);
		newVisible.removeAll(lastVisible);

		NewlyVisibleEvent newlyVisibleEvent = null;
		if (!newVisible.isEmpty()) {
			newlyVisibleEvent = new NewlyVisibleEvent(obj, newVisible);
		}

		lastVisible = current;
		return newlyVisibleEvent;
	}
}
