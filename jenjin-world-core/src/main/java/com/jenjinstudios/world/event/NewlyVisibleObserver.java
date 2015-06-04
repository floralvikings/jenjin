package com.jenjinstudios.world.event;

import com.jenjinstudios.world.Node;
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
public class NewlyVisibleObserver extends NodeObserver<NewlyVisibleEvent>
{
	private Collection<WorldObject> lastVisible = new HashSet<>(10);

	@Override
	protected NewlyVisibleEvent observePreUpdate(Node node) { return null; }

	@Override
	protected NewlyVisibleEvent observeUpdate(Node node) { return null; }

	@Override
	protected NewlyVisibleEvent observePostUpdate(Node node) {
		NewlyVisibleEvent newlyVisibleEvent = null;
		if (node instanceof WorldObject) {
			WorldObject object = (WorldObject) node;
			Collection<WorldObject> current = getVisibleObjects(object);
			Collection<WorldObject> newVisible = new LinkedList<>(current);
			newVisible.removeAll(lastVisible);

			if (!newVisible.isEmpty()) {
				newlyVisibleEvent = new NewlyVisibleEvent(object, newVisible);
			}

			lastVisible = current;
		}
		return newlyVisibleEvent;
	}
}
