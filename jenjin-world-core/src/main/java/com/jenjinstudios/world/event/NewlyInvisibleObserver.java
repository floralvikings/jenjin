package com.jenjinstudios.world.event;

import com.jenjinstudios.world.object.Actor;
import com.jenjinstudios.world.object.WorldObject;
import com.jenjinstudios.world.reflection.DynamicMethod;

import java.util.Collection;
import java.util.HashSet;

import static com.jenjinstudios.world.math.SightCalculator.getVisibleObjects;

/**
 * Observes world objects and triggers events when objects become invisible to them.
 *
 * @author Caleb Brinkman
 */
public class NewlyInvisibleObserver extends NodeObserver<NewlyInvisibleEvent>
{
	private Collection<WorldObject> lastVisible = new HashSet<>(10);

	/**
	 * Observe the given actor to determine if any objects have become invisible to it.
	 *
	 * @param actor The actor to observe.
	 *
	 * @return An even containing any objects that have become invisible to the actor.
	 */
	@DynamicMethod
	protected NewlyInvisibleEvent observePostUpdate(Actor actor) {
		Collection<WorldObject> current = getVisibleObjects(actor);
		lastVisible.removeAll(current);

		NewlyInvisibleEvent newlyInvisibleEvent = null;
		if (!current.isEmpty()) {
			newlyInvisibleEvent = new NewlyInvisibleEvent(actor, lastVisible);
		}

		lastVisible = current;

		return newlyInvisibleEvent;
	}
}
