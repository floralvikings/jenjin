package com.jenjinstudios.world.event;

import com.jenjinstudios.world.object.Actor;
import com.jenjinstudios.world.object.WorldObject;
import com.jenjinstudios.world.reflection.DynamicMethod;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import static com.jenjinstudios.world.math.SightCalculator.getVisibleObjects;

/**
 * Observes a world object and dispatches a NewlyVisibleEvent when to handlers when an object enters the observed
 * object's view radius.
 *
 * @author Caleb Brinkman
 */
public class NewlyVisibleObserver extends NodeObserver<NewlyVisibleEvent>
{
	private Collection<WorldObject> lastVisible = new HashSet<>(10);

	/**
	 * Observe the given actor to determine if any objects have become visible to it.
	 *
	 * @param actor The actor to observe.
	 *
	 * @return An even containing any objects that have become visible to the actor.
	 */
	@DynamicMethod
	protected NewlyVisibleEvent observePostUpdate(Actor actor) {
		Collection<WorldObject> current = getVisibleObjects(actor);
		Collection<WorldObject> newVisible = new LinkedList<>(current);
		newVisible.removeAll(lastVisible);

		NewlyVisibleEvent newlyVisibleEvent = null;
		if (!newVisible.isEmpty()) {
			newlyVisibleEvent = new NewlyVisibleEvent(actor, newVisible);
		}

		lastVisible = current;
		return newlyVisibleEvent;
	}
}
