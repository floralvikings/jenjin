package com.jenjinstudios.world.actor;

import com.jenjinstudios.world.WorldObject;
import com.jenjinstudios.world.event.PreUpdateEvent;
import com.jenjinstudios.world.math.SightCalculator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Caleb Brinkman
 */
public class Vision implements PreUpdateEvent
{
	public static final String EVENT_NAME = "visionEvent";
	private transient final Set<WorldObject> visibleObjects = new HashSet<>();
	private transient final Set<WorldObject> newlyVisibleObjects = new HashSet<>();
	private transient final Set<WorldObject> newlyInvisibleObjects = new HashSet<>();
	private transient final WorldObject worldObject;

	public Vision(WorldObject worldObject) {
		this.worldObject = worldObject;
	}

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
	public void onPreUpdate() {
		Collection<WorldObject> objects = SightCalculator.getVisibleObjects(worldObject);
		setVisibleObjects(objects);
	}
}
