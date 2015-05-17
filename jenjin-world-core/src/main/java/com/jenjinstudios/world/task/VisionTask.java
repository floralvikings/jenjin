package com.jenjinstudios.world.task;

import com.jenjinstudios.world.World;
import com.jenjinstudios.world.math.SightCalculator;
import com.jenjinstudios.world.object.WorldObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Tracks visible objects.
 *
 * @author Caleb Brinkman
 */
public class VisionTask extends WorldObjectTaskAdapter
{
	private final transient Set<WorldObject> visibleObjects = new HashSet<>(10);
	private final transient Set<WorldObject> newlyVisibleObjects = new HashSet<>(10);
	private final transient Set<WorldObject> newlyInvisibleObjects = new HashSet<>(10);

	public Set<WorldObject> getVisibleObjects() {
		synchronized (visibleObjects)
		{
			return new HashSet<>(visibleObjects);
		}
	}

	public Set<WorldObject> getNewlyVisibleObjects() {
		synchronized (newlyVisibleObjects)
		{
			return new HashSet<>(newlyVisibleObjects);
		}
	}

	public Set<WorldObject> getNewlyInvisibleObjects() {
		synchronized (newlyInvisibleObjects)
		{
			return new HashSet<>(newlyInvisibleObjects);
		}
	}

	public void setVisibleObjects(Collection<WorldObject> currentlyVisible) {
		synchronized (newlyVisibleObjects)
		{
			newlyVisibleObjects.clear();
			currentlyVisible.stream().
				  filter(o -> !getVisibleObjects().contains(o)).
				  forEach(newlyVisibleObjects::add);
		}
		synchronized (newlyInvisibleObjects)
		{
			newlyInvisibleObjects.clear();
			getVisibleObjects().stream().
				  filter(o -> !currentlyVisible.contains(o)).
				  forEach(newlyInvisibleObjects::add);
		}
		synchronized (visibleObjects)
		{
			visibleObjects.clear();
			visibleObjects.addAll(currentlyVisible);
		}
	}

	@Override
	public void onPreUpdate(World world, WorldObject worldObject) {
		Collection<WorldObject> objects = SightCalculator.getVisibleObjects(worldObject);
		setVisibleObjects(objects);
	}
}
