package com.jenjinstudios.world.event;

import com.jenjinstudios.world.Node;
import com.jenjinstudios.world.object.Actor;
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
public class NewlyInvisibleObserver extends NodeObserver<NewlyInvisibleEvent>
{
	private Collection<WorldObject> lastVisible = new HashSet<>(10);

	@Override
	protected NewlyInvisibleEvent observePreUpdate(Node node) { return null; }

	@Override
	protected NewlyInvisibleEvent observeUpdate(Node node) { return null; }

	@Override
	protected NewlyInvisibleEvent observePostUpdate(Node node) {
		NewlyInvisibleEvent newlyInvisibleEvent = null;
		if (node instanceof Actor) {
			Actor actor = (Actor) node;
			Collection<WorldObject> current = getVisibleObjects(actor);
			lastVisible.removeAll(current);

			if (!current.isEmpty()) {
				newlyInvisibleEvent = new NewlyInvisibleEvent(actor, lastVisible);
			}

			lastVisible = current;
		}
		return newlyInvisibleEvent;
	}
}
